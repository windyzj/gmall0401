package com.atguigu.gmall0401.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall0401.bean.*;
import com.atguigu.gmall0401.manage.mapper.*;
import com.atguigu.gmall0401.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;


@Service
public class ManageServiceImpl implements ManageService {

    @Autowired
    BaseAttrValueMapper baseAttrValueMapper;

    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    BaseCatalog1Mapper baseCatalog1Mapper;

    @Autowired
    BaseCatalog2Mapper baseCatalog2Mapper;

    @Autowired
    BaseCatalog3Mapper  baseCatalog3Mapper;
    @Override
    public List<BaseCatalog1> getCatalog1() {
        return baseCatalog1Mapper.selectAll();
    }

    @Override
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {
        BaseCatalog2 baseCatalog2 = new BaseCatalog2();
        baseCatalog2.setCatalog1Id(catalog1Id);
        List<BaseCatalog2> baseCatalog2List = baseCatalog2Mapper.select(baseCatalog2);
        return baseCatalog2List;
    }

    @Override
    public List<BaseCatalog3> getCatalog3(String catalog2Id) {
        BaseCatalog3 baseCatalog3 = new BaseCatalog3();
        baseCatalog3.setCatalog2Id(catalog2Id);
        List<BaseCatalog3> baseCatalog3List = baseCatalog3Mapper.select(baseCatalog3);
        return baseCatalog3List;
    }

    @Override
    public List<BaseAttrInfo> getAttrList(String catalog3Id) {

        Example example = new Example(BaseAttrInfo.class);
        example.createCriteria().andEqualTo("catalog3Id",catalog3Id);

        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoMapper.selectByExample(example);

        return baseAttrInfoList;
    }

    @Override
    public BaseAttrInfo getBaseAttrInfo(String attrId) {
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectByPrimaryKey(attrId);

        BaseAttrValue baseAttrValueQuery=new BaseAttrValue();
        baseAttrValueQuery.setAttrId(attrId);
        List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.select(baseAttrValueQuery);

        baseAttrInfo.setAttrValueList(baseAttrValueList);

        return baseAttrInfo;
    }

    @Override
    @Transactional
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        if(baseAttrInfo.getId()!=null &&baseAttrInfo.getId().length()>0){
            baseAttrInfoMapper.updateByPrimaryKeySelective(baseAttrInfo);
        }else{
            baseAttrInfo.setId(null);
            baseAttrInfoMapper.insertSelective(baseAttrInfo);
        }
        Example example = new Example(BaseAttrValue.class);
        example.createCriteria().andEqualTo("attrId",baseAttrInfo.getId());
        //根据attrid先全部删除，再统一保存
        baseAttrValueMapper.deleteByExample(example);

        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue baseAttrValue : attrValueList) {
            String id = baseAttrInfo.getId();
            baseAttrValue.setAttrId(id);
            baseAttrValueMapper.insertSelective(baseAttrValue);
        }


    }
}
