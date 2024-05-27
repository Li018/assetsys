package com.example.assetsys.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * @program: assetsys
 * @ClassName:Notice
 * @description: 系统公告 实体类
 * @author:li
 * @Version 3.0
 **/

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_notice")
@Schema(title = "Notice对象", description = "系统公告")
public class Notice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "序列号")
    @TableId(value = "id", type = IdType.AUTO)
    @ExcelProperty("序列号")
    private Long id;

    @Schema(title = "标题")
    @TableField("name")
    @ExcelProperty("标题")
    private String name;

    @Schema(title = "内容")
    @TableField("content")
    @ExcelProperty("内容")
    private String content;

    @Schema(title = "创建时间")
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;


}
