package com.atguigu.gmall0401.order.consumer;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall0401.bean.OrderDetail;
import com.atguigu.gmall0401.bean.OrderInfo;
import com.atguigu.gmall0401.enums.ProcessStatus;
import com.atguigu.gmall0401.service.OrderService;
import com.atguigu.gmall0401.util.ActiveMQUtil;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Component
public class OrderConsumer {

    @Autowired
    ActiveMQUtil activeMQUtil;

    @Reference
    OrderService orderService;

    @JmsListener(destination = "PAYMENT_TO_ORDER"  ,containerFactory ="jmsQueueListener"  )
    public  void consumePayment(MapMessage mapMessage) throws JMSException {
        String orderId = mapMessage.getString("orderId");
        String result = mapMessage.getString("result");
        if("success".equals(result)){
            System.out.println("订单"+orderId +"支付完成");
            // 订单修改状态
            orderService.updateStatus(orderId, ProcessStatus.PAID);
            // 发送消息给库存系统
            sendOrderToWare(orderId);
        }

    }

    public void sendOrderToWare(String orderId){
        String wareParamJson = initWareParamJson(orderId);

        Connection connection = activeMQUtil.getConnection();
        try {
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            MessageProducer producer = session.createProducer(session.createQueue("ORDER_RESULT_QUEUE"));
            ActiveMQTextMessage textMessage = new ActiveMQTextMessage();
            textMessage.setText(wareParamJson);
            producer.send(textMessage);
            orderService.updateStatus(orderId, ProcessStatus.NOTIFIED_WARE);
            session.commit();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }


    }

    /**
     * 初始化 发送到库存系统中的参数
     * @param orderId
     * @return
     */
    private  String initWareParamJson(String orderId){
        OrderInfo orderInfo = orderService.getOrderInfo(orderId);

        Map  paramMap=new HashMap();

        paramMap.put("orderId",orderId);
        paramMap.put("consignee",orderInfo.getConsignee());
        paramMap.put("consigneeTel",orderInfo.getConsigneeTel());
        paramMap.put("orderComment",orderInfo.getOrderComment());
        paramMap.put("orderBody",orderInfo.genSubject());
        paramMap.put("deliveryAddress",orderInfo.getDeliveryAddress());
        paramMap.put("paymentWay","2");
        List<Map> details=new ArrayList();
        for (OrderDetail orderDetail : orderInfo.getOrderDetailList() ){
            HashMap<String, String> orderDetailMap = new HashMap<>();
            orderDetailMap.put("skuId",orderDetail.getSkuId());
            orderDetailMap.put("skuNum",orderDetail.getSkuNum().toString());
            orderDetailMap.put("skuName",orderDetail.getSkuName());
            details.add(orderDetailMap);
        }
        paramMap.put("details",details );
        String paramJson = JSON.toJSONString(paramMap);
        return  paramJson;

    }

    @JmsListener(destination = "SKU_DEDUCT_QUEUE",containerFactory = "jmsQueueListener" )
    public  void  consumeWareDeduct(MapMessage mapMessage) throws JMSException {
        // 更新订单状态
        String orderId = mapMessage.getString("orderId");
        String status = mapMessage.getString("status");
        if("DEDUCTED".equals(status)){
            orderService.updateStatus(orderId,ProcessStatus.WAITING_DELEVER);
        }else{
            orderService.updateStatus(orderId,ProcessStatus.STOCK_EXCEPTION);
        }

    }


    @JmsListener(destination = "SKU_DELIVER_QUEUE",containerFactory = "jmsQueueListener" )
    public void consumeDeliver(MapMessage mapMessage) throws JMSException {
        String orderId = mapMessage.getString("orderId");
        String status = mapMessage.getString("status");
        String trackingNo = mapMessage.getString("trackingNo");

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setTrackingNo(trackingNo);
        if(status.equals("DELEVERED")){
            orderService.updateStatus(orderId,ProcessStatus.DELEVERED,orderInfo);
        }
        new ThreadPoolTaskExecutor();


        ScheduledExecutorService executorService;
        ExecutorService executorService1 = Executors.newFixedThreadPool(1);
    }
}
