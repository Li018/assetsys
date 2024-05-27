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
 * @ClassName:Asset
 * @description: 资产管理 实体类
 * @author:li
 * @Version 3.0
 **/

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_asset")
@Schema(title = "Asset对象", description = "资产管理")
public class Asset implements Serializable{

private static final long serialVersionUID=1L;

        @Schema(title = "序列号")
                @TableId(value = "id", type = IdType.AUTO)
                @ExcelProperty("序列号")
private Long id;

        @Schema(title = "资产编号")
    @TableField("assetno")
        @ExcelProperty("资产编号")
private String assetno;

        @Schema(title = "资产型号")
    @TableField("assettypeno")
        @ExcelProperty("资产型号")
private String assettypeno;

        @Schema(title = "资产名")
    @TableField("assetname")
        @ExcelProperty("资产名")
private String assetname;

        @Schema(title = "资产标签")
    @TableField("assetlabel")
        @ExcelProperty("资产标签")
private String assetlabel;

    @Schema(title = "资产标签信息")
    @TableField(exist = false)
    @ExcelIgnore
    private Label label = new Label();

        @Schema(title = "资产描述")
    @TableField("assetdesc")
        @ExcelProperty("资产描述")
private String assetdesc;

        @Schema(title = "资产类别")
    @TableField("assetcategory")
        @ExcelProperty("资产类别")
private String assetcategory;

    @Schema(title = "资产类别信息")
    @TableField(exist = false)
    @ExcelIgnore
    private Category category = new Category();

        @Schema(title = "资产状态")
    @TableField("assetStatus")
        @ExcelProperty("资产状态")
private String assetStatus;

        @Schema(title = "加入时间")
    @TableField("registerTime")
        @ExcelProperty("加入时间")
private String registerTime;

        @Schema(title = "最近更新时间")
    @TableField("lastUpdatedTime")
        @ExcelProperty("最近更新时间")
private String lastUpdatedTime;

        @Schema(title = "资产所属组织")
    @TableField("belongOrg")
        @ExcelProperty("资产所属组织")
private String belongOrg;

    @Schema(title = "资产所属组织信息")
    @TableField(exist = false)
    @ExcelIgnore
    private Assetorg assetorg = new Assetorg();


    @Schema(title = "资产负责人")
    @TableField("assetUser")
        @ExcelProperty("资产负责人")
private String assetUser;

    @Schema(title = "资产负责人信息")
    @TableField(exist = false)
    @ExcelIgnore
    private User user = new User();

        @Schema(title = "资产网络ip")
    @TableField("ip")
        @ExcelProperty("资产网络ip")
private String ip;


        }
