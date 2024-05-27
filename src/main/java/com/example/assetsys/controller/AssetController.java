package com.example.assetsys.controller;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.assetsys.common.Result;

import com.example.assetsys.entity.*;
import com.example.assetsys.mapper.AssetorgMapper;
import com.example.assetsys.service.*;
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
import java.util.Random;


/**
 * @program: assetsys
 * @ClassName:AssetController
 * @description: AssetController前端控制器
 * @author:li
 * @Version 3.0
 **/
@Tag(name = "资产管理 前端控制器")
    @RestController
@RequestMapping("/asset")
    public class AssetController {

@Resource
private AssetService assetService;
@Resource
private AssetorgService assetorgService;
@Resource
private LabelService labelService;
@Resource
private CategoryService categoryService;
@Resource
private AssetorgMapper assetorgMapper;
@Resource
private UserService userService;
/**
 * 新增
 * @param asset
 * @return
 */
@Operation(summary = "新增")
@PostMapping
public Result save(@RequestBody Asset asset){
        asset.setRegisterTime(DateUtil.now());
        Random random = new Random();
        int randomValue = random.nextInt(100);
        asset.setAssetno(DateUtil.now()+randomValue);
        asset.setAssettypeno(String.valueOf(System.currentTimeMillis()));
        return Result.success(assetService.save(asset));
        }
/**
* 修改
*
* @param asset
* @return
*/
@Operation(summary = "修改")
@PutMapping("/{id}")
public Result update(@PathVariable Long id,@RequestBody Asset asset) {
        asset.setLastUpdatedTime(DateUtil.now());
        return Result.success(assetService.updateById(asset));
        }

/**
 * 查询所有Asset
 * @return
 */
@Operation(summary = "查询所有Asset")
@GetMapping
public Result findAll(){
        return Result.success(assetService.list());
        }
/**
  * 获取单个
  * @param id
  * @return
  */
@Operation(summary = "获取单个")
@GetMapping("/{id}")
public Result findOne(@PathVariable Integer id){
        return Result.success(assetService.getById(id));
        }
/**
 * 分页显示
 * @param name
 * @param pageNum
 * @param pageSize
 * @return
 */
@Operation(summary = "分页显示,并可以通过name进行模糊查询，assetlabel、assetcategory、assetorg进行精确查询")
@GetMapping("/page")
public Result findPage(
@RequestParam(defaultValue = "") String name,
@RequestParam(defaultValue = "") String assetlabel,
@RequestParam(defaultValue = "") String assetcategory,
@RequestParam(defaultValue = "") String assetorg,
@RequestParam(defaultValue = "1") Integer pageNum,
@RequestParam(defaultValue = "10") Integer pageSize){
        QueryWrapper<Asset>queryWrapper=new QueryWrapper<>();
        if(!"".equals(name)){
        queryWrapper.like("assetname",name);

        }
        if(!"".equals(assetlabel)){
                queryWrapper.eq("assetlabel",assetlabel);
        }
        if(!"".equals(assetcategory)){
                queryWrapper.eq("assetcategory",assetcategory);
        }
        if(!"".equals(assetorg)){
                assetorgMapper.selectDescendantsById(assetorg).forEach(entity->{
                        queryWrapper.or().eq("belongOrg",entity.getId());
                });
        }
        queryWrapper.orderByDesc("id");
        Page<Asset>page = assetService.page(new Page<>(pageNum,pageSize),queryWrapper);
        List<Asset>records = page.getRecords();
        for (Asset asset:records){
                Assetorg assetorg1 = assetorgService.getById(asset.getBelongOrg());
                if(ObjectUtil.isNotEmpty(assetorg1)){
                        asset.setAssetorg(assetorg1);
                }
                Label label = labelService.getById(asset.getAssetlabel());
                if(ObjectUtil.isNotEmpty(label)){
                        asset.setLabel(label);
                }
                Category category =  categoryService.getById(asset.getAssetcategory());
                if(ObjectUtil.isNotEmpty(category)){
                        asset.setCategory(category);
                }
                User user = userService.getById(asset.getAssetUser());
                if (ObjectUtil.isNotEmpty(user)){
                        asset.setUser(user);
                }
        }
        page.setRecords(records);
        return Result.success(page);
        }
/**
* 单个删除
* @param id
* @return
*/
@Operation(summary = "单个删除")
@DeleteMapping("/{id}")
public Result delete(@PathVariable Integer id){
        return Result.success(assetService.removeById(id));
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
        return Result.success(assetService.removeByIds(Arrays.asList(ids)));
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

        List<Asset> list = assetService.listByIds(Arrays.asList(ids));
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("asset导出数据", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), Asset.class).sheet("sheel1").doWrite(list);

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
        EasyExcel.read(file.getInputStream(), Asset.class, new PageReadListener<Asset>(dataList -> {
        dataList.forEach(entity -> assetService.save(entity.toBuilder().id(null).build()));
        })).sheet().doRead();
        return Result.success();
        }

        }