package com.example.assetsys.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @program: assetsys
 * @ClassName:AsyncConfig
 * @description: 异步发送请求的配置
 * @author:li
 * @Version 3.0
 **/
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    /**
     * 重写关于异步的配置，在邮箱发送邮件的时候进行发送
     * @return
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("MyAsync-");
        executor.initialize();
        return executor;
    }

}