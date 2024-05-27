package com.example.assetsys.mapper;

import com.example.assetsys.entity.Task;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 演练任务 Mapper 接口
 * </p>
 *
 * @author baomidou
 * @since 2024-01-05
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {

}
