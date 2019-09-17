package com.atguigu.gmall0401.service;

import com.atguigu.gmall0401.bean.SkuLsInfo;
import com.atguigu.gmall0401.bean.SkuLsParams;
import com.atguigu.gmall0401.bean.SkuLsResult;

public interface ListService {

    public void saveSkuLsInfo(SkuLsInfo skuLsInfo) ;

    public SkuLsResult getSkuLsInfoList(SkuLsParams skuLsParams );

    public  void incrHotScore(String skuId);
}
