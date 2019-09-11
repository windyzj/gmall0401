package com.atguigu.gmall0401.item.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall0401.bean.SkuInfo;
import com.atguigu.gmall0401.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ItemController {

    @Reference
    ManageService manageService;

    @GetMapping("{skuId}.html")
    public String item(@PathVariable("skuId") String skuId, HttpServletRequest request){
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        request.setAttribute("skuInfo",skuInfo);
        request.setAttribute("gname","<span style=\"color:green\">宝强</span>");
        return   "item";
    }
}
