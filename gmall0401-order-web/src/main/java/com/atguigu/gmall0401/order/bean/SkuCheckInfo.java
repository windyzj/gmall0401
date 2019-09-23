package com.atguigu.gmall0401.order.bean;

import com.atguigu.gmall0401.bean.OrderDetail;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SkuCheckInfo {

    OrderDetail orderDetail;

    String checkType;   //验证库存是1  验价格2

    Boolean ifPass=false;

    String checkMsg;

}
