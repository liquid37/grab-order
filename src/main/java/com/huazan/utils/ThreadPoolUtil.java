package com.huazan.utils;

import java.util.concurrent.*;

public class ThreadPoolUtil {

    public static ScheduledExecutorService createScheduledExecutorService(int coreSize){
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        return executorService;
    }

    public static Executor createThreadPoolExecutorCallerRunsPolicy(int coreSize,int maxSize,int second){
        Executor executor = new ThreadPoolExecutor(coreSize,maxSize, second,
                TimeUnit.SECONDS,new SynchronousQueue<>(),
                new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

}
