package com.example.assetsys.common;

import lombok.Data;

import java.io.Serializable;


/**
 * @program: assetsys
 * @ClassName: Constant
 * @description: 统一封装返回体
 * @author:li
 * @Version 3.0
 **/
@Data
public class Result implements Serializable {
    private static final long serialVersionUID = 1L;
//    状态码
    private int code;
//    提示信息message
    private String msg;
//    返回的属性类型
    private Object data;

    /**
     * 直接返回成功结果
     * @param data
     * @return
     */
    public static Result success(Object data) {
        return success(200, "操作成功", data);
    }

    /**
     * 自定义返回成功结果
     * @param code
     * @param msg
     * @param data
     * @return
     */
    public static Result success(int code, String msg, Object data) {
        Result r = new Result();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    /**
     * 不带结果直接返回成功
     * @return
     */
    public static Result success() {
        Result r = new Result();
        r.setCode(200);
        r.setMsg("操作成功");
        return r;
    }

    /**
     * 直接返回失败信息
     * @return
     */
    public static Result error() {
        return error(400, "操作失败", null);
    }

    /**
     * 带参数返回失败信息
     * @param msg
     * @return
     */
    public static Result error(String msg) {
        return error(400, msg, null);
    }

    /**
     * 自定义返回失败信息
     * @param code
     * @param msg
     * @param data
     * @return
     */
    public static Result error(int code, String msg, Object data) {
        Result r = new Result();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }
}