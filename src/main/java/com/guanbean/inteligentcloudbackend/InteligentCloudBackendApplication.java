package com.guanbean.inteligentcloudbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@MapperScan("com.guanbean.inteligentcloudbackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableAsync
@SpringBootApplication()

public class InteligentCloudBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(InteligentCloudBackendApplication.class, args);
    }
}
