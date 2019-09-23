package com.atguigu.gmall0401.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall0401.bean.OrderDetail;
import com.atguigu.gmall0401.bean.OrderInfo;
import com.atguigu.gmall0401.order.mapper.OrderDetailMapper;
import com.atguigu.gmall0401.order.mapper.OrderInfoMapper;
import com.atguigu.gmall0401.service.OrderService;
import com.atguigu.gmall0401.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import sun.misc.UUDecoder;

import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Override
    @Transactional
    public String saveOrder(OrderInfo orderInfo) {

        orderInfoMapper.insertSelective(orderInfo);

        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(orderInfo.getId());
            orderDetailMapper.insertSelective(orderDetail);
        }

        return orderInfo.getId();

    }

    @Override
    public OrderInfo getOrderInfo(String orderId) {
        OrderInfo orderInfo = orderInfoMapper.selectByPrimaryKey(orderId);
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderId);
        List<OrderDetail> orderDetailList = orderDetailMapper.select(orderDetail);
        orderInfo.setOrderDetailList(orderDetailList);
        return orderInfo;
    }

    @Override
    public String genToken(String userId) {
        //token  type   String  key   user:10201:trade_code  value token
        String token = UUID.randomUUID().toString();
        String tokenKey="user:"+userId+":trade_code";
        Jedis jedis = redisUtil.getJedis();
        jedis.setex(tokenKey,10*60,token);
        jedis.close();

        return token;
    }

        @Override
        public boolean verifyToken(String userId, String token) {
            String tokenKey="user:"+userId+":trade_code";
            Jedis jedis = redisUtil.getJedis();
            String tokenExists = jedis.get(tokenKey);
            jedis.watch(tokenKey);
            Transaction transaction = jedis.multi();
            if(tokenExists!=null&&tokenExists.equals(token)){
                transaction.del(tokenKey);
            }
            List<Object> list = transaction.exec();
            if(list!=null&&list.size()>0&&(Long)list.get(0)==1L){
                return true;
            }else{
                return false;
            }

        }

}
