package com.example.assetsys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.assetsys.entity.Files;
import com.example.assetsys.mapper.FileMapper;
import com.example.assetsys.service.FileService;
import org.springframework.stereotype.Service;

/**
 * @program: assetsys
 * @ClassName 文件上传 service实现类
 * @description:
 * @Version 3.0
 **/
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, Files> implements FileService {

}
