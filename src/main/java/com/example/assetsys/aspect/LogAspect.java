package com.example.assetsys.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.example.assetsys.entity.Log;
import com.example.assetsys.annotations.IgnoreParam;
import com.example.assetsys.annotations.IgoreResult;
import com.example.assetsys.service.LogService;
import com.example.assetsys.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;


/**
 * @program: assetsys
 * @ClassName: LogAspect
 * @description: 日志注解拦截器
 * @author:li
 * @Version 3.0
 **/
@Component
@Aspect
public class LogAspect {
    @Resource
    private LogService logService;
    @Resource
    private UserService userService;

    @Around("execution(* com.example.assetsys.controller.*.*(..)) ")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 设置username用户名
        String username = null;




        // 获取方法名和参数
        String method = joinPoint.getSignature().getName();
        String params = null;
		// 检查是否有IgnoreParm忽略参数注解,如果没有将请求的参数转成数组
        if (!joinPoint.getTarget().getClass().getMethod(method, getParameterTypes(joinPoint)).isAnnotationPresent(IgnoreParam.class)){
            params= Arrays.toString(joinPoint.getArgs());
        }


        // 记录开始时间
        long startTime = System.currentTimeMillis();

        // 获取返回结果
        Object result =  joinPoint.proceed();;
        // 判断是否登录,如果登录设置用户名
        if (StpUtil.isLogin()) {
            username=userService.getUserInfo().getUsername();
        }
        // 记录结束时间
        long endTime = System.currentTimeMillis();

        // 计算执行时间
        long time = endTime - startTime;

        // 获取客户端IP地址
        String ip = this.getRequestIpAddress();
        // 获取请求方法类型
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String httpMethod = request.getMethod();
        // 创建日志记录对象
        Log logRecord = new Log();
        logRecord.setUsername(username);
        logRecord.setOperation(httpMethod);
        logRecord.setMethod(joinPoint.getSignature().getDeclaringTypeName() + "." + method + "()");
        logRecord.setParams(params);
        logRecord.setTaketime(String.valueOf(time));
        logRecord.setIp(ip);
//        判断该请求方法是否有包含IgoreResult注解，如果没有给logRecord对象设置result属性
       if (!joinPoint.getTarget().getClass().getMethod(method, getParameterTypes(joinPoint)).isAnnotationPresent(IgoreResult.class)){

           logRecord.setResult(String.valueOf(result));
        }


        logService.save(logRecord);
        // 将日志记录保存到数据库中

        // 返回结果
        return result;
    }

    /**
     * 获取客户端Ip地址
     * @return
     */
    private String getRequestIpAddress() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        // 从X-Forwarded-For中获取真实IP地址
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            // 如果X-Forwarded-For未设置，从其他头中获取
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            // 如果以上头信息都未设置，直接从请求中获取
            ip = request.getRemoteAddr();
        }

        // 多个代理时，取第一个IP地址
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    /**
     * 获取方法参数类型
     * @param joinPoint
     * @return
     */
    private Class<?>[] getParameterTypes(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getParameterTypes();
    }
}
