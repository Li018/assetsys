package com.example.assetsys.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.assetsys.common.Constant;
import com.example.assetsys.common.Result;
import com.example.assetsys.entity.User;
import com.example.assetsys.entity.UserRole;
import com.example.assetsys.entity.dto.LoginDto;
import com.example.assetsys.entity.dto.MailBean;
import com.example.assetsys.entity.dto.RegisterDto;
import com.example.assetsys.annotations.ClearPerms;
import com.example.assetsys.annotations.IgnoreParam;
import com.example.assetsys.annotations.IgoreResult;
import com.example.assetsys.exception.EmailException;
import com.example.assetsys.service.PermissionService;
import com.example.assetsys.service.UserRoleService;
import com.example.assetsys.service.UserService;
import com.example.assetsys.utils.MailUtil;
import com.example.assetsys.utils.RedisUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * @program: assetsys
 * @ClassName:AuthController
 * @description: 认证处理控制器
 * @author:li
 * @Version 3.0
 **/
@Tag(name = "认证授权 前端控制器")
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
//    注入UserService属性
    @Resource
    private UserService userService;
//    注入密码加密PassWordEncoder属性
    @Resource
    private PasswordEncoder passwordEncoder;
//    注入PermissionServcice属性
    @Resource
    private PermissionService permissionService;
//    注入StpInterface属性
    @Resource
    private StpInterface stpInterface;
//    注入reidisUtils属性
    @Resource
    private RedisUtils redisUtils;
//    注入UserRoleService属性
    @Resource
    private UserRoleService userRoleService;
//   注入MailUtil属性
    @Resource
    private MailUtil mailUtil;


    /**
     * 登录接口
     *
     * @param
     * @return
     */
    @PostMapping("/login")
    @IgnoreParam
    @IgoreResult
    public SaResult doLogin(@RequestBody LoginDto loginDto) {
		// 根据用户名进行查询查库
        User queryUser = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, loginDto.getUsername()));
        // 获取
		String code = String.valueOf(redisUtils.hget(Constant.CAPTCHA_PREFIX, loginDto.getToken()));
        redisUtils.hdel(Constant.CAPTCHA_PREFIX, loginDto.getToken());
        if (!code.equals(loginDto.getCode())) {
            return SaResult.error("验证码错误！");
        }


        if (ObjectUtil.isEmpty(queryUser) || !passwordEncoder.matches(loginDto.getPassword(), queryUser.getPassword())) {
            return SaResult.error("账户或者密码错误");
        }
        if (queryUser.getStatu() == 0L) {
            return SaResult.error("账户已经被封禁，请联系系统管理员！");
        }
        // 第1步，先登录上，使用Sa-token官方的工具类登录，后台的令牌就会通过tokenInfo
		// 返回到前端
        StpUtil.login(queryUser.getId(), "PC");
        // 第2步，获取 Token  相关参数
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        // 第3步，返回给前端
        return SaResult.data(tokenInfo);

    }


    /**
     * // 查询登录状态，浏览器访问： http://localhost:8081/user/isLogin69+
     *
     * @return
     */
    @GetMapping("/islogin")
    @ClearPerms

    public String isLogin() {
        System.out.println(StpUtil.getLoginId());
        User user = userService.getUserInfo();
        log.warn(JSONUtil.toJsonStr(user));
        return "当前会话是否登录：" + StpUtil.isLogin();
    }

    // 查询 Token 信息  ---- http://localhost:8081/acc/tokenInfo
    @GetMapping("/tokenInfo")
    @ClearPerms(value = true)
    public SaResult tokenInfo() {
        log.warn(JSONUtil.toJsonStr(StpUtil.getTokenInfo()));
        return SaResult.data(StpUtil.getTokenInfo());
    }

    /**
     * 获取认证信息
     * userInfo
     * rolePerms
     * PermissionPerms
     * 用于前端显示自己的个人信息
     * v-permmission指令需要的数据
     * //todo role暂时待定使用
     *
     * @return
     */
    @GetMapping("/authInfo")
    public SaResult getPerms() {
		// 获取当前的用户信息
		// 获取用户的权限信息
		// 获取用户的角色信息
		// 获取用户的路由信息
		
        return SaResult.data(
                MapUtil.builder().put("rolePerms", StpUtil.getRoleList())
                        .put("permissionPerms", StpUtil.getPermissionList())
                        .put("userInfo", userService.getUserInfo())
                        .put("routers", permissionService.buildTrees(permissionService.getRouters(StpUtil.getLoginId(), "PC")))
                        .build()
        );
    }


    /**
     * 退出登录
     *
     * @return
     */
    @PostMapping("/logout")
    @ClearPerms(value = true)
    public SaResult logout() {
		// 使用sa-token中的工具类进行退出
        StpUtil.logout();
		// 这个时候就已经退出登录，前端再次发送请求就会报401的状态码
        return SaResult.ok();
    }

    /**
     * 获取验证码
     * 用于登录页面的使用
     *
     * @return
     */
    @GetMapping("/captcha")
    public Result getCaptcha() {

        // 设置key
        String key = UUID.randomUUID().toString();
        //定义图形验证码的长、宽、验证码字符数、干扰线宽度
        ShearCaptcha shearCaptcha = CaptchaUtil.createShearCaptcha(150, 40, 5, 0);
        // 存入redis并设置过期时间为30分钟
        //captcha:d6e561a6-7929-4469-8154-008710932f61
        String base64String = "";
        try {
            base64String = "data:image/png;base64," + shearCaptcha.getImageBase64();

        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * 放入redis缓存之中
         */
        redisUtils.hset(Constant.CAPTCHA_PREFIX, key, shearCaptcha.getCode(), 120);

        return Result.success(
                MapUtil.builder()
                        .put("token", key)
                        .put("captchaImg", base64String)
                        .build()

        );


    }

    /**
     * 重置密码
     *
     * @param passwordMap
     * @return
     */
    @PostMapping("/repassword")
//    在此处就需要清除当前用户的缓存信息
    @ClearPerms(value = true)
    public Result repassWord(@RequestBody HashMap<String, String> passwordMap) {
       // 获取当前用户
		User user = userService.getUserInfo();
//        和原密码进行校验
        if (!passwordEncoder.matches(passwordMap.get("oldpass"), user.getPassword())) {
            return Result.error("旧密码输入错误！");
        }
//        判断前端两次输入的密码是否一致
        if (!passwordMap.get("password").equals(passwordMap.get("checkPass"))) {
            return Result.error("两次密码不一致");
        }
//      没有问题就可以设置密码
        user.setPassword(passwordEncoder.encode(passwordMap.get("password")));
// 		保存返回

        return Result.success(userService.saveOrUpdate(user));
    }

    /**
     * 注册用户
     * @param registerDto
     * @return
     */
    @PostMapping("/register")
    public Result register(@RequestBody RegisterDto registerDto) {
        //通过token传递过来的key
        String code = String.valueOf(redisUtils.hget(Constant.CAPTCHA_PREFIX, registerDto.getToken()));
        //删除掉redis缓存中这条记录
        redisUtils.hdel(Constant.CAPTCHA_PREFIX, registerDto.getToken());
        if (!code.equals(registerDto.getCode())) {
            return Result.error("验证码错误！");
        }
//        构造创建构造条件基于username进行查库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, registerDto.getUsername());

        List<User> users = userService.list(queryWrapper);
//        判断一下拥有该名的用户是否大于0，如果大于0则返回到前端，提示用户进行更换
        if (users.size() > 0) {
            return Result.error("账户名已经存在,请更换");
        }
//        再通过邮箱进行查库
        LambdaQueryWrapper<User>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        List<User>userList=userService.list(lambdaQueryWrapper.eq(User::getEmail,registerDto.getEmail()));
//       如果拥有该邮箱的记录大于0，抛出异常
         if (userList.size()>0){
            throw new  EmailException("邮箱已经被其他用户使用，请您更换！");
        }
//         如果没有的话那么则进行保存，需要注意密码加密
        else {
            User user = new User().setPassword(passwordEncoder.encode(registerDto.getPassword())).setUsername(registerDto.getUsername())
                    .setAvatar(registerDto.getAvatar())
                    .setNickname(registerDto.getNickname())
                    .setPhone(registerDto.getPhone())
                    .setEmail(registerDto.getEmail())
                    .setStatu(1L);
            userService.saveOrUpdate(user);
//            对用户的关联表也需要保存一条用户和角色的关联数据
            UserRole userRole = new UserRole().setRoleId(Constant.DEFAULT_ROLE)
                    .setUserId(user.getId());
            userRoleService.saveOrUpdate(userRole);

        }
        return Result.success();
    }

    /**
     * 发送邮箱验证码
     * @param username
     * @return
     * @throws MessagingException
     */
    @GetMapping("/email/{username}")
    public Result getEmailCode(@PathVariable String username) throws MessagingException {
//        进行查库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
//        找出用户名是否存在
        queryWrapper.eq(User::getUsername, username);
        User user = userService.getOne(queryWrapper);
//        如果该名用户为空，则向前端抛出异常
        if (user == null) {
            throw new EmailException("账户名错误，请核对重新输入！");
        }
//        如果查到用户了，但是没有绑定邮箱，那么，则给前端抛出异常进行提示
        if ("".equals(user.getEmail())) {
            throw new EmailException("您的邮箱为空，请联系管理员重置密码！");
        }
//        判断在5分钟内是否已经发送过邮箱验证码，如果发送过就不再重复发送

        if (redisUtils.hget(Constant.EMAIL_PREFIX,user.getUsername())!=null){
            throw new EmailException("验证码已经发送到您的邮箱，请勿重复发送邮箱验证码！");
        }
//        生成随机5位数字
        String code= RandomUtil.randomNumbers(5);
//      创建MailBean对象
        MailBean mailBean = new MailBean();
        mailBean.setRecipient(user.getEmail());
        mailBean.setSubject("assetsys_找回密码");
        mailBean.setContent("重置密码的邮箱验证码为" +code+"，请在5分钟内使用！如非您本人操作，请不要泄漏。"+ DateUtil.now());
//       通过MailUtil 进行发送
        mailUtil.sendSimpleMail(mailBean);
//        将生成的5位数字验证码存储到缓存之中
        redisUtils.hset(Constant.EMAIL_PREFIX,user.getUsername(),code,300
        );
//      返回
        return Result.success();
    }
    @PostMapping("/findpassword")
    public Result findPassWord(@RequestBody RegisterDto registerDto){
//        构造条件
        LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername,registerDto.getUsername());
//        基于username构造条件进行查库
        User user=userService.getOne(lambdaQueryWrapper);
//        如果为空就抛出异常提示用户，账户名有误
        if (user==null){
            return Result.error("账户名有误，请重新输入！");
        }
//        通过redis缓存判断是否已经发送出验证码
        if (redisUtils.hget(Constant.EMAIL_PREFIX,user.getUsername())==null){
            throw new EmailException("邮箱验证码为空，请点击获取邮箱验证码按钮获取邮箱验证码！");
        }
//        通过redis缓存判断前端输入的和后端发送的一致，如果不一致，让用户重新输入
        if (!redisUtils.hget(Constant.EMAIL_PREFIX,user.getUsername()).toString().equals(registerDto.getCode())){
            throw new EmailException("邮箱验证码错误，请重新输入！");
        }
//        密码加密
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
//        删除掉该名用户在redis中的缓存数据
        redisUtils.hdel(Constant.EMAIL_PREFIX,user.getUsername());
//        保存并且返回成功
        return Result.success(userService.saveOrUpdate(user));
    }

}