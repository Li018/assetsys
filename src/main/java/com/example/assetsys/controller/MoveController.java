package com.example.assetsys.controller;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.assetsys.common.Result;

import com.example.assetsys.entity.Exploit;
import com.example.assetsys.entity.Mytask;
import com.example.assetsys.entity.Vulscan;
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


import com.example.assetsys.entity.Move;

import javax.swing.text.Utilities;


/**
 * @program: assetsys
 * @ClassName:MoveController
 * @description: MoveController前端控制器
 * @author:li
 * @Version 3.0
 **/
@Tag(name = "横向移动 前端控制器")
    @RestController
@RequestMapping("/move")
    public class MoveController {

@Resource
private MoveService moveService;
@Resource
private ExploitService exploitService;
        @Resource
        private UserService userService;
        @Resource
        private MytaskService mytaskService;
        @Resource
        private VulscanService vulscanService;

        /**
/**
 * 新增
 * @param move
 * @return
 */
@Operation(summary = "新增")
@PostMapping
public Result save(@RequestBody Move move){
        move.setTester(userService.getUserInfo().getId().toString());
        Exploit exploit =  exploitService.getById(move.getExploitvuls()) ;
//        move.setTargetip(exploit.getTargetip());
        String S = exploit.getTargetip();
        move.setMovepath( S  + " 移动到 " + move.getTargetip());
        move.setAttacktime(DateUtil.now());
        Vulscan vulscan =  vulscanService.getById(exploit.getVul()) ;
        Mytask mytask =  mytaskService.getById(vulscan.getRelatertask());
        if(mytask.getSubmitmovenum()==null){
                mytask.setSubmitmovenum("1");
        }else {
                mytask.setSubmitmovenum(String.valueOf(Integer.parseInt(mytask.getSubmitmovenum())+1));
        }
        mytaskService.updateById(mytask);
        return Result.success(moveService.save(move));
        }
/**
* 修改
*
* @param move
* @return
*/
@Operation(summary = "修改")
@PutMapping("/{id}")
public Result update(@PathVariable Long id,@RequestBody Move move) {
        return Result.success(moveService.updateById(move));
        }

/**
 * 查询所有Move
 * @return
 */
@Operation(summary = "查询所有Move")
@GetMapping
public Result findAll(){
        return Result.success(moveService.list());
        }
/**
  * 获取单个
  * @param id
  * @return
  */
@Operation(summary = "获取单个")
@GetMapping("/{id}")
public Result findOne(@PathVariable Integer id){
        return Result.success(moveService.getById(id));
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
        QueryWrapper<Move>queryWrapper=new QueryWrapper<>();
        if(!"".equals(name)){
        queryWrapper.like("name",name);

        }
        queryWrapper.orderByDesc("id");
        queryWrapper.eq("tester",userService.getUserInfo().getId());

        Page<Move> Movepage = moveService.page(new Page<>(pageNum,pageSize),queryWrapper);
        List<Move> records = Movepage.getRecords();
        for (Move move : records) {
                Exploit exploit = exploitService.getById(move.getExploitvuls());
              if(ObjectUtil.isNotNull(exploit)){
                      move.setExploit(exploit);
                      }
        }
        Movepage.setRecords(records);
        return Result.success(Movepage);
        }
/**
* 单个删除
* @param id
* @return
*/
@Operation(summary = "单个删除")
@DeleteMapping("/{id}")
public Result delete(@PathVariable Integer id){
        return Result.success(moveService.removeById(id));
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
        return Result.success(moveService.removeByIds(Arrays.asList(ids)));
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

        List<Move> list = moveService.listByIds(Arrays.asList(ids));
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("move导出数据", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), Move.class).sheet("sheel1").doWrite(list);

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
        EasyExcel.read(file.getInputStream(), Move.class, new PageReadListener<Move>(dataList -> {
        dataList.forEach(entity -> moveService.save(entity.toBuilder().id(null).build()));
        })).sheet().doRead();
        return Result.success();
        }

        }