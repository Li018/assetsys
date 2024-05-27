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
 * @ClassName:Assetorg
 * @description: 资产组织管理 实体类
 * @author:li
 * @Version 3.0
 **/

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_assetorg")
@Schema(title = "Assetorg对象", description = "资产组织管理")
public class Assetorg implements Serializable{

private static final long serialVersionUID=1L;

        @Schema(title = "序列号")
                @TableId(value = "id", type = IdType.AUTO)
                @ExcelProperty("序列号")
private Long id;

        @Schema(title = "组织名称")
    @TableField("orgname")
        @ExcelProperty("组织名称")
private String orgname;

        @Schema(title = "夫级组织")
    @TableField("fatherorg")
        @ExcelProperty("夫级组织")
private String fatherorg;


        }
