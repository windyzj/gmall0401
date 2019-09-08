package com.atguigu.gmall0401.service;

import com.atguigu.gmall0401.bean.*;

import java.util.List;

public interface ManageService {

    //查询一级分类
    public List<BaseCatalog1> getCatalog1();



    //查询二级分类 根据一级分类ID
    public List<BaseCatalog2> getCatalog2(String catalog1Id);


    //查询三级分类 根据二级分类ID
    public List<BaseCatalog3> getCatalog3(String catalog2Id);

    //根据三级分类查询平台属性
    public List<BaseAttrInfo> getAttrList(String catalog3Id);

    //根据平台属性id 查询平台属性的详情 顺便把该属性的属性值列表也取到
    public  BaseAttrInfo getBaseAttrInfo(String attrId);


    //保存平台属性
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo);


    //删除平台属性

    //获得基本销售属性
    public List<BaseSaleAttr> getBaseSaleAttrList();

    //保存spu信息
    public void saveSpuInfo(SpuInfo spuInfo);

    //根据三级分类查询spu列表
    public List<SpuInfo> getSpuList(String catalog3Id);


}
