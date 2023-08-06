package com.atguigu.lock;

import java.util.concurrent.locks.ReentrantLock;

//资源类
class LTicket{

    private int number = 30;

    //创建可重入锁 公平锁与非公平锁
    private final ReentrantLock lock = new ReentrantLock(true);

    public  void sale(){
        lock.lock();

        try {
            if (number>0){
                System.out.println(Thread.currentThread().getName()+" : 卖出： "+(number--)+ " 剩下： "+number);
            }
        } finally {
            lock.unlock();
        }
    }
}


public class LsaleTicket {
    public static void main(String[] args) {
        LTicket ticket = new LTicket();

        new Thread(() ->{
            for (int i = 0; i <40 ; i++) {
                ticket.sale();
            }
        },"aa").start();

        new Thread(() ->{
            for (int i = 0; i <40 ; i++) {
                ticket.sale();
            }
        },"bb").start();

        new Thread(() ->{
            for (int i = 0; i <40 ; i++) {
                ticket.sale();
            }
        },"cc").start();
    }
}
