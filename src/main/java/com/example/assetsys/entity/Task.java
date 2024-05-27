package com.example.assetsys.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
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
 * @ClassName:Task
 * @description: 演练任务 实体类
 * @author:li
 * @Version 3.0
 **/

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_task")
@Schema(title = "Task对象", description = "演练任务")
public class Task implements Serializable{

private static final long serialVersionUID=1L;

        @Schema(title = "序列号")
                @TableId(value = "id", type = IdType.AUTO)
                @ExcelProperty("序列号")
private Long id;

        @Schema(title = "任务名称")
    @TableField("name")
        @ExcelProperty("任务名称")
private String name;

        @Schema(title = "任务描述")
    @TableField("taskdesc")
        @ExcelProperty("任务描述")
private String taskdesc;

        @Schema(title = "组织结构")
    @TableField("relatedorg")
        @ExcelProperty("组织结构")
private String relatedorg;
    @Schema(title = "所属组织结构信息")
    @TableField(exist = false)
    @ExcelIgnore
    private Assetorg assetorg = new Assetorg();


    @Schema(title = "参与人员")
    @TableField("joinedtesters")
        @ExcelProperty("参与人员")
private String joinedtesters;

    @Schema(title = "参与人员信息")
    @TableField(exist = false)
    @ExcelIgnore
    private  User user = new User();

        @Schema(title = "任务状态")
    @TableField("taskstatus")
        @ExcelProperty("任务状态")
private String taskstatus;

        @Schema(title = "任务漏洞")
    @TableField("taskvul")
        @ExcelProperty("任务漏洞")
private String taskvul;

        @Schema(title = "开始时间")
    @TableField("starttime")
        @ExcelProperty("开始时间")
private String starttime;

        @Schema(title = "结束时间")
    @TableField("endtime")
        @ExcelProperty("结束时间")
private String endtime;


        }
