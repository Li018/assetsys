package com.example.assetsys.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.example.assetsys.entity.dto.Meta;
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
 * @ClassName:Permission
 * @description: 菜单表 实体类
 * @author:li
 * @Version 3.0
 **/

@Data
@Accessors(chain = true)
@TableName("sys_permission")
@Schema(title = "Permission对象", description = "菜单表")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "序号")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(title = "父id")
    @TableField("parent_id")
    private Long parentId;

    @Schema(title = "访问路径")
    @TableField("path")
    private String path;

    @Schema(title = "组件路径")
    @TableField("component")
    private String component;

    @Schema(title = "图标")
    @TableField("icon")
    private String icon;

    @Schema(title = "标题")
    @TableField("title")
    private String title;

    @Schema(title = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(title = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(title = "当前状态")
    @TableField("statu")
    private Long statu;

    @Schema(title = "权限标识")
    @TableField("perms")
    private String perms;

    @Schema(title = "排序号")
    @TableField("order_num")
    private Integer orderNum;

    @Schema(title = "是否隐藏")
    @TableField("hidden")
    private Boolean hidden;

    @Schema(title = "菜单类型")
    @TableField("menu_type")
    private Byte menuType;
    @Schema(title = "菜单子级")
    @TableField(exist = false)
    private List<Permission> children = new ArrayList<>();
    @Schema(title = "路由封装")
    @TableField(exist = false)
    private Meta meta=new Meta();

}
