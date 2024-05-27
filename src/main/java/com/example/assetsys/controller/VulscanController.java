package com.example.assetsys.controller;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.assetsys.common.Result;

import com.example.assetsys.entity.Mytask;
import com.example.assetsys.service.MytaskService;
import com.example.assetsys.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


import com.example.assetsys.service.VulscanService;
import com.example.assetsys.entity.Vulscan;



/**
 * @program: assetsys
 * @ClassName:VulscanController
 * @description: VulscanController前端控制器
 * @author:li
 * @Version 3.0
 **/
@Tag(name = "漏洞扫描 前端控制器")
    @RestController
@RequestMapping("/vulscan")
@Slf4j
    public class VulscanController {

@Resource
private VulscanService vulscanService;

@Resource
private MytaskService mytaskService;

@Resource
private UserService  userService;

/**
 * 新增
 * @param vulscan
 * @return
 */
@Operation(summary = "新增")
@PostMapping
public Result save(@RequestBody Vulscan vulscan){
        vulscan.setScantime(DateUtil.now());
        vulscan.setTester(userService.getUserInfo().getId().toString());
        Mytask mytask = mytaskService.getById(vulscan.getRelatertask());
        if(mytask.getSubmitscannum()==null){
                mytask.setSubmitscannum("1");

        }else {
                mytask.setSubmitscannum(String.valueOf(Integer.parseInt(mytask.getSubmitscannum())+1));

        }
        mytaskService.updateById(mytask);
        return Result.success(vulscanService.save(vulscan));
        }
/**
* 修改
*
* @param vulscan
* @return
*/
@Operation(summary = "修改")
@PutMapping("/{id}")
public Result update(@PathVariable Long id,@RequestBody Vulscan vulscan) {
        return Result.success(vulscanService.updateById(vulscan));
        }

/**
 * 查询所有Vulscan
 * @return
 */
@Operation(summary = "查询所有Vulscan")
@GetMapping
public Result findAll() {

        QueryWrapper<Vulscan> queryWrapper = new QueryWrapper<>();
        QueryWrapper<Mytask> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("userid", userService.getUserInfo().getId());
        queryWrapper1.eq("taskstatus", "进行中");
        queryWrapper1.eq("finishstatus", "已接受");
        List<Mytask> list = mytaskService.list(queryWrapper1);
        if (ObjectUtil.isNotEmpty(list)) {
                // 用or.eq
                queryWrapper.in("relatertask",
                        list.stream().map(Mytask::getId).collect(Collectors.toList()));
                queryWrapper.eq("tester", userService.getUserInfo().getId());

        }else {
                return Result.success();
        }
        return Result.success(vulscanService.list(queryWrapper));

}
/**
  * 获取单个
  * @param id
  * @return
  */
@Operation(summary = "获取单个")
@GetMapping("/{id}")
public Result findOne(@PathVariable Integer id){
        return Result.success(vulscanService.getById(id));
        }
/**
// * 分页显示
// * @param name
 * @param pageNum
 * @param pageSize
 * @return
 */
@Operation(summary = "分页显示")
@GetMapping("/page")
public Result findPage(
//@RequestParam(defaultValue = "") String name,
@RequestParam(defaultValue = "") String taskname,
@RequestParam(defaultValue = "1") Integer pageNum,
@RequestParam(defaultValue = "10") Integer pageSize){
        log.info("taskname:{}", taskname);

        QueryWrapper<Vulscan>queryWrapper=new QueryWrapper<>();
//        if(!"".equals(name)){
//        queryWrapper.like("name",name);
//        }
        if(!"".equals(taskname)) {
                // 我通过taskname找到taskid
                QueryWrapper<Mytask> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.like("taskname", taskname);
                log.info("taskname:{}", taskname);
                List<Mytask> list = mytaskService.list(queryWrapper1);
                log.info("list:{}", list);
                if (ObjectUtil.isNotEmpty(list)) {
                        for(Mytask mytask:list){
                                queryWrapper.or().eq("relatertask",mytask.getId());
                        }

                }
        }
        queryWrapper.eq("tester",userService.getUserInfo().getId());

        queryWrapper.orderByDesc("id");
        Page<Vulscan> Vulscanpage = vulscanService.page(new Page<>(pageNum,pageSize),queryWrapper);
        List<Vulscan> records = Vulscanpage.getRecords();
        for(Vulscan vulscan:records){
                Mytask mytask = mytaskService.getById(vulscan.getRelatertask());
                if(ObjectUtil.isNotEmpty(mytask)){
                        vulscan.setMytask(mytask);
                }

        }



        Vulscanpage.setRecords(records);
        return Result.success(Vulscanpage);
        }

/**
* 单个删除
* @param id
* @return
*/
@Operation(summary = "单个删除")
@DeleteMapping("/{id}")
public Result delete(@PathVariable Integer id){
        return Result.success(vulscanService.removeById(id));
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
        return Result.success(vulscanService.removeByIds(Arrays.asList(ids)));
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

        List<Vulscan> list = vulscanService.listByIds(Arrays.asList(ids));
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("vulscan导出数据", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), Vulscan.class).sheet("sheel1").doWrite(list);

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
        EasyExcel.read(file.getInputStream(), Vulscan.class, new PageReadListener<Vulscan>(dataList -> {
        dataList.forEach(entity -> vulscanService.save(entity.toBuilder().id(null).build()));
        })).sheet().doRead();
        return Result.success();
        }

        }