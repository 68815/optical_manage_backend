package com.optical.manage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.optical.manage.mapper")
public class OpticalManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpticalManageApplication.class, args);
    }
}
