package com.example.assetsys.mapper;

import com.example.assetsys.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2023-09-30
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
