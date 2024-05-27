//package com.example.assetsys;
//
//import com.baomidou.mybatisplus.core.mapper.BaseMapper;
//import com.baomidou.mybatisplus.generator.FastAutoGenerator;
//import com.baomidou.mybatisplus.generator.config.OutputFile;
//import com.example.assetsys.service.UserRoleService;
//import com.example.assetsys.service.UserService;
//import jakarta.annotation.Resource;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.Collections;
//
//@SpringBootTest
//@Slf4j
//class assetsysApplicationTests {
//    @Value("${spring.datasource.url}")
//    private String url;
//    @Value("${spring.datasource.username}")
//    private String username;
//    @Value(("${spring.datasource.password}"))
//    private String password;
//
//    @Resource
//    private UserRoleService userRoleService;
//    @Resource
//    private UserService userService;
//
//
//
//    @Test
//    void contextLoads() {
//        FastAutoGenerator.create(url, username, password)
//                .globalConfig(builder -> {
//                    builder
//                            .disableOpenDir()
//                            .fileOverride()// 覆盖已生成文件
//                            .enableSwagger()//启动swagger模式
//                            .outputDir(System.getProperty("user.dir") + "/src/main/java/"); // 指定输出目录
//                })
//                .packageConfig(builder -> {
//                    builder.parent("com.example.assetsys") // 设置父包名
//                            .moduleName(null) // 设置父包模块名
//                            .entity("entity")
//                            .service("service")
//                            .serviceImpl("service.impl")
//                            .mapper("mapper")
//                            .xml("mapper.xml")
//                            .controller("controller")
//                            .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "/src/main/resources/mapper/")); // 设置mapperXml生成路径
//                })
//                .strategyConfig(builder -> {
//                    builder.addInclude("sys_student") // 设置需要生成的表名
//                            .addTablePrefix("sys")
//                            .serviceBuilder()
//                            .formatServiceFileName("%sService")
//                            .formatServiceImplFileName("%sServiceImpl")
//                            .entityBuilder()
//                            .enableLombok()
//                            .logicDeleteColumnName("deleted")
//                            .enableTableFieldAnnotation()
//                            .controllerBuilder()
//                            .enableRestStyle()
//                            .formatFileName("%sController")
//                            .mapperBuilder()
//                            .enableBaseResultMap()  //生成通用的resultMap
//                            .superClass(BaseMapper.class)
//                            .formatMapperFileName("%sMapper")
//                            .enableMapperAnnotation()
//                            .formatXmlFileName("%sMapper");
//                    // 设置过滤表前缀 可以在Constant自行设置
//                })
//                // 使用Velocity引擎模板
//                .execute();
//    }
//
//
//
//
//}
