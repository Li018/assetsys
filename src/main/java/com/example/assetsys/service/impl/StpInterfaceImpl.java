package com.example.assetsys.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.assetsys.common.Constant;
import com.example.assetsys.entity.Permission;
import com.example.assetsys.entity.Role;
import com.example.assetsys.entity.RolePermission;
import com.example.assetsys.service.*;
import com.example.assetsys.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: assetsys3
 * @ClassName:StpInterfaceImpl
 * @description: 自定义权限加载接口实现类
 * @author:li
 * @Version 3.0
 **/
@Component    // 保证此类被 SpringBoot 扫描，完成 Sa-Token 的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private UserService userService;
    @Resource
    private RoleService roleService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private RolePermissionService rolePermissionService;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 返回一个账号所拥有的权限码集合
     *
     * @param loginId
     * @param loginType
     * @return
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        //        先从redis中进行获取，获取不到再去数据库中获取
        if (ObjectUtil.isNotEmpty(redisUtils.hget(Constant.PERMISSION_PREFIX, String.valueOf(loginId)))) {
            System.out.println("从redis中进行获取permissionPermsList");
            return (List<String>) redisUtils.hget(Constant.PERMISSION_PREFIX, String.valueOf(loginId));
        }

        List<Long> userRoleIds = userRoleService.getRoleIds(loginId, loginType);
        if (userRoleIds.size() == 0) {
            return new ArrayList<>();
        }
        List<Role> roles = roleService.list(new LambdaQueryWrapper<Role>().in(Role::getId, userRoleIds).eq(Role::getStatu, 1L));
        if (roles.size() == 0) {
            return new ArrayList<>();
        }
        //        构造条件
        LambdaQueryWrapper<RolePermission> rolePermissionLambdaQueryWrapper = new LambdaQueryWrapper<RolePermission>().in(RolePermission::getRoleId, roles.stream().map(Role::getId).collect(Collectors.toList()));

        //     基于roleIds获取permissionIds
        List<Long> permissionIds = rolePermissionService.list(rolePermissionLambdaQueryWrapper).stream().map(RolePermission::getPermissionId).collect(Collectors.toList());
        if (permissionIds.size() == 0) {
            return new ArrayList<>();
        }
        //    基于permissionIds获取permissionPermslist进行返回
        List<String> permissionPermslist = permissionService.listByIds(permissionIds).stream().filter(permission -> permission.getStatu() == 1L).map(Permission::getPerms).collect(Collectors.toList());
        //        从数据库中进行获取判断非空以后再存储到redis中，防止缓存击穿
        if (ObjectUtil.isNotEmpty(permissionPermslist)) {

            redisUtils.hset(Constant.PERMISSION_PREFIX, (String) loginId, permissionPermslist, 2592000);
        }
        return permissionPermslist;

    }

    /**
     * 返回一个账号所拥有的角色标识集合
     *
     * @param loginId
     * @param loginType
     * @return
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {

//        先从redis中进行获取，获取不到再去数据库中获取
        if (ObjectUtil.isNotEmpty(redisUtils.hget(Constant.ROLE_PREFIX, loginId.toString()))) {
            System.out.println("获取到了缓存数据");
            return (List<String>) redisUtils.hget(Constant.ROLE_PREFIX, loginId.toString());
        }
//        从数据库中进行获取判断非空以后再存储到redis中，防止缓存击穿
        List<Long> userRoleIds = userRoleService.getRoleIds(loginId, loginType);
        if (userRoleIds.size() == 0) {
            return new ArrayList<>();
        }
        List<String> rolePermsList =
                roleService.list(new LambdaQueryWrapper<Role>()
                                .in(Role::getId, userRoleIds)
                                .eq(Role::getStatu, 1L))
                        .stream().map(Role::getPerms)
                        .collect(Collectors.toList());
        if (ObjectUtil.isNotEmpty(rolePermsList)) {
            redisUtils.hset(Constant.ROLE_PREFIX, (String) loginId, rolePermsList, 2592000);
        }
        return rolePermsList;
    }





}