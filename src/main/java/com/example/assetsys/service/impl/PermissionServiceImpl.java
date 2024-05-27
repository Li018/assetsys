package com.example.assetsys.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.assetsys.common.Constant;
import com.example.assetsys.entity.Permission;
import com.example.assetsys.entity.Role;
import com.example.assetsys.entity.RolePermission;
import com.example.assetsys.entity.dto.Meta;
import com.example.assetsys.mapper.PermissionMapper;
import com.example.assetsys.service.PermissionService;
import com.example.assetsys.service.RolePermissionService;
import com.example.assetsys.service.RoleService;
import com.example.assetsys.service.UserRoleService;
import com.example.assetsys.utils.RedisUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @program: assetsys
 * @ClassName:PermissionServiceImpl
 * @description: 菜单表 service实现类
 * @author:li
 * @Version 3.0
 **/
@Service
@Slf4j
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
//    注入RolePermissionService属性
    @Resource
    private RolePermissionService rolePermissionService;
//    注入UserRoleService属性
    @Resource
    private UserRoleService userRoleService;
//    注入RedisUtils属性
    @Resource
    private RedisUtils redisUtils;
//    注入RoleService属性
    @Resource
    private RoleService roleService;

    @Override

    /**
     * 双重for循环构建树形结构
     *
     * @param permissions
     * @return
     */
    public List<Permission> buildTrees(List<Permission> permissions) {
//      创建一个空的Permission集合
        List<Permission> finalyPermissions = new ArrayList<>();
//      进行第一层for循环
        for (Permission firstPermission : permissions) {
//      进行第二层for循环
            for (Permission secondPermission : permissions) {
//                当第二层循环的permission的ParentId等于第一层的Id时，将第二层的Permission添加到第一层Permission的Children集合属性里面
                if (secondPermission.getParentId().equals(firstPermission.getId())) {
                    firstPermission.getChildren().add(secondPermission);
                }

            }
//            如果第一层的ParentId=0，也就是说第一层上面没有父级的时候，将这个permission添加到初始创建的那个permission集合中
            if (firstPermission.getParentId() == 0L) {
                finalyPermissions.add(firstPermission);
            }
        }
//         返回finalyPermisisons
        return finalyPermissions;
    }

    /**
     * 获取用户的路由和菜单信息
     * 用于前端添加动态路由,菜单的展示
     *
     * @param loginId
     * @param loginType
     * @return
     */
    @Override
    public List<Permission> getRouters(Object loginId, String loginType) {

        List<Permission> routers = (List<Permission>) redisUtils.hget(Constant.ROUTERS_PREFIX, String.valueOf(loginId));
        if (ObjectUtil.isNotEmpty(routers)) {
            return routers;
        } else {

            //        通过UserRoleService获取到该名User的RoleID集合
            List<Long> roleIds = userRoleService.getRoleIds(loginId, loginType);
            //         通过RoleService找到Id在ROleID集合中且Statu=1L的Role集合
            if (roleIds.size()==0){
                return new ArrayList<>();
            }
            List<Role> roles = roleService.list(new LambdaQueryWrapper<Role>().in(Role::getId, roleIds).eq(Role::getStatu, 1L));
//              如果roles的长度等于0直接返回一个空的集合
            if (roles.size() == 0) {
                return new ArrayList<>();
            }
            //      创建Permission的构造条件，将roles的ID集合遍历出来，
            LambdaQueryWrapper<RolePermission> rolePermissionLambdaQueryWrapper = new LambdaQueryWrapper<RolePermission>().in(RolePermission::getRoleId, roles.stream().map(Role::getId).collect(Collectors.toList()));

            //     基于构造条件获取当前用户角色权限关联表的权限ID集合
            List<Long> permissionIds = rolePermissionService.list(rolePermissionLambdaQueryWrapper).stream().map(RolePermission::getPermissionId).collect(Collectors.toList());
//            如果 关联表的权限ID集合的长度为0 ，则也返回一个空集合
            if (permissionIds.size() == 0) {
                return new ArrayList<>();
            }
            /**
             * 基于permissionIds获取permissionPermslist进行返回,
             * 加入构造条件 为Statu=1,而且MenuType=1或者MenuType=2，
             * 也就是说，权限的类型为目录或者菜单，因为路由数据只需要这两种菜单数据类型
             */
            List<Permission> routersByDataSource = this.listByIds(permissionIds).stream().filter(permission ->
                    permission.getStatu() == 1L && (permission.getStatu() == 1 && (permission.getMenuType() == 1 || permission.getMenuType() == 2))
            ).distinct().sorted(Comparator.comparing(Permission::getOrderNum, Comparator.reverseOrder()).reversed()).collect(Collectors.toList());

            /**
             * 将路由集合进行一下转换，转化为前端需要的方式，
             * 也就是将部分属性，加入到meta对象属性中去，
             * 这样子再前端就可以直接使用不用再次转化了
             */
            routersByDataSource.forEach(r -> r.setMeta(new Meta(r.getIcon(), r.getTitle(), r.getHidden())));
            /**
             * 判断redis缓存中是否有有该名用户的缓存数据，如果没有的话，添加到缓存中去。
             */
            if (ObjectUtil.isNotEmpty(routersByDataSource)) {
                redisUtils.hset(Constant.ROUTERS_PREFIX, (String) loginId, routersByDataSource);
            }
            /**
             * 返回路由集合
             */
            return routersByDataSource;
        }


    }

}
