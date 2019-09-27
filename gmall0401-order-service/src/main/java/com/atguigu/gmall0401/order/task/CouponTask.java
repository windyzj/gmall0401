package com.atguigu.gmall0401.order.task;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0401.service.OrderService;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

//@Component
//@EnableScheduling
public class CouponTask {


/*    @Scheduled(cron = "0 30 2 21 * ?")   // 每月 月21日 凌晨两点半 执行

    @Scheduled(cron = "0 0 * * * ?")   // 小时执行一次

    @Scheduled(cron = "0 0/30 * * * ?")   // 半小时执行一次

    @Scheduled(cron = "5 * * * * ?")   // 每分钟执行一次 秒钟指向5的时候*/

    @Reference
    OrderService orderService;

    @Scheduled(cron = "0/5 * * * * ?")   //每5秒执行一次
    public void work() throws InterruptedException {
        System.out.println("thread = ===============" + Thread.currentThread());
        List<Integer> integers = orderService.checkExpiredCoupon();
        for (Integer couponId : integers) {
            orderService.handleExpiredCoupon(couponId);
        }
    }


/*    @Scheduled(cron = "0/1 * * * * ?")   //每1秒执行一次
    public void work2() throws InterruptedException {
        System.out.println("thread 2222= ===============" + Thread.currentThread());
    }*/

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5);
        return taskScheduler;
    }




}
