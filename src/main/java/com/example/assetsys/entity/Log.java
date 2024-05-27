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
 * @ClassName:Log
 * @description: 操作日志 实体类
 * @author:li
 * @Version 3.0
 **/

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_log")
@Schema(title = "Log对象", description = "操作日志")
public class Log implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(title = "序列号")
    @TableId(value = "id", type = IdType.AUTO)
    @ExcelProperty("序列号")
    private Long id;

    @Schema(title = "操作用户")
    @TableField("username")
    @ExcelProperty("操作用户")
    private String username;

    @Schema(title = "操作")
    @TableField("operation")
    @ExcelProperty("操作")
    private String operation;

    @Schema(title = "方法名称")
    @TableField("method")
    @ExcelProperty("方法名称")
    private String method;

    @Schema(title = "参数")
    @TableField("params")
    @ExcelProperty("参数")
    private String params;

    @Schema(title = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @ExcelProperty("创建时间")
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(title = "Ip")
    @TableField("ip")
    @ExcelProperty("Ip")
    private String ip;

    @Schema(title = "返回结果")
    @TableField("result")
    @ExcelProperty("返回结果")
    private String result;

    @Schema(title = "耗时")
    @TableField("taketime")
    @ExcelProperty("耗时")
    private String taketime;


}
