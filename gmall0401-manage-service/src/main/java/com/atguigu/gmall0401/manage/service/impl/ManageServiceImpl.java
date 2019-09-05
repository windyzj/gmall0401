package com.atguigu.gmall0401.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall0401.bean.BaseCatalog1;
import com.atguigu.gmall0401.bean.BaseCatalog2;
import com.atguigu.gmall0401.bean.BaseCatalog3;
import com.atguigu.gmall0401.manage.mapper.BaseCatalog1Mapper;
import com.atguigu.gmall0401.manage.mapper.BaseCatalog2Mapper;
import com.atguigu.gmall0401.manage.mapper.BaseCatalog3Mapper;
import com.atguigu.gmall0401.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Service
public class ManageServiceImpl implements ManageService {


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
}
