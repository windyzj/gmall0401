package com.atguigu.gmall0401.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall0401.bean.CartInfo;
import com.atguigu.gmall0401.bean.SkuInfo;
import com.atguigu.gmall0401.cart.mapper.CartInfoMapper;
import com.atguigu.gmall0401.service.CartService;
import com.atguigu.gmall0401.service.ManageService;
import com.atguigu.gmall0401.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.*;


@Service
public class CartServiceImpl implements CartService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    CartInfoMapper cartInfoMapper;

    @Reference
    ManageService manageService;

    @Override
    public CartInfo addCart(String userId, String skuId, Integer num) {


        // 加数据库
        // 尝试取出已有的数据    如果有  把数量更新 update   如果没有insert
        CartInfo cartInfoQuery=new CartInfo();
        cartInfoQuery.setSkuId(skuId);
        cartInfoQuery.setUserId(userId);
        CartInfo cartInfoExists=null;
          cartInfoExists = cartInfoMapper.selectOne(cartInfoQuery);
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        if(cartInfoExists!=null){
            cartInfoExists.setSkuName(skuInfo.getSkuName());
            cartInfoExists.setCartPrice(skuInfo.getPrice());
            cartInfoExists.setSkuNum(cartInfoExists.getSkuNum()+num);
            cartInfoExists.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfoMapper.updateByPrimaryKeySelective(cartInfoExists);
        }else{
            CartInfo cartInfo = new CartInfo();
            cartInfo.setSkuId(skuId);
            cartInfo.setUserId(userId);
            cartInfo.setSkuNum(num);
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuPrice(skuInfo.getPrice());

            cartInfoMapper.insertSelective(cartInfo);
            cartInfoExists=cartInfo;
        }

      //  Jedis jedis = redisUtil.getJedis();
        // 加缓存
        //   type  hash     key   cart:101:info     field   skuId   value   cartInfoJson
        //   如果购物车中已有该sku  增加个数  如果没有新增一条
/*        String cartKey="cart:"+userId+":info";
        String cartInfoJson = JSON.toJSONString(cartInfoExists);
        jedis.hset(cartKey,skuId,cartInfoJson) ;//新增 也可以覆盖*/

       // jedis.close();
        loadCartCache(userId);

        return cartInfoExists;

    }

    @Override
    public List<CartInfo> cartList(String userId) {
        //先查缓存
        Jedis jedis = redisUtil.getJedis();
        String cartKey="cart:"+userId+":info";
        List<String> cartJsonList = jedis.hvals(cartKey);
        List<CartInfo> cartList=new ArrayList<>();
        if(cartJsonList!=null&&cartJsonList.size()>0){  //缓存命中
            for (String cartJson : cartJsonList) {
                CartInfo cartInfo = JSON.parseObject(cartJson, CartInfo.class);
                cartList.add(cartInfo);
            }

            cartList.sort(new Comparator<CartInfo>() {
                @Override
                public int compare(CartInfo o1, CartInfo o2) {
                    return o2.getId().compareTo(o1.getId());
                }
            });

            return    cartList;
        }else {
            //缓存未命中  //缓存没有查数据库 ，同时加载到缓存中
            return loadCartCache(userId);
        }

    }

    /**
     * 合并购物车
     * @param userIdDest
     * @param userIdOrig
     * @return
     */
    @Override
    public List<CartInfo> mergeCartList(String userIdDest, String userIdOrig) {
        //1 先做合并
        cartInfoMapper.mergeCartList(userIdDest,userIdOrig);
        // 2 合并后把临时购物车删除
        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userIdOrig);
        cartInfoMapper.delete(cartInfo);
        // 3 重新读取数据 加载缓存
        List<CartInfo> cartInfoList = loadCartCache(userIdDest);

        return cartInfoList;
    }

    /**
     *  缓存没有查数据库 ，同时加载到缓存中
     * @param userId
     * @return
     */
    public List<CartInfo>  loadCartCache(String userId){
        // 读取数据库
        List<CartInfo> cartInfoList = cartInfoMapper.selectCartListWithSkuPrice(userId);
        //加载到缓存中
        //为了方便插入redis  把list --> map
        if(cartInfoList!=null&&cartInfoList.size()>0) {
            Map<String, String> cartMap = new HashMap<>();
            for (CartInfo cartInfo : cartInfoList) {
                cartMap.put(cartInfo.getSkuId(), JSON.toJSONString(cartInfo));
            }
            Jedis jedis = redisUtil.getJedis();
            String cartKey = "cart:" + userId + ":info";
            jedis.del(cartKey);
            jedis.hmset(cartKey, cartMap);                // hash
            jedis.expire(cartKey, 60 * 60 * 24);
            jedis.close();
        }
        return  cartInfoList;

    }
}
