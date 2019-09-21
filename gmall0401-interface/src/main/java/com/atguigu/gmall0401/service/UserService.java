package com.atguigu.gmall0401.service;

import com.atguigu.gmall0401.bean.UserAddress;
import com.atguigu.gmall0401.bean.UserInfo;

import java.util.List;

public interface UserService {

    List<UserInfo> getUserInfoListAll();

    void addUser(UserInfo userInfo);

    void updateUser(UserInfo userInfo);

    void updateUserByName(String name,UserInfo userInfo);

    void delUser(UserInfo userInfo);

    UserInfo getUserInfoById(String id);


    UserInfo  login(UserInfo userInfo);

    Boolean verify(String userId);

    public  List<UserAddress> getUserAddressList(String userId);

}
