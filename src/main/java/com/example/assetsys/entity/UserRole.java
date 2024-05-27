package com.example.assetsys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;


/**
 * @program: assetsys
 * @ClassName:UserRole
 * @description: 用户角色表 实体类
 * @author:li
 * @Version 3.0
 **/

@Data
@Accessors(chain = true)
@TableName("sys_user_role")
@Schema(title = "UserRole对象", description = "用户角色表")

@NoArgsConstructor
public class UserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "序号")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(title = "用户id")
    @TableField("user_id")
    private Long userId;

    @Schema(title = "角色id")
    @TableField("role_id")
    private Long roleId;


}
