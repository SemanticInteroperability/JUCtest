package com.atguigu.sync;


class Share{
    private int number = 0;

    public synchronized void incr() throws InterruptedException {
        //if会出现虚假唤醒,换成while
        while (number != 0){//1.判断number值是否是0，如果不是就等待
            this.wait();
        }
        //2.执行+1
        number++;
        System.out.println(Thread.currentThread().getName()+":: "+number);
        //3.通知其他线程
        this.notifyAll();
    }

    public synchronized void dec() throws InterruptedException {
        while (number!=1){
            this.wait();
        }
        number--;
        System.out.println(Thread.currentThread().getName()+":: "+number);
        //通知其他线程
        this.notifyAll();
    }
}
public class ThreadDemo01 {

    public static void main(String[] args) {
        Share share = new Share();

        new Thread(() -> {
            for (int i = 0; i <10 ; i++) {
                try {
                    share.incr();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"AA").start();


        new Thread(() -> {
            for (int i = 0; i <10 ; i++) {
                try {
                    share.dec();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"BB").start();


    }
}
