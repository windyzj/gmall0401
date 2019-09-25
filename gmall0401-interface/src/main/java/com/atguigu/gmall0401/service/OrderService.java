package com.atguigu.gmall0401.service;

import com.atguigu.gmall0401.bean.OrderInfo;
import com.atguigu.gmall0401.enums.ProcessStatus;
import com.sun.org.apache.xpath.internal.operations.Or;

public interface OrderService {


    public  String  saveOrder(OrderInfo orderInfo);

    public  OrderInfo getOrderInfo(String orderId);

    public  String  genToken(String userId);

    public  boolean  verifyToken(String userId,String token);


     public  void updateStatus(String orderId, ProcessStatus processStatus,OrderInfo... orderInfo );



}
