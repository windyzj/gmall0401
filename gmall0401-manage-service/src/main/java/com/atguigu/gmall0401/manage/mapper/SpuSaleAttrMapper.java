package com.atguigu.gmall0401.manage.mapper;

import com.atguigu.gmall0401.bean.SpuSaleAttr;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpuSaleAttrMapper extends Mapper<SpuSaleAttr> {
    public List<SpuSaleAttr> getSpuSaleAttrListBySpuId(String spuId);
}
