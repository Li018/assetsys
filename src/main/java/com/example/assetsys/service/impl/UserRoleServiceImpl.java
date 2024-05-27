package com.example.assetsys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.assetsys.entity.UserRole;
import com.example.assetsys.mapper.UserRoleMapper;
import com.example.assetsys.service.UserRoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @program: assetsys
 * @ClassName:UserRoleServiceImpl
 * @description: 用户角色表 service实现类
 * @author:li
 * @Version 3.0
 **/
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
    /**
     * 获取roleIds
     *
     * @param loginId
     * @param loginType
     * @return
     */
    @Override
    public List<Long> getRoleIds(Object loginId, String loginType) {

        //        基于id获取userolelist
        LambdaQueryWrapper<UserRole> userRoleLambdaQueryWrapper = new LambdaQueryWrapper<UserRole>().in(UserRole::getUserId, loginId);
//        基于userolelist转换成roleIds
        return this.list(userRoleLambdaQueryWrapper).stream().map(UserRole::getRoleId).collect(Collectors.toList());
    }

}
