package com.atguigu.gmall0401.passport.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0401.bean.UserInfo;
import com.atguigu.gmall0401.service.UserService;
import com.atguigu.gmall0401.util.JwtUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {

    @Reference
    UserService userService;

     String jwtKey="atguigu";


    @GetMapping("index")
    public String index(@RequestParam("originUrl") String originUrl, Model model){
        model.addAttribute("originUrl",originUrl);
        return "index";
    }


    @PostMapping("login")
    @ResponseBody
    public String login(UserInfo userInfo, HttpServletRequest request){

      UserInfo userInfoExist=  userService.login(userInfo);
      //1
        if(userInfoExist!=null){
            //制作token
            Map<String, Object> map = new HashMap<>();
            map.put("userId",userInfoExist.getId());
            map.put("nickName",userInfoExist.getNickName());
            System.out.println(request.getRemoteAddr());
//            // 如果有 反向代理的话  ，要在反向代理中进行配置  把用户真实ip传递过来

            String ipAddr = request.getHeader("X-forwarded-for");
            String token = JwtUtil.encode(jwtKey, map, ipAddr);

            return  token;
        }



      return  "fail";
    }


    @Test
    public void testJwt(){
        Map<String, Object> map = new HashMap<>();
        map.put("userId","123");
        map.put("nickName","zhang3");
        String token = JwtUtil.encode("atguigu", map, "192.168.11.120");
        System.out.println(token);
        Map<String, Object> map1 = JwtUtil.decode(token, "atguigu", "192.168.11.120");

        System.out.println(map1);
    }
    // http://passport.gmall.com/verify?token=xxxx&currentIP=xxxx
    @GetMapping("verify")
    @ResponseBody
    public  String verify(@RequestParam("token") String token, @RequestParam("currentIP") String currentIp){
        //1 验证token
        Map<String, Object> userMap = JwtUtil.decode(token, jwtKey, currentIp);

        //2 验证缓存
        if(userMap!=null){
            String userId = (String)userMap.get("userId");
            Boolean isLogin= userService.verify(userId);
            if(isLogin){
                return "success";
            }
        }
        return "fail";

    }




}
