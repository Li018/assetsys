package com.example.assetsys.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.assetsys.common.Result;
import com.example.assetsys.entity.Files;
import com.example.assetsys.mapper.FileMapper;
import com.example.assetsys.service.FileService;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: assetsys
 * @ClassName:FileController
 * @description: 文件上传控制器
 * @author:li
 * @Version 3.0
 **/
@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${files.upload.path}")
    //将application.yml文件中的文件位置赋予到fileUploadPath    //注意导包：.beans.factory.annotation.Value;
    private String fileUploadPath;

    @Resource
    private FileMapper fileMapper;

    @Resource
    private FileService fileService;
    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    public Result upload(@RequestParam MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename(); //获取( 原始名称 )
        String type = FileUtil.extName(originalFilename); //获取( 文件类型 )    //注意FileUtil.extName是String
        long size = file.getSize(); //获取( 文件大小 )

        // 1.定义一个文件唯一的标识位
        String uuid = IdUtil.fastSimpleUUID();
        String fileUUID = uuid + StrUtil.DOT + type;
        File uploadFile = new File(fileUploadPath + fileUUID); //StrUtil.DOT( 文件名) + type( png )

        // 2.判断配置的文件目录是否存在，若不存在则创建一个新的文件目录
        if (!uploadFile.getParentFile().exists()) {
            uploadFile.getParentFile().mkdirs();
        }

        // 3.获取文件的url
        String url;
        // 上传文件到磁盘
        file.transferTo(uploadFile);
        // 获取文件的md5
        String md5 = SecureUtil.md5(uploadFile);
        // 从数据库查询是否存在相同的记录
        Files dbFiles = getFileByMd5(md5);
        if (dbFiles != null) {
            url = dbFiles.getUrl();
            // 由于文件已存在，所以删除刚才上传的重复文件
            uploadFile.delete();
        } else {
            // 数据库若不存在重复文件,则不删除刚才上传的文件
            url = "http://localhost:9090/api/file/" + fileUUID;
        }


        // 4.再存储到数据库
        Files saveFile = new Files();
        saveFile.setName(originalFilename);
        saveFile.setType(type);
        saveFile.setSize(size / 1024);//转换在数据库显示的图片大小为KB
        saveFile.setUrl(url);
        saveFile.setMd5(md5);
        fileService.save(saveFile);
        return Result.success(url);  //上传成功后返回url
    }


    /**
     * 文件下载
     *
     * @param fileUUID
     * @param response
     * @throws IOException
     */
    @GetMapping("/{fileUUID}")
    public void download(@PathVariable String fileUUID, HttpServletResponse response) throws IOException {
        // 根据文件的唯一标识码获取文件
        File uploadFile = new File(fileUploadPath + fileUUID);

        // 设置输出流格式
        ServletOutputStream os = response.getOutputStream(); // 写出流
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileUUID, "UTF-8"));
        response.setContentType("application/octet-stream");

        // 读取文件的字节流
        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();
    }

    /**
     * md5查询文件
     *
     * @param md5
     * @return
     */
    private Files getFileByMd5(String md5) {
        // 查询文件的md5是否存在

        List<Files> filesList = fileMapper.selectList(new LambdaQueryWrapper<Files>().eq(Files::getMd5,md5));
        return filesList.size() == 0 ? null : filesList.get(0);
    }

    /**
     * 分页显示
     *
     * @param pageNum
     * @param pageSize
     * @param name
     * @return
     */

    @GetMapping("/page")

    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name) {


        return Result.success(fileMapper.selectPage(new Page<>(pageNum, pageSize),         new LambdaQueryWrapper<Files>().eq(Files::getIsDelete,false).like(Files::getName,name).orderByDesc(Files::getId)));

    }

    /**
     * 修改文件
     *
     * @param files
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody Files files) {
        return Result.success(
                fileService.saveOrUpdate(files));
    }

    /**
     * 删除文件
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        Files files = fileService.getById(id);
        files.setIsDelete(true);
        fileService.saveOrUpdate(files);
        return Result.success();
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping("/batch/{ids}")
    public Result deleteBatch(@PathVariable Long[] ids) {

      fileService.list(new LambdaQueryWrapper<Files>().in(Files::getId,ids)).forEach(files -> {
          files.setIsDelete(true);fileService.saveOrUpdate(files);
      });

        return Result.success();
    }


}





