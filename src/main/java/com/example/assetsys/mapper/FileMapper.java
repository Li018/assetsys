package com.example.assetsys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.assetsys.entity.Files;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 文件上传 Mapper 接口
 * </p>
 *
 * @author
 * @since 2023-02-01
 */
@Mapper
public interface FileMapper extends BaseMapper<Files> {

}
