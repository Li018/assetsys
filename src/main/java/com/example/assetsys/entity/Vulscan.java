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
 * @ClassName:Vulscan
 * @description: 漏洞扫描 实体类
 * @author:li
 * @Version 3.0
 **/

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_vulscan")
@Schema(title = "Vulscan对象", description = "漏洞扫描")
public class Vulscan implements Serializable{

private static final long serialVersionUID=1L;

        @Schema(title = "序列号")
                @TableId(value = "id", type = IdType.AUTO)
                @ExcelProperty("序列号")
private Long id;

        @Schema(title = "扫描任务名称")
    @TableField("name")
        @ExcelProperty("扫描任务名称")
private String name;

        @Schema(title = "演练员")
    @TableField("tester")
        @ExcelProperty("演练员")
private String tester;

        @Schema(title = "关联任务")
    @TableField("relatertask")
        @ExcelProperty("关联任务")
private String relatertask;


    @Schema(title = "关联任务信息")
    @TableField(exist = false)
    @ExcelIgnore
    private  Mytask mytask = new Mytask();

        @Schema(title = "扫描策略")
    @TableField("scanpolicy")
        @ExcelProperty("扫描策略")
private String scanpolicy;

        @Schema(title = "扫描时间")
    @TableField("scantime")
        @ExcelProperty("扫描时间")
private String scantime;

        @Schema(title = "发现的漏洞")
    @TableField("foundvul")
        @ExcelProperty("发现的漏洞")
private String foundvul;

        @Schema(title = "漏洞编号")
    @TableField("vulno")
        @ExcelProperty("漏洞编号")
private String vulno;

        @Schema(title = "扫描工具")
    @TableField("scantools")
        @ExcelProperty("扫描工具")
private String scantools;

        @Schema(title = "等级")
    @TableField("risklevel")
        @ExcelProperty("等级")
private String risklevel;


        }
