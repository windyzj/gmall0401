package com.atguigu.gmall0401.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0401.bean.UserInfo;
import com.atguigu.gmall0401.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @Reference
    UserService userService;

    @GetMapping("trade")
    public UserInfo trade(@RequestParam("userid") String userid){
        UserInfo userInfo = userService.getUserInfoById(userid);
        System.out.println("123123");
        return  userInfo;
    }
}
