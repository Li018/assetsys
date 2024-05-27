package com.example.assetsys.exception;

/**
 * @program: assetsys
 * @ClassName:EmailException
 * @description: 邮箱验证码异常
 * @author:li
 * @Version 3.0
 **/
public class EmailException extends RuntimeException {


    public EmailException(String e) {
        super(e);
    }
}