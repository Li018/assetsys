package com.example.assetsys.controller;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.assetsys.annotations.ClearPerms;
import com.example.assetsys.common.Result;
import com.example.assetsys.entity.Permission;
import com.example.assetsys.entity.RolePermission;
import com.example.assetsys.service.PermissionService;
import com.example.assetsys.service.RolePermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @program: assetsys
 * @ClassName:PermissionController
 * @description: PermissionController前端控制器
 * @author:li
 * @Version 3.0
 **/
@Tag(name = "菜单表 前端控制器")
@RestController
@Slf4j
@RequestMapping("/permission")
public class PermissionController {
//   注入PermissionService属性
    @Resource
    private PermissionService permissionService;
//    RolePermissionService属性
    @Resource
    private RolePermissionService rolePermissionService;

    /**
     * 新增
     *
     * @param permission
     * @return
     */
    @Operation(summary = "新增")
    @PostMapping
    public Result save(@RequestBody Permission permission) {

//        需要对传递过来的Permission数据进行校验
        return this.checkPermission(permission);
    }
    /**
     * 校验传递的权限数据的合法性
     *
     * @param permission
     * @return
     */
    private Result checkPermission(Permission permission) {
		// 先找出来父级菜单
        Permission parentPermission = permissionService.getById(permission.getParentId());
//        判断一下是否为按钮菜单类型在根目录下面
        if (permission.getParentId().equals(0L) && permission.getMenuType() == 3) {
            return Result.error("按钮不能在根目录下面！");
        }
//        判断一下是否为修改，如果是修改的话，需要再次判断父级目录是否为自己
        if (ObjUtil.isNotEmpty(permission.getId())) {

            if (permission.getParentId().equals(permission.getId())) {
                return Result.error("父级目录不能是自己!");
            }
        }
//        判断一下父级菜单类型是否为按钮，正常来说，父级菜单类型只能是目录或者是菜单
        if (parentPermission != null) {
            if (parentPermission.getMenuType() == 3) {
                return Result.error("父级目录不能是按钮!");
            }

        }
//        进行保存（新增或者的修改）
        return Result.success(permissionService.saveOrUpdate(permission));
    }
    /**
     * 修改
     *
     * @param permission
     * @return
     */

    @Operation(summary = "修改")
    @ClearPerms
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Permission permission) {
        return this.checkPermission(permission);
    }



    /**
     * 查询所有Permission
     *
     * @return
     */
    @Operation(summary = "查询所有Permission")
    @GetMapping
    public Result indAll() {
        return Result.success(permissionService.list());
    }

    /**
     * 获取单个
     *
     * @param id
     * @return
     */
    @Operation(summary = "获取单个")
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(permissionService.getById(id));
    }

    /**
     * 递归分页展示
     *
     * @param
     * @param
     * @param
     * @return
     */
    @Operation(summary = "显示树形结构")
    @GetMapping("/page")
    public Result findPage() {
//        按照OrderNum进行排序，查询出所有的Permissions，并且通过PermissionsService的.buildTrees创建属性数据结构
        return Result.success(permissionService.buildTrees(permissionService.list().stream().sorted(Comparator.comparing(Permission::getOrderNum, Comparator.reverseOrder()).reversed()).collect(Collectors.toList())));
    }


    /**
     * 单个删除
     *
     * @param id
     * @return
     */
    @Operation(summary = "单个删除")
    @ClearPerms
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        //        删除自己
        permissionService.removeById(id);
        this.deleteChildByIds(id);
        return Result.success();
    }

    /**
     * 递归调用删除自己和子级菜单
     *
     * @param id
     */
    private void deleteChildByIds(Long id) {

        //      删除关联表
        rolePermissionService.remove(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getPermissionId, id));
        //        找出子级
        LambdaQueryWrapper<Permission> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Permission::getParentId, id);
        //        获取子级ids
        List<Long> ids = permissionService.list(lambdaQueryWrapper).stream().map(permission -> permission.getId()).collect(Collectors.toList());
        //        删除子级
        permissionService.removeByIds(ids);
        //        查询子级是否有子级，如果存在子级，进行递归调用删除子级。
        for (Long pid : ids) {
            List<Permission> permissions = permissionService.list(new LambdaQueryWrapper<Permission>().eq(Permission::getParentId, pid));
            if (permissions.size() > 0) {
                this.deleteChildByIds(pid);
            }
        }


    }


}

