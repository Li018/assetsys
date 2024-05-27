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
 * @ClassName:Role
 * @description: 角色表 实体类
 * @author:li
 * @Version 3.0
 **/

@Data
@TableName("sys_role")
@Accessors(chain = true)
@Schema(title = "Role对象", description = "角色表")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "序号")
    @ExcelProperty("序号")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(title = "角色名称")
    @ExcelProperty("角色名称")
    @TableField("name")
    private String name;

    @Schema(title = "描述")
    @ExcelProperty("描述")
    @TableField("description")
    private String description;

    @Schema(title = "唯一标识")
    @ExcelProperty("唯一标识")
    @TableField("perms")
    private String perms;

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
    private Integer statu;

    @Schema(title = "权限编号")
    @TableField(exist = false)
    @ExcelIgnore
    private List<Long> permissionIds=new ArrayList<>();


}
