package com.atguigu.gmall0401.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.atguigu.gmall0401")
@MapperScan(basePackages = "com.atguigu.gmall0401.cart.mapper")
public class Gmall0401CartServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(Gmall0401CartServiceApplication.class, args);
    }

}
