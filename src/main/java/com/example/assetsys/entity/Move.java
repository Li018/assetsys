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
 * @ClassName:Move
 * @description: 横向移动 实体类
 * @author:li
 * @Version 3.0
 **/

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_move")
@Schema(title = "Move对象", description = "横向移动")
public class Move implements Serializable{

private static final long serialVersionUID=1L;

        @Schema(title = "序列号")
                @TableId(value = "id", type = IdType.AUTO)
                @ExcelProperty("序列号")
private Long id;

        @Schema(title = "移动名称")
    @TableField("name")
        @ExcelProperty("移动名称")
private String name;

        @Schema(title = "测试员")
    @TableField("tester")
        @ExcelProperty("测试员")
private String tester;

        @Schema(title = "发生时间")
    @TableField("attacktime")
        @ExcelProperty("发生时间")
private String attacktime;

        @Schema(title = "目标IP")
    @TableField("targetip")
        @ExcelProperty("目标IP")
private String targetip;

        @Schema(title = "利用漏洞")
    @TableField("exploitvuls")
        @ExcelProperty("利用的漏洞")
private String exploitvuls;


    @Schema(title = "利用漏洞的信息")
    @TableField(exist = false)
    @ExcelIgnore
    private  Exploit exploit = new Exploit();

        @Schema(title = "横移路径")
    @TableField("movepath")
        @ExcelProperty("横移路径")
private String movepath;

        @Schema(title = "移动结果")
    @TableField("moveresult")
        @ExcelProperty("移动结果")
private String moveresult;

        @Schema(title = "执行时间")
    @TableField("movetime")
        @ExcelProperty("执行时间")
private String movetime;


        }
