package com.atguigu.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolDemo1 {

    public static void main(String[] args) {
        //一池5线程
        ExecutorService threadPool1 = Executors.newFixedThreadPool(5);

        //一池1线程
        ExecutorService threadPool2 = Executors.newSingleThreadExecutor();

        //一池可扩容线程
        ExecutorService threadPool3 = Executors.newCachedThreadPool();
        try {
            for (int i = 1; i <= 10; i++) {
                threadPool3.execute(() ->{
                    System.out.println(Thread.currentThread().getName()+"办理业务");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPool3.shutdown();
        }
    }
}
