package com.atguigu.gmall0401.payment.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradePayRequest;
import com.atguigu.gmall0401.bean.OrderInfo;
import com.atguigu.gmall0401.bean.PaymentInfo;
import com.atguigu.gmall0401.enums.PaymentStatus;
import com.atguigu.gmall0401.payment.config.AlipayConfig;
import com.atguigu.gmall0401.service.OrderService;
import com.atguigu.gmall0401.service.PaymentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class PaymentController {

    @Reference
    OrderService orderService;

    @Autowired
    AlipayClient alipayClient;

    @Reference
    PaymentInfoService paymentInfoService;

    @GetMapping("index")
    public String  index(String orderId, HttpServletRequest request){
        OrderInfo orderInfo = orderService.getOrderInfo(orderId);
        request.setAttribute("orderId",orderId);
        request.setAttribute("totalAmount",orderInfo.getTotalAmount());

        return "index";
    }

    @PostMapping("/alipay/submit")
    @ResponseBody
    public String alipaySubmit(String orderId, HttpServletResponse response){

        //1 准备参数 给支付宝提交
        OrderInfo orderInfo = orderService.getOrderInfo(orderId);
        //
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();

        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);
        long currentTimeMillis = System.currentTimeMillis();
        String outTradeNo="ATGUIGU-"+orderId+"-"+currentTimeMillis;
        String productNo="FAST_INSTANT_TRADE_PAY";
        BigDecimal totalAmount = orderInfo.getTotalAmount();
        String subject=orderInfo.genSubject();
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("out_trade_no",outTradeNo);
        jsonObject.put("product_code",productNo);
        jsonObject.put("total_amount",totalAmount);
        jsonObject.put("subject",subject);
        alipayRequest.setBizContent(jsonObject.toJSONString());

        //组织参数
        String submitHtml="";
        try {
              submitHtml = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=UTF-8");

        //2  把提交操作保存起来

        PaymentInfo paymentInfo = new PaymentInfo();

        paymentInfo.setOrderId(orderId);
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOutTradeNo(outTradeNo);
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID);
        paymentInfo.setSubject(subject);
        paymentInfo.setTotalAmount(totalAmount);

        paymentInfoService.savePaymentInfo(paymentInfo);

        return submitHtml;
    }
}
