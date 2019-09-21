package com.atguigu.gmall0401.service;

import com.atguigu.gmall0401.bean.OrderInfo;

public interface OrderService {


    public  void  saveOrder(OrderInfo orderInfo);

    public  String  genToken(String userId);

    public  boolean  verifyToken(String userId,String token);



}
