package com.example.assetsys.mapper;

import com.example.assetsys.entity.Asset;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 资产管理 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2024-01-05
 */
@Mapper
public interface AssetMapper extends BaseMapper<Asset> {

}
