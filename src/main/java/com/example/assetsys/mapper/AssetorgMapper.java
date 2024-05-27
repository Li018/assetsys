package com.example.assetsys.mapper;

import com.example.assetsys.entity.Assetorg;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 资产组织管理 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2024-01-05
 */
@Mapper
public interface AssetorgMapper extends BaseMapper<Assetorg> {
    List<Assetorg> selectDescendantsById(@Param("id") String id);

}
