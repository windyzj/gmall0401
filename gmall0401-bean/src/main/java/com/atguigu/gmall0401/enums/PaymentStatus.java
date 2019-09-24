package com.atguigu.gmall0401.enums;

public enum PaymentStatus {
    UNPAID("支付中"),
    PAID("已支付"),
    PAY_FAIL("支付失败"),
    PAY_REFUND("支付用户退款"),
    ClOSED("已关闭");

    private String name ;

    PaymentStatus(String name) {
        this.name=name;
    }

}
