package com.example.assetsys.controller;
import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.assetsys.common.Result;

import com.example.assetsys.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;


import com.example.assetsys.service.MytaskService;
import com.example.assetsys.entity.Mytask;



/**
 * @program: assetsys
 * @ClassName:MytaskController
 * @description: MytaskController前端控制器
 * @author:li
 * @Version 3.0
 **/
@Tag(name = "我的任务管理 前端控制器")
    @RestController

@RequestMapping("/mytask")
    public class MytaskController {

@Resource
private MytaskService mytaskService;
@Resource
private UserService userService;
/**
 * 新增
 * @param mytask
 * @return
 */
@Operation(summary = "新增")
@PostMapping
public Result save(@RequestBody Mytask mytask){
        return Result.success(mytaskService.save(mytask));
        }
/**
* 修改
*
* @param mytask
* @return
*/
@Operation(summary = "修改")
@PutMapping("/{id}")
public Result update(@PathVariable Long id,@RequestBody Mytask mytask) {
        if(mytask.getFinishstatus().equals("已完成")){
                mytask.setFinishtime(DateUtil.now());
        }
        return Result.success(mytaskService.updateById(mytask));
        }

/**
 * 查询所有Mytask
 * @return
 */
@Operation(summary = "查询所有Mytask")
@GetMapping
public Result findAll(){
        QueryWrapper<Mytask>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("userid",userService.getUserInfo().getId());
        queryWrapper.eq("taskstatus","进行中");
        queryWrapper.eq("finishstatus","已接受");
        return Result.success(mytaskService.list(queryWrapper));
        }
/**
  * 获取单个
  * @param id
  * @return
  */
@Operation(summary = "获取单个")
@GetMapping("/{id}")
public Result findOne(@PathVariable Integer id){
        return Result.success(mytaskService.getById(id));
        }
/**
 * 分页显示
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
@RequestParam(defaultValue = "10") Integer pageSize){
        QueryWrapper<Mytask>queryWrapper=new QueryWrapper<>();
        if(!"".equals(name)){
        queryWrapper.like("taskname",name);

        }
        queryWrapper.orderByDesc("id");
        queryWrapper.eq("userid",userService.getUserInfo().getId());
        return Result.success(mytaskService.page(new Page<>(pageNum,pageSize),queryWrapper));
        }
/**
* 单个删除
* @param id
* @return
*/
@Operation(summary = "单个删除")
@DeleteMapping("/{id}")
public Result delete(@PathVariable Integer id){
        return Result.success(mytaskService.removeById(id));
        }
/**
* 批量删除
* @param ids
* @return
*/
@Operation(summary = "批量删除")
@DeleteMapping("/batch/{ids}")
@Transactional
public Result deleteByIds(@PathVariable String[]ids){
        return Result.success(mytaskService.removeByIds(Arrays.asList(ids)));
        }

/**
 * 批量导出
 * 使用的技术为alibaba下面的easyexcel
 * 写数据
 *
 * @param ids
 * @return
 */
@Operation(summary = "批量导出")
@GetMapping("/batch/export/{ids}")
public void exportByIds(@PathVariable String[] ids, HttpServletResponse response) throws IOException {

        List<Mytask> list = mytaskService.listByIds(Arrays.asList(ids));
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("mytask导出数据", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), Mytask.class).sheet("sheel1").doWrite(list);

        }

/**
 * 批量导入
 * 使用的技术为alibaba下面的easyexcel
 * 读数据
 *
 * @param
 */
@Operation(summary = "批量导入")
@PostMapping("/batch/upload")
public Result writeExcel(MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(), Mytask.class, new PageReadListener<Mytask>(dataList -> {
        dataList.forEach(entity -> mytaskService.save(entity.toBuilder().id(null).build()));
        })).sheet().doRead();
        return Result.success();
        }

        }