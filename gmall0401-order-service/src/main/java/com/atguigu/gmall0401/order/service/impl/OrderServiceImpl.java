package com.atguigu.gmall0401.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall0401.bean.OrderDetail;
import com.atguigu.gmall0401.bean.OrderInfo;
import com.atguigu.gmall0401.enums.OrderStatus;
import com.atguigu.gmall0401.enums.ProcessStatus;
import com.atguigu.gmall0401.order.mapper.OrderDetailMapper;
import com.atguigu.gmall0401.order.mapper.OrderInfoMapper;
import com.atguigu.gmall0401.service.OrderService;
import com.atguigu.gmall0401.util.RedisUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import sun.misc.UUDecoder;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

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



    @Override
    public void updateStatus(String orderId, ProcessStatus processStatus, OrderInfo... orderInfos) {
        OrderInfo orderInfo = new OrderInfo();
        if(orderInfos!=null&& orderInfos.length>0 ){ //如果还需要附带更新其他订单信息则 使用可变参数中的订单信息
            orderInfo=orderInfos[0];
        }
        orderInfo.setProcessStatus(processStatus);
        orderInfo.setOrderStatus(processStatus.getOrderStatus());
        orderInfo.setId(orderId);
        orderInfoMapper.updateByPrimaryKeySelective(orderInfo);

    }

    public List<Integer> checkExpiredCoupon(){
        return Arrays.asList(1,2,3,4,5,6,7);
    }


    @Override
    public List<OrderInfo> getOrderListByUser(String userId) {
        // 优先去查缓存
        //缓存未命中 去查库

        Example example=new Example(OrderInfo.class);
        example.setOrderByClause("id desc");
        example.createCriteria().andEqualTo("userId",userId);

        List<OrderInfo> orderInfoList = orderInfoMapper.selectByExample(example);
        for (Iterator<OrderInfo> iterator = orderInfoList.iterator(); iterator.hasNext(); ) {
            OrderInfo orderInfo = iterator.next();
            OrderDetail orderDetailQuery=new OrderDetail();
            orderDetailQuery.setOrderId(orderInfo.getId());
            List<OrderDetail> orderDetailList = orderDetailMapper.select(orderDetailQuery);
            orderInfo.setOrderDetailList(orderDetailList);

            if(orderInfo.getOrderStatus()== OrderStatus.SPLIT){  //如果订单被拆分则 循环插入子订单
                List<OrderInfo> orderSubList = new ArrayList<>();
                for (OrderInfo subOrderInfo : orderInfoList) {
                    if(orderInfo.getId().equals(subOrderInfo.getParentOrderId())){
                        orderSubList.add(subOrderInfo);

                    }
                }
                orderInfo.setOrderSubList(orderSubList);
            }

        }

        return orderInfoList;
    }

    @Override
    public List<Map> orderSplit(String orderId, String wareSkuMapJson) {
        // 1  先用orderId 查询出 原始订单
        OrderInfo orderInfoParent = getOrderInfo(orderId);

        // 2  wareSkuMap => list   循环这个list
        List<Map> mapList = JSON.parseArray(wareSkuMapJson, Map.class);

        List<Map> wareParamMapList=new ArrayList<>();
        // 循环一次 ：  生成一个订单    订单分成2个部分   主订单 orderInfo  订单明细 orderDetail
        for (Map wareSkuMap : mapList) {
            //  3 子订单 订单主表 orderInfo   拷贝一份主订单信息    待修改 金额  id parent_order_id
            OrderInfo orderInfoSub = new OrderInfo();
            try {
                BeanUtils.copyProperties(orderInfoSub,orderInfoParent);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }


            //  4   子订单  订单明细  orderDetail
            List<String > skuIdList = (List<String >)wareSkuMap.get("skuIds");  // 传过来的拆单方案
            List<OrderDetail> orderDetailList = orderInfoParent.getOrderDetailList(); //现有的父订单的所有订单明细
            ArrayList<OrderDetail> orderDetailSubList = new ArrayList<>();  // 我希望存放子订单的明细
            for (String skuId : skuIdList) {
                for (OrderDetail orderDetail : orderDetailList) {
                    if(skuId.equals(orderDetail.getSkuId())){
                        OrderDetail orderDetailSub = new OrderDetail();
                        try {
                            BeanUtils.copyProperties(orderDetailSub,orderDetail);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        orderDetailSub.setId(null);
                        orderDetailSub.setOrderId(null);
                        orderDetailSubList.add(orderDetailSub);
                    }
                }
            }

            //  5 组合完成一个子订单    修改 金额  id 清空 parent_order_id   保存 子订单
            orderInfoSub.setOrderDetailList(orderDetailSubList);
            orderInfoSub.setId(null);
            orderInfoSub.sumTotalAmount();
            orderInfoSub.setParentOrderId(orderInfoParent.getId());

            saveOrder(orderInfoSub);


            //  6 把子订单 包装成为 库存模块 需要的结构  map

            Map wareParamMap = initWareParamJsonFormOrderInfo(orderInfoSub);
            wareParamMap.put("wareId",wareSkuMap.get("wareId"));

            wareParamMapList.add(wareParamMap);

            // 7 原始订单  的状态改为已拆分

            updateStatus(orderId,ProcessStatus.SPLIT);
        }

         //  组合成为List<Map> 返回


        return wareParamMapList;
    }

    @Async
    public void handleExpiredCoupon(Integer id){
        try {
            System.out.println("购物券："+ id +"发送用户");
            Thread.sleep(1000);

            System.out.println("购物券："+ id +"删除");
            Thread.sleep(1000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    /**
     * 初始化 发送到库存系统中的参数
     * @param orderId
     * @return
     */
    public   Map initWareParamJson(String orderId){
        OrderInfo orderInfo = getOrderInfo(orderId);

        Map map = initWareParamJsonFormOrderInfo(orderInfo);
        return  map;

    }

    private  Map initWareParamJsonFormOrderInfo(OrderInfo orderInfo) {
        Map  paramMap=new HashMap();

        paramMap.put("orderId",orderInfo.getId());
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
        return paramMap;
    }

}
