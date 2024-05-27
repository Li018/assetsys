package com.example.assetsys.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * @program: assetsys
 * @ClassName:User
 * @description: 用户表 实体类
 * @author:li
 * @Version 3.0
 **/

@Data
@Accessors(chain = true)
@TableName("sys_user")
@Schema(title = "User对象", description = "用户表")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "序号")
    @ExcelProperty("序号")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(title = "账户名")
    @ExcelProperty("账户名")
    @TableField("username")
    private String username;

    @Schema(title = "密码")
    @ExcelIgnore
    @TableField("password")
    private String password;


    @Schema(title = "昵称")
    @ExcelProperty("昵称")
    @TableField("nickname")
    private String nickname;

    @Schema(title = "头像")
    @ExcelProperty("头像")
    @TableField("avatar")
    private String avatar;
    @Schema(title = "手机号")
    @ExcelProperty("手机号")
    @TableField("phone")
    private String phone;
    @Schema(title = "邮箱")
    @ExcelProperty("邮箱")
    @TableField("email")
    private String email;
    @Schema(title = "个人简介")
    @ExcelProperty("个人简介")
    @TableField("content")
    private String content;
    @Schema(title = "创建时间")
    @ExcelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(title = "更新时间")
    @ExcelProperty("更新时间")
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(title = "当前状态")
    @ExcelProperty("当前状态")
    @TableField("statu")
    private Long statu;
    @Schema(title = "角色编号")
    @ExcelIgnore
    @TableField(exist = false)
    private List<Long> roleIds=new ArrayList<>();


}
