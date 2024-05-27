package com.example.assetsys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @program: assetsys
 * @ClassName 文件上传 实体类
 * @description:
 * @author: dyy
 * @Version 3.0
 **/
@Data
@Accessors(chain = true)
@TableName("sys_file")
public class Files implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 序号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件名称
     */
    private String name;

    /**
     * 文件类型
     */
    private String type;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 是否禁用
     */
    private Boolean enable;

    private String url;

    private String md5;

    private Boolean isDelete;


}
