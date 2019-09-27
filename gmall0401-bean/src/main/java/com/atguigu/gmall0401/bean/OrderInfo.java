package com.atguigu.gmall0401.bean;

import com.atguigu.gmall0401.enums.OrderStatus;
import com.atguigu.gmall0401.enums.PaymentWay;
import com.atguigu.gmall0401.enums.ProcessStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderInfo implements Serializable {
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column
    private String consignee;

    @Column
    private String consigneeTel;


    @Column
    private BigDecimal totalAmount;

    @Column
    private OrderStatus orderStatus;

    @Column
    private ProcessStatus processStatus;


    @Column
    private String userId;

    @Column
    private PaymentWay paymentWay;

    @Column
    private Date expireTime;

    @Column
    private String deliveryAddress;

    @Column
    private String orderComment;

    @Column
    private Date createTime;

    @Column
    private String parentOrderId;

    @Column
    private String trackingNo;


    @Transient
    private List<OrderDetail> orderDetailList;

    private List<OrderInfo> orderSubList;
    @Transient
    private String wareId;

    @Column
    private String outTradeNo;

    public void sumTotalAmount(){
        BigDecimal totalAmount=new BigDecimal("0");
        for (OrderDetail orderDetail : orderDetailList) {
            totalAmount= totalAmount.add(orderDetail.getOrderPrice().multiply(new BigDecimal(orderDetail.getSkuNum())));
        }
        this.totalAmount=  totalAmount;
    }


    public String genSubject(){
        if(orderDetailList.size()>0) {
            OrderDetail orderDetail = orderDetailList.get(0);
             return orderDetail.getSkuName()+"等"+orderDetailList.size()+"件商品";
        }
        return null;
    }
}
