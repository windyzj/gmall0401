package com.atguigu.gmall0401.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.atguigu.gmall0401.user.mapper")
@ComponentScan(basePackages = "com.atguigu.gmall0401")
public class Gmall0401UserManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(Gmall0401UserManageApplication.class, args);
    }

}
