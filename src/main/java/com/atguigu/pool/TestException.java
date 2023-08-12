package com.atguigu.pool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

//用callable得到异常
@Slf4j
public class TestException {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        Future<Boolean> future = pool.submit(() -> {
            log.debug("task1");
            int i = 1/0;
            return true;
        });
        log.debug("result:{}",future.get());
    }
}
