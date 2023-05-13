package com.atguigu.lock;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ShareResource{
    private int flag = 1;

    private Lock lock = new ReentrantLock();

    //精准定向唤醒
    private Condition c1 = lock.newCondition();
    private Condition c2 = lock.newCondition();
    private Condition c3 = lock.newCondition();

    //打印5次
    public void print5(int loop) throws InterruptedException {
        lock.lock();

        try{
            while (flag != 1){
                c1.await();
            }
            for (int i = 0; i < 5; i++) {
                System.out.println(Thread.currentThread().getName()+" :: "+i+" :"+"轮数："+loop);
            }
            //修改标志位
            flag=2;
            //通知BB线程
            c2.signal();
        }finally {
            lock.unlock();
        }
    }

    //打印10次
    public void print10(int loop) throws InterruptedException {
        lock.lock();

        try{
            while (flag != 2){
                c2.await();
            }
            for (int i = 0; i < 10; i++) {
                System.out.println(Thread.currentThread().getName()+" :: "+i+" :"+"轮数："+loop);
            }
            //修改标志位
            flag=3;
            //通知CC线程
            c3.signal();
        }finally {
            lock.unlock();
        }
    }

    //打印10次
    public void print15(int loop) throws InterruptedException {
        lock.lock();

        try{
            while (flag != 3){
                c3.await();
            }
            for (int i = 0; i < 15; i++) {
                System.out.println(Thread.currentThread().getName()+" :: "+i+" :"+"轮数："+loop);
            }
            //修改标志位
            flag=1;
            //通知AA线程
            c1.signal();
        }finally {
            lock.unlock();
        }
    }


}
public class TreadDemo03 {

    public static void main(String[] args) {

        ShareResource resource = new ShareResource();
        new Thread(() ->{
            for (int i = 0; i < 10; i++) {
                try {
                    resource.print5(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"AA").start();

        new Thread(() ->{
            for (int i = 0; i < 10; i++) {
                try {
                    resource.print10(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"BB").start();

        new Thread(() ->{
            for (int i = 0; i < 10; i++) {
                try {
                    resource.print15(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"CC").start();

    }
}
