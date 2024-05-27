package com.example.assetsys.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.assetsys.annotations.ClearPerms;
import com.example.assetsys.common.Result;
import com.example.assetsys.entity.Role;
import com.example.assetsys.entity.RolePermission;
import com.example.assetsys.entity.UserRole;
import com.example.assetsys.service.RolePermissionService;
import com.example.assetsys.service.RoleService;
import com.example.assetsys.service.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @program: assetsys
 * @ClassName:RoleController
 * @description: RoleController前端控制器
 * @author:li
 * @Version 3.0
 **/
@Tag(name = "角色表 前端控制器")
@RestController
@Slf4j
@RequestMapping("/role")
public class RoleController {
    //  注入RoleService属性
    @Resource
    private RoleService roleService;
    //    注入RolePermissionService属性
    @Resource
    private RolePermissionService rolePermissionService;
    @Resource
    private UserRoleService userRoleService;

    /**
     * 新增
     *
     * @param role
     * @return
     */
    @Operation(summary = "新增")
    @PostMapping
    @ClearPerms
    public Result save(@RequestBody Role role) {
//        先新增role数据
//        再根据新增的Role返回过来的ID新增RolePermission关联表数据
        roleService.save(role);
//        使用saveBatchRolePermissions方法
        this.saveBatchRolePermissions(role);
        return Result.success();
    }

    /**
     * 批量新增rolePermissions
     *
     * @param role
     */
    private void saveBatchRolePermissions(Role role) {
/**
 *   遍历role的permissionIds,
 *   每一次遍历都创建一个RolePermission对象
 *   将遍历来的pid（权限ID）和
 */


        role.getPermissionIds().forEach(pid -> {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(role.getId());
            rolePermission.setPermissionId(pid);
            rolePermissionService.save(rolePermission);
        });

    }

    /**
     * 修改
     *
     * @param role
     * @return
     */
    @Operation(summary = "修改")
    @ClearPerms
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Role role) {
        rolePermissionService.remove(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, id));
//        log.warn(JSONUtil.toJsonStr(role.getPermissionIds()));
        saveBatchRolePermissions(role);
        return Result.success(roleService.updateById(role));
    }

    /**
     * 查询所有Role
     *
     * @return
     */
    @Operation(summary = "查询所有Role")
    @GetMapping
    public Result indAll() {
        return Result.success(roleService.list());
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
        Role role = roleService.getById(id);
        role.setPermissionIds(rolePermissionService.list(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, id)).stream().map(RolePermission::getPermissionId).collect(Collectors.toList()));

        return Result.success(role);
    }

    /**
     * 分页显示
     *
     * @param name
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Operation(summary = "分页显示")
    @GetMapping("/page")
    public Result findPage(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(roleService.page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<Role>().like(Role::getName, name).orderByDesc(Role::getId)));
    }

    /**
     * 单个删除
     *
     * @param id
     * @return
     */
    @Operation(summary = "单个删除")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        rolePermissionService.remove(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, id));
        userRoleService.remove(new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, id));
        return Result.success(roleService.removeById(id));
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @Operation(summary = "批量删除")
    @DeleteMapping("/batch/{ids}")
    @Transactional
    public Result deleteByIds(@PathVariable String[] ids) {
        Arrays.asList(ids).forEach(id -> {
            rolePermissionService.remove(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, id));
            userRoleService.remove(new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, id));
        });
        return Result.success(roleService.removeByIds(Arrays.asList(ids)));
    }
    /**
     * 批量导出
     * @param ids
     * @param response
     * @throws IOException
     */
    @Operation(summary = "批量导出")
    @GetMapping("/batch/export/{ids}")
    public void exportByIds(@PathVariable String[] ids, HttpServletResponse response) throws IOException {
        List<Role> list = roleService.listByIds(Arrays.asList(ids));
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("user导出数据", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), Role.class).sheet("sheel1").doWrite(list);

    }
}

