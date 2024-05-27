package com.example.assetsys.service;

import com.example.assetsys.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * @program: assetsys
 * @ClassName: UserService
 * @description: 用户表 service
 * @author:li
 * @Version 3.0
 **/

public interface UserService extends IService<User> {

    User getUserInfo();
}
