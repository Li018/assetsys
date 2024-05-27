package com.example.assetsys.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @program: assetsys3
 * @ClassName:GlobalMetaObjectHandler
 * @description: 全局自动填充处理器
 * @author:li
 * @Version 3.0
 **/
@Component
@Slf4j
public class GlobalMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入时候的填充策略
     *
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill");
        //添加时候自动填充 setFieldValByName三个参数为：映射类字段，填充值，原对象
        // setFieldValByName(String fieldName, Object fieldVal, MetaObject metaObject)
        //给createTime这个字段和updateTime这俩字段 来一个什么值呢 来一个自动插入时间 传一个数据 这个数据就是mataObject
        this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);

    }

    /**
     * 更新时候的填充策略
     *
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill");
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);

    }
}