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


import com.example.assetsys.service.AssetorgService;
import com.example.assetsys.entity.Assetorg;



/**
 * @program: assetsys
 * @ClassName:AssetorgController
 * @description: AssetorgController前端控制器
 * @author:li
 * @Version 3.0
 **/
@Tag(name = "资产组织管理 前端控制器")
    @RestController
@RequestMapping("/assetorg")
    public class AssetorgController {

@Resource
private AssetorgService assetorgService;
/**
 * 新增
 * @param assetorg
 * @return
 */
@Operation(summary = "新增")
@PostMapping
public Result save(@RequestBody Assetorg assetorg){
        return Result.success(assetorgService.save(assetorg));
        }
/**
* 修改
*
* @param assetorg
* @return
*/
@Operation(summary = "修改")
@PutMapping("/{id}")
public Result update(@PathVariable Long id,@RequestBody Assetorg assetorg) {
        return Result.success(assetorgService.updateById(assetorg));
        }

/**
 * 查询所有Assetorg
 * @return
 */
@Operation(summary = "查询所有Assetorg")
@GetMapping
public Result findAll(){
        return Result.success(assetorgService.list());
        }
/**
  * 获取单个
  * @param id
  * @return
  */
@Operation(summary = "获取单个")
@GetMapping("/{id}")
public Result findOne(@PathVariable Integer id){
        return Result.success(assetorgService.getById(id));
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
@RequestParam(defaultValue = "") String fathername,
@RequestParam(defaultValue = "1") Integer pageNum,
@RequestParam(defaultValue = "10") Integer pageSize){
        QueryWrapper<Assetorg>queryWrapper=new QueryWrapper<>();
        if(!"".equals(name)){
        queryWrapper.like("orgname",name);
        }
        if(!"".equals(fathername)){
                queryWrapper.like("fatherorg",fathername);
        }
        queryWrapper.orderByDesc("id");
        return Result.success(assetorgService.page(new Page<>(pageNum,pageSize),queryWrapper));
        }
/**
* 单个删除
* @param id
* @return
*/
@Operation(summary = "单个删除")
@DeleteMapping("/{id}")
public Result delete(@PathVariable Integer id){
        return Result.success(assetorgService.removeById(id));
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
        return Result.success(assetorgService.removeByIds(Arrays.asList(ids)));
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

        List<Assetorg> list = assetorgService.listByIds(Arrays.asList(ids));
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("assetorg导出数据", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), Assetorg.class).sheet("sheel1").doWrite(list);

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
        EasyExcel.read(file.getInputStream(), Assetorg.class, new PageReadListener<Assetorg>(dataList -> {
        dataList.forEach(entity -> assetorgService.save(entity.toBuilder().id(null).build()));
        })).sheet().doRead();
        return Result.success();
        }

        }