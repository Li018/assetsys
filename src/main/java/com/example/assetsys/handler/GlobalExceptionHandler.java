package com.example.assetsys.handler;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.util.SaResult;
import com.example.assetsys.common.Result;
import com.example.assetsys.exception.EmailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import cn.hutool.core.io.IORuntimeException;
/**
 * @program: assetsys3
 * @ClassName:GlobalExceptionHandler
 * @description: 全局异常拦截
 * @author:li
 * @Version 3.0
 **/
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(SaTokenException.class)
    public SaResult handlerSaTokenException(SaTokenException e) {

        if (e.getCode()>=11041  && e.getCode() <= 11051) {
            SaHolder.getResponse().setStatus(HttpStatus.FORBIDDEN.value());
            return SaResult.error("暂无权限！");
        }

        // 更多 code 码判断 ...
        SaHolder.getResponse().setStatus(e.getCode());
        // 默认的提示
        return SaResult.error(e.getMessage());
    }
    @ExceptionHandler(IORuntimeException.class)
    public void handlerIORuntimeException(IORuntimeException e){
        log.error("文件找不到");
    }
    @ExceptionHandler(EmailException.class)
    public Result handlerEmailException(EmailException e){
        log.error(e.getMessage());
        return Result.error(e.getMessage());

    }
    @ExceptionHandler(RuntimeException.class)
    public Result handlerRuntimeException(RuntimeException e){
        log.error(e.getMessage());
        e.printStackTrace();
        SaHolder.getResponse().setStatus(HttpStatus.BAD_REQUEST.value());
        return Result.error(e.getMessage());
    }


}