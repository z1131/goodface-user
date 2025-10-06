package com.deepknow.goodface.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDubbo
@MapperScan("com.deepknow.goodface.user.repo")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}