package com.huazan;

import com.huazan.service.ISystemGrabOrderService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;

@Configuration
@ComponentScan(value = "com.huazan")
public class GrabOrdersMain {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new AnnotationConfigApplicationContext(GrabOrdersMain.class);
        System.out.println("******************************************");
        System.out.println("进入华赞抓单系统");
        System.out.println("请选择抓单的网站");
        System.out.println("1.同城");
        System.out.println("2.深度");
        System.out.println("按数字 + 回车进行选择，其他为退出");
        Scanner sc = new Scanner(System.in);
        int spiderSystem = sc.nextInt();
        ISystemGrabOrderService grabOrderService = null;
        if (spiderSystem == 1) {
            grabOrderService = context.getBean("sameSystem",ISystemGrabOrderService.class);
        } else if (spiderSystem == 2) {
            grabOrderService = context.getBean("deepSystem",ISystemGrabOrderService.class);
        } else {
            System.exit(0);
        }
        //while (true) {
            grabOrderService.grabOrder();
        //}
    }
}

