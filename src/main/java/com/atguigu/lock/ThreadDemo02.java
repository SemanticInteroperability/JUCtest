package com.atguigu.lock;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Share2{
    private int number = 0;

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void incr() throws InterruptedException {
        lock.lock();
        try {
            while(number!=0){
                condition.await();
            }
            number++;
            System.out.println(Thread.currentThread().getName()+":: "+number);
            condition.signalAll();
        }finally {
            lock.unlock();
        }
    }

    public void dec() throws InterruptedException {
        lock.lock();
        try {
            while(number!=1){
                condition.await();
            }
            number--;
            System.out.println(Thread.currentThread().getName()+":: "+number);
            condition.signalAll();
        }finally {
            lock.unlock();
        }
    }
}


public class ThreadDemo02 {
    public static void main(String[] args) {
        Share2 share = new Share2();
        new Thread(() ->{
            for (int i = 0; i <10 ; i++) {
                try {
                    share.incr();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"AA").start();

        new Thread(() ->{
            for (int i = 0; i <10 ; i++) {
                try {
                    share.dec();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"BB").start();

        new Thread(() ->{
            for (int i = 0; i <10 ; i++) {
                try {
                    share.incr();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"CC").start();

        new Thread(() ->{
            for (int i = 0; i <10 ; i++) {
                try {
                    share.dec();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"DD").start();


    }

}
