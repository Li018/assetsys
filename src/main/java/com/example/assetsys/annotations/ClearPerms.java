package com.example.assetsys.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: assetsys
 * @ClassName: ClearPerms
 * @description: 清除权限
 * @author:li
 * @Version 3.0
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClearPerms {
	// 设置一个值属性，当值等于false的时候，清除全部用户的缓存权限，当值等于true的时候，清除当前用户的缓存权限
    boolean value() default false;

}
