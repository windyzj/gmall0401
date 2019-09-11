package com.atguigu.gmall0401.manage.mapper;

import com.atguigu.gmall0401.bean.SkuSaleAttrValue;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SkuSaleAttrValueMapper extends Mapper<SkuSaleAttrValue> {

    public List<Map> getSaleAttrValuesBySpu(String spuId);
}
