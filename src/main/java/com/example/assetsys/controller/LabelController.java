package com.example.assetsys.controller;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.assetsys.common.Result;

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


import com.example.assetsys.service.LabelService;
import com.example.assetsys.entity.Label;



/**
 * @program: assetsys
 * @ClassName:LabelController
 * @description: LabelController前端控制器
 * @author:li
 * @Version 3.0
 **/
@Tag(name = "资产标签 前端控制器")
    @RestController
@RequestMapping("/label")
    public class LabelController {

@Resource
private LabelService labelService;
/**
 * 新增
 * @param label
 * @return
 */
@Operation(summary = "新增")
@PostMapping
public Result save(@RequestBody Label label){
        QueryWrapper<Label>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("name",label.getName());
        if(labelService.exists(queryWrapper)){
                return Result.error("已存在标签");
        }
        return Result.success(labelService.save(label));
        }
/**
* 修改
*
* @param label
* @return
*/
@Operation(summary = "修改")
@PutMapping("/{id}")
public Result update(@PathVariable Long id,@RequestBody Label label) {
        QueryWrapper<Label>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("name",label.getName());
        if(labelService.exists(queryWrapper)){
                return Result.error("已存在标签");
        }
        return Result.success(labelService.updateById(label));
        }

/**
 * 查询所有Label
 * @return
 */
@Operation(summary = "查询所有Label")
@GetMapping
public Result findAll(){
        return Result.success(labelService.list());
        }
/**
  * 获取单个
  * @param id
  * @return
  */
@Operation(summary = "获取单个")
@GetMapping("/{id}")
public Result findOne(@PathVariable Integer id){
        return Result.success(labelService.getById(id));
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
        QueryWrapper<Label>queryWrapper=new QueryWrapper<>();
        if(!"".equals(name)){
        queryWrapper.like("name",name);

        }
        queryWrapper.orderByDesc("id");
        return Result.success(labelService.page(new Page<>(pageNum,pageSize),queryWrapper));
        }
/**
* 单个删除
* @param id
* @return
*/
@Operation(summary = "单个删除")
@DeleteMapping("/{id}")
public Result delete(@PathVariable Integer id){
        return Result.success(labelService.removeById(id));
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
        return Result.success(labelService.removeByIds(Arrays.asList(ids)));
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

        List<Label> list = labelService.listByIds(Arrays.asList(ids));
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("label导出数据", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), Label.class).sheet("sheel1").doWrite(list);

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
        EasyExcel.read(file.getInputStream(), Label.class, new PageReadListener<Label>(dataList -> {
        dataList.forEach(entity -> labelService.save(entity.toBuilder().id(null).build()));
        })).sheet().doRead();
        return Result.success();
        }

        }