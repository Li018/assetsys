package com.example.assetsys.controller;

import com.example.assetsys.common.Result;
import com.example.assetsys.entity.dto.SystemInformation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: assetsys
 * @ClassName:SystemInformationController
 * @description: 系统参数控制器
 * @author:li
 * @Version 3.0
 **/
@RestController
@Tag(name = "系统参数前端控制器")
@RequestMapping("/system")
public class SystemInformationController {
    /**
     * 获取系统参数信息
     *
     * @return
     */
    @GetMapping
    public Result getSystemInformation() {
        return Result.success(new SystemInformation());
    }
}