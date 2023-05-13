package com.atguigu.sync;

//资源类
class Ticket{

    private int number = 30;

    //synchronized的作用是保证在同一时刻， 被修饰的代码块或方法只会有一个线程执行，以达到保证并发安全的效果。
    public synchronized  void sale(){
        if (number>0){
            System.out.println(Thread.currentThread().getName()+" : 卖出： "+(number--)+ " 剩下： "+number);
        }
    }
}
public class SaleTicket {

    public static void main(String[] args) {
        Ticket ticket = new Ticket();
//创建多个线程，调用资源类的操作方法
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <40 ; i++) {
                    ticket.sale();
                }
            }
        },"AA").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <40 ; i++) {
                    ticket.sale();
                }
            }
        },"BB").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <40 ; i++) {
                    ticket.sale();
                }
            }
        },"CC").start();
    }
}
