package com.example.assetsys.controller;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.assetsys.common.Result;

import com.example.assetsys.entity.*;
import com.example.assetsys.entity.dto.TaskDto;
import com.example.assetsys.mapper.AssetorgMapper;
import com.example.assetsys.service.*;
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


/**
 * @program: assetsys
 * @ClassName:TaskController
 * @description: TaskController前端控制器
 * @author:li
 * @Version 3.0
 **/
@Tag(name = "演练任务 前端控制器")
    @RestController
@RequestMapping("/task")
@Slf4j
    public class TaskController {

@Resource

private TaskService taskService;
        @Resource
        private AssetorgService assetorgService;
        @Resource
        private UserService userService;
        @Resource
        private MytaskService mytaskService;
/**
 * 新增
 * @param
 * @return
 */
@Operation(summary = "新增")
@PostMapping
public Result save(@RequestBody
//                   TaskDto taskDto
                           Task task             ){

//        if(task.getJoinedtesters()!=null){
//                String tester = task.getJoinedtesters();
//                String[] split = tester.split(",");
//                for (String s : split) {
//                        Mytask mytask = new Mytask();
//                        Long taskid = task.getId();
//                        mytask.setTaskid(taskid.toString());
//                        mytask.setUserid(s);
//                        mytask.setTaskstatus(task.getTaskstatus());
//                        mytaskService.save(mytask);
//                }
//        }

        return Result.success(  taskService.save(task));
        }
/**
* 修改
*
* @param task
* @return
*/
@Operation(summary = "修改")
@PutMapping("/{id}")
public Result update(@PathVariable Long id,@RequestBody Task task) {
        if(task.getTaskstatus().equals("进行中")){
                task.setStarttime(DateUtil.now());
        }else if(task.getTaskstatus().equals("已完成")){
                task.setEndtime(DateUtil.now());
        }
        log.info("task:{}",task);
        String tester = task.getJoinedtesters();
        if(tester==null){
                return Result.success(taskService.updateById(task));
        }
        String[] split2 = tester.split(",");
        if(taskService.getById(task.getId()).getJoinedtesters() == null){
                for (String s : split2) {
                        Mytask mytask = new Mytask();
                        Long taskid = task.getId();
                        mytask.setTaskid(taskid.toString());
                        mytask.setTaskname(task.getName());
                        mytask.setUserid(s);
                        mytask.setTaskstatus(task.getTaskstatus());
                        log.info("mytask更新:{}",mytask);
                        mytaskService.save(mytask);

                }
                return Result.success(taskService.updateById(task));
        }
        String pretester  = taskService.getById(task.getId()).getJoinedtesters();
        String[] split1 = pretester.split(",");
        if(!pretester.equals(tester)){
                //在pretester删除掉与tester没有有交集的部分
                for (String s : split1) {
                        if(!Arrays.asList(split2).contains(s)){
                                QueryWrapper<Mytask> queryWrapper = new QueryWrapper<>();
                                queryWrapper.eq("userid",s);
                                queryWrapper.eq("taskid",task.getId());
                                mytaskService.remove(queryWrapper);
                        }
                }

                //将tester中的与pretester有交集的部分的状态改为taskstatus

        }

        //将tester中的与pretester没有交集的部分添加到数据库中
        for (String s : split2) {
                if(!Arrays.asList(split1).contains(s)){
                        Mytask mytask = new Mytask();
                        Long taskid = task.getId();
                        mytask.setTaskid(taskid.toString());
                        mytask.setTaskname(task.getName());
                        mytask.setUserid(s);
                        mytask.setTaskstatus(task.getTaskstatus());
                        log.info("没有交集的部分:{}",mytask);
                        mytaskService.save(mytask);
                }
        }
        //将tester中的与pretester有交集的部分的状态改为taskstatus
        for (String s : split2) {
                if(Arrays.asList(split1).contains(s)) {
                        QueryWrapper<Mytask> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("taskid",task.getId());
                        queryWrapper.eq("userid",userService.getUserInfo().getId());
                        Mytask mytask = mytaskService.getOne(queryWrapper);
                        mytask.setTaskstatus(task.getTaskstatus());
                        log.info("交集的部分:{}", mytask);
                        mytaskService.updateById(mytask);
                }
        }
//        for (String s : split2) {
//                log.info("mytask交集部分:{}",s);
//                log.info("split2:{}",Arrays.asList(split2));
//                if(Arrays.asList(split1).contains(s)){
//                        Mytask mytask = new Mytask();
//                        Long taskid = task.getId();
//                        mytask.setTaskid(taskid.toString());
//                        mytask.setUserid(s);
//                        mytask.setTaskstatus(task.getTaskstatus());
//                        mytaskService.save(mytask);
//                }
//        }
//        String[] split = tester.split(",");
//        for (String s : split) {
//                Mytask mytask = new Mytask();
//                Long taskid = task.getId();
//                mytask.setTaskid(taskid.toString());
//                mytask.setUserid(s);
//                mytaskService.save(mytask);
//        }

        return Result.success(taskService.updateById(task));
        }

/**
 * 查询所有Task
 * @return
 */
@Operation(summary = "查询所有Task")
@GetMapping
public Result findAll(){
        return Result.success(taskService.list());
        }
/**
  * 获取单个
  * @param id
  * @return
  */
@Operation(summary = "获取单个")
@GetMapping("/{id}")
public Result findOne(@PathVariable Integer id){
        return Result.success(taskService.getById(id));
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
@RequestParam(defaultValue = "") String taskdesc,
@RequestParam(defaultValue = "1") Integer pageNum,
@RequestParam(defaultValue = "") String taskstatus,
@RequestParam(defaultValue = "10") Integer pageSize){
        log.info("name:{}",name);
        log.info("taskdesc:{}",taskdesc);
        QueryWrapper<Task>queryWrapper=new QueryWrapper<>();
        if(!"".equals(name)){
        queryWrapper.like("name",name);

        }

        if(!"".equals(taskdesc)){
                queryWrapper.like("taskdesc",taskdesc);
        }
        if(!"".equals(taskstatus)){
                queryWrapper.like("taskstatus",taskstatus);
        }
        queryWrapper.orderByDesc("id");
        log.info("queryWrapper:{}",queryWrapper);
        log.info("pageNum:{}",taskService.list(queryWrapper));
        Page<Task> taskPage = taskService.page(new Page<>(pageNum, pageSize), queryWrapper);
        List<Task> records = taskPage.getRecords();
        for (Task task : records) {
                Assetorg assetorg = assetorgService.getById(task.getRelatedorg());
                if(ObjectUtil.isNotEmpty(assetorg)){
                        task.setAssetorg(assetorg);
                }
                User user = userService.getById(task.getJoinedtesters());
                if(ObjectUtil.isNotEmpty(user)){
                        task.setUser(user);

                }
        }
        taskPage.setRecords(records);
        return Result.success(taskPage);
        }
/**
* 单个删除
* @param id
* @return
*/
@Operation(summary = "单个删除")
@DeleteMapping("/{id}")
public Result delete(@PathVariable Integer id){
        return Result.success(taskService.removeById(id));
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
        return Result.success(taskService.removeByIds(Arrays.asList(ids)));
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

        List<Task> list = taskService.listByIds(Arrays.asList(ids));
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("task导出数据", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), Task.class).sheet("sheel1").doWrite(list);

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
        EasyExcel.read(file.getInputStream(), Task.class, new PageReadListener<Task>(dataList -> {
        dataList.forEach(entity -> taskService.save(entity.toBuilder().id(null).build()));
        })).sheet().doRead();
        return Result.success();
        }

        }