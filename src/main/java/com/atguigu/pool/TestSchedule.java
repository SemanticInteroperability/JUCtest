package com.atguigu.pool;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TestSchedule {
    public static void main(String[] args) {

        //如何让每周星期四18：00：00定时执行任务
        //获取当前时间
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);

        //获取周四的时间
        LocalDateTime time = now.withHour(18).withMinute(0).withSecond(0).withNano(0).with(DayOfWeek.THURSDAY);

        // 如果 当前时间>本周四，必须找到下个周四
        if (now.compareTo(time)>0){
            time = time.plusWeeks(1);
        }
        System.out.println(time);

        long initDelay = Duration.between(now, time).toMillis();
        long period = 1000;
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        pool.scheduleAtFixedRate(()->{
            System.out.println("running..");
        },initDelay, period,TimeUnit.MILLISECONDS);
    }
}
