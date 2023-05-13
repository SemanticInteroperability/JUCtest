package com.atguigu.readwrite;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//todo
//写锁降级为读锁:  获取写锁 -> 获取读锁 -> 释放写锁 -> 释放读锁
//意思是保证写完后可以立刻被读，因为写完后可能被其他写锁上锁导致无法读，所以先让读锁上锁防止其他写锁上锁而不能读
//先读不释放的话，线程就会一直等待，导致写锁进不去。
// 但是如果是先写的话不释放，读锁也能进入，而且当写锁释放的时候，这时候就只剩下读锁，所以原来的写锁降级成了读锁
class MyCache{
    private volatile Map<String,Object> map = new HashMap<>();

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public void put(String key,Object value){

        //写锁
        readWriteLock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName()+"正在写操作");
            TimeUnit.MILLISECONDS.sleep(300);
            map.put(key,value);
            System.out.println(Thread.currentThread().getName()+"写完了"+ key);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            readWriteLock.writeLock().unlock();
        }


    }

    public Object get(String key){
        Object result = null;

        //读锁
        readWriteLock.readLock().lock();

        try {
            System.out.println(Thread.currentThread().getName()+"正在读取操作");
            TimeUnit.MILLISECONDS.sleep(300);
            result = map.get(key);
            System.out.println(Thread.currentThread().getName()+"取完了"+ key);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            readWriteLock.readLock().unlock();
        }
        return  result;
    }
}
public class ReadWriteLockDemo {
    public static void main(String[] args) {
        MyCache myCache = new MyCache();

        //创建线程放数据
        for (int i = 1; i <= 5; i++) {
            final int num = i;
            new Thread(()->{
                myCache.put(num+"",num+"");
                //myCache.get(num+"");
            },String.valueOf(i)).start();
        }


        //创建线程读取数据
        for (int i = 1; i <= 5; i++) {
            final int num = i;
            new Thread(()->{
                myCache.get(num+"");
            },String.valueOf(i)).start();
        }

    }
}
