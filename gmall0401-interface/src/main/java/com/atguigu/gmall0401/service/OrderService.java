package com.atguigu.gmall0401.service;

import com.atguigu.gmall0401.bean.OrderInfo;
import com.atguigu.gmall0401.enums.ProcessStatus;
import com.sun.org.apache.xpath.internal.operations.Or;

import java.util.List;
import java.util.Map;

public interface OrderService {


    public  String  saveOrder(OrderInfo orderInfo);

    public  OrderInfo getOrderInfo(String orderId);

    public  String  genToken(String userId);

    public  boolean  verifyToken(String userId,String token);


     public  void updateStatus(String orderId, ProcessStatus processStatus,OrderInfo... orderInfo );

    public List<Integer> checkExpiredCoupon();

    public void handleExpiredCoupon(Integer id);

    public   Map initWareParamJson(String orderId);

    public List<OrderInfo> getOrderListByUser(String userId);

     public List<Map>  orderSplit(String orderId, String wareSkuMap);

}
