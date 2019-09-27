package com.atguigu.gmall0401.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.atguigu.gmall0401")
@MapperScan(basePackages = "com.atguigu.gmall0401.order.mapper")
@EnableTransactionManagement
@EnableScheduling
public class Gmall0401OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(Gmall0401OrderServiceApplication.class, args);
    }

}
