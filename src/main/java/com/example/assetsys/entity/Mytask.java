package com.example.assetsys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

    import lombok.experimental.Accessors;


/**
 * @program: assetsys
 * @ClassName:Mytask
 * @description: 我的任务管理 实体类
 * @author:li
 * @Version 3.0
 **/

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_mytask")
@Schema(title = "Mytask对象", description = "我的任务管理")
public class Mytask implements Serializable{

private static final long serialVersionUID=1L;

        @Schema(title = "序列号")
                @TableId(value = "id", type = IdType.AUTO)
                @ExcelProperty("序列号")
private Long id;

        @Schema(title = "用户id")
    @TableField("userid")
        @ExcelProperty("用户id")
private String userid;

        @Schema(title = "任务id")
    @TableField("taskid")
        @ExcelProperty("任务id")
private String taskid;

        @Schema(title = "任务状态")
    @TableField("finishstatus")
        @ExcelProperty("任务状态")
private String finishstatus;

        @Schema(title = "完成时间")
    @TableField("finishtime")
        @ExcelProperty("完成时间")
private String finishtime;

        @Schema(title = "漏洞扫描个数")
    @TableField("submitscannum")
        @ExcelProperty("漏洞扫描个数")
private String submitscannum;

        @Schema(title = "漏洞利用个数")
    @TableField("submitexploitnum")
        @ExcelProperty("漏洞利用个数")
private String submitexploitnum;

        @Schema(title = "横向移动个数")
    @TableField("submitmovenum")
        @ExcelProperty("横向移动个数")
private String submitmovenum;

    @Schema(title = "状态")
    @TableField("taskstatus")
    @ExcelProperty("状态")
        private String taskstatus;

    @Schema(title = "任务名称")
    @TableField("taskname")
    @ExcelProperty("任务名称")
    private String taskname;

        }
