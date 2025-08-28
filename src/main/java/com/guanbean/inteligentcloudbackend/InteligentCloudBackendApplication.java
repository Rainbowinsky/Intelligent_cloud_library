package com.guanbean.inteligentcloudbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.guanbean.inteligentcloudbackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class InteligentCloudBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(InteligentCloudBackendApplication.class, args);
    }

}
