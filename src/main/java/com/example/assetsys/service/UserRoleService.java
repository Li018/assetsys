package com.example.assetsys.service;

import com.example.assetsys.entity.UserRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * @program: assetsys
 * @ClassName: UserRoleService
 * @description: 用户角色表 service
 * @author:li
 * @Version 3.0
 **/

public interface UserRoleService extends IService<UserRole> {

    List<Long> getRoleIds(Object loginId, String loginType);
}
