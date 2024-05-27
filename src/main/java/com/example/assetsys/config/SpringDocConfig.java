package com.example.assetsys.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: assetsys
 * @ClassName:SpringDocConfig
 * @description: 接口文档SwaagerUI的配置类
 * 主要是配置Swaager页面的一些页面介绍参数等等
 * @author:li
 * @Version 3.0
 **/
@Configuration
@Slf4j
public class SpringDocConfig {
    @Bean
    public OpenAPI restfulOpenAPI() {
        return new OpenAPI()
                .info(new Info().title(" 安全资产攻防管理平台")
                        .description("安全资产攻防管理平台！")
                        .version("v1.0.")
                        .license(new License().name("黎世强")));
    }

}
