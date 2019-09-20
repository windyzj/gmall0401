package com.atguigu.gmall0401.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.atguigu.gmall0401")
public class Gmall0401CartWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(Gmall0401CartWebApplication.class, args);
    }

}
