package com.atguigu.callable;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


class MyThread1 implements Runnable{

    //无返回值
    @Override
    public void run() {

    }
}

class MyThread2 implements Callable{

    //有返回值 计算结果，如果无法计算结果，则抛出一个异常。
    @Override
    public Integer call() throws Exception {
        return 1024;
    }
}

public class Demo01 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //FutureTask实现了Runnable接口，构造函数 public FutureTask(Callable<V> callable)
        //单独开启线程，执行异步回调
        //开启子线程计算，存放计算完成后的结果，等需要的时候直接获取，而不需要每次都去计算
        //子线程会将call方法的返回值存着, 需要的时候get, 不用每次都重新执行call方法
        FutureTask<Integer> futureTask = new FutureTask<Integer>(() -> 1024);

        Thread thread = new Thread(futureTask, "FMT");
        thread.start();

        while(!futureTask.isDone()){
            System.out.println("wait..");
        }

        //调用FutureTask的get方法
        System.out.println(futureTask.get());

        if (futureTask.isDone()){
            System.out.println("ok..");
        }

        System.out.println(futureTask.get());

        System.out.println(Thread.currentThread().getName()+" over");
    }
}
