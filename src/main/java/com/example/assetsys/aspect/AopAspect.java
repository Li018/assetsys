package com.example.assetsys.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.example.assetsys.common.Constant;
import com.example.assetsys.annotations.ClearPerms;
import com.example.assetsys.service.UserService;
import com.example.assetsys.utils.RedisUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;


/**
 * @program: assetsys
 * @ClassName: AopAspect
 * @description: AopAspect自定义注解拦截器
 * @author:li
 * @Version 3.0
 **/
@Slf4j
@Aspect
@Component
public class AopAspect {
    @Resource
    RedisUtils redisUtils;
    @Resource
    private UserService userService;
	// 在执行有ClearPerms注解之前执行
    @Before("@annotation(clearPerms)")
    public void ClearPermissionRedis(JoinPoint point, ClearPerms clearPerms) {
       // 判断value的值是否为true
        if (clearPerms.value()==true){
			//如果为true，清除掉当前角色信息缓存，权限信息缓存，路由权限缓存，在线用户权限缓存
            log.warn("清除{}角色权限缓存成功",userService.getUserInfo().getUsername());
            redisUtils.hdel(Constant.ROLE_PREFIX, (String) StpUtil.getLoginId());
            log.warn("清除{}权限缓存成功",userService.getUserInfo().getUsername());
            redisUtils.hdel(Constant.PERMISSION_PREFIX, (String) StpUtil.getLoginId());
            log.warn("清除{}路由缓存成功",userService.getUserInfo().getUsername());
            redisUtils.hdel(Constant.ROUTERS_PREFIX,(String) StpUtil.getLoginId());
            log.warn("清除{}在线用户缓存成功",userService.getUserInfo().getUsername());
            redisUtils.hdel(Constant.ONLINE_PREFIX, String.valueOf(StpUtil.getLoginId()));

        }else{
			// 如果是false 那么则清除全部的角色、权限、路由、在线用户缓存信息
            log.info("清除全部角色权限缓存成功！");
            redisUtils.del(Constant.ROLE_PREFIX);
            log.info("清除全部权限缓存成功！");
            redisUtils.del(Constant.PERMISSION_PREFIX);
            log.info("清除全部路由缓存成功！");
            redisUtils.del(Constant.ROUTERS_PREFIX);
            log.info("清除全部在线用户缓存成功！");
            redisUtils.del(Constant.ONLINE_PREFIX);
        }


    }


}