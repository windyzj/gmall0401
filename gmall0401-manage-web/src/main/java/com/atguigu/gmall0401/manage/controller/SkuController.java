package com.atguigu.gmall0401.manage.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0401.bean.SkuInfo;
import com.atguigu.gmall0401.service.ManageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SkuController {

    @Reference
    ManageService manageService;

    @PostMapping("saveSkuInfo")
    public String  saveSkuInfo(@RequestBody SkuInfo skuInfo){
        manageService.saveSkuInfo(skuInfo);
        return  "success";
    }

}
