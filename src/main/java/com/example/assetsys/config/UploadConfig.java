package com.example.assetsys.config;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @program: assetsys
 * @ClassName:UploadConfig
 * @description: 文件上传配置类
 * @author:li
 * @Version 3.0
 **/
@Configuration
public class UploadConfig implements WebMvcConfigurer {

    /**
     * 文件上传配置类
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //设置单个上传的文件最大尺寸
        factory.setMaxFileSize(DataSize.parse("2011480KB")); //KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize(DataSize.parse("101124000KB"));
        return factory.createMultipartConfig();
    }

    /**
     * 设置资源处理类
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/", "classpath:/resources/", "classpath:/static/");
    }
}