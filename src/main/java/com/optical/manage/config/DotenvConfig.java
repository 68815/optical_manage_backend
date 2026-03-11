package com.optical.manage.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {
    
    static {
        // 在类加载时就加载.env文件，确保环境变量在Spring Boot读取配置文件之前可用
        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .filename(".env")
                .load();
        
        // 将.env文件中的环境变量设置到系统环境变量中
        dotenv.entries().forEach(entry -> {
            if (System.getenv(entry.getKey()) == null) {
                System.setProperty(entry.getKey(), entry.getValue());
            }
        });
    }
}
