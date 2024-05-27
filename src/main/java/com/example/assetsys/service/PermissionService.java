package com.example.assetsys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.assetsys.entity.Permission;

import java.util.List;


/**
 * @program: assetsys
 * @ClassName: PermissionService
 * @description: 菜单表 service
 * @author:li
 * @Version 3.0
 **/

public interface PermissionService extends IService<Permission> {
    List<Permission> buildTrees(List<Permission> permissions);

    List<Permission> getRouters(Object loginId, String loginType);
}
