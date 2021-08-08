package com.huazan;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class DemoMain {

    static DateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static Executor executor = new ThreadPoolExecutor(2,2, 1,
            TimeUnit.MILLISECONDS,new SynchronousQueue<>(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public static void main(String[] args) {
        /*List<Integer> ages = initDate();
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                doGrabOrder(ages);
            }
        }, 1, 5, TimeUnit.SECONDS);*/
        Random random = new Random();
        for(int i = 1; i<=20;i++){
            System.out.println(random.nextInt(6)+1);
        }
    }

    private static List<Integer> initDate() {
        List ages = new ArrayList();
        for(int i = 1; i<=100; i++){
            ages.add(i);
        }
        return ages;
    }

    private static void doGrabOrder(List<Integer> ages){
        System.out.println( "str ---->"+ dateTime.format(new Date()));
        for(Integer age : ages){
            executor.execute(()->{
                    doSomething(age);
            });
        }

    }

    private static void doSomething(Integer age) {
        System.out.println(Thread.currentThread().getName()+ "执行的age："+age);
        try {
            Thread.sleep(500l);
        }catch (Exception e){

        }
    }




}
