package com.atguigu.pool;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


@Slf4j(topic="c.TestPool")
public class TestPool {
//    private static final Logger log = LoggerFactory.getLogger(TestPool.class);
    public static void main(String[] args) {
        ThreadPool threadPool = new ThreadPool(1, 1000, TimeUnit.MILLISECONDS, 1,((queue, task) -> queue.put(task)));
        for (int i = 0; i < 3; i++) {
            int j=i;
            threadPool.execute(()->{
                try {
                    Thread.sleep(1000000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(j);
                log.debug("{}",j);
            });
        }
    }
}

//当阻塞队列满了，
// 1.死等
//2.带超时等待
//3.让调用者放弃任务执行
//4.让调用者抛出异常
//5.让调用者自己执行任务
@FunctionalInterface  //拒绝策略  策略模式
interface RejectPolicy<T>{
    void reject(BlockingQueue<T> queue,T task);
}


@Slf4j(topic="c.ThreadPool")
class ThreadPool{
    //private static final Logger log = LoggerFactory.getLogger(ThreadPool.class);
    //任务队列
    private BlockingQueue<Runnable> taskQueue;

    //线程集合
    private HashSet<Worker> workers = new HashSet();

    //核心线程数
    private int coreSize;

    private long timeOut;

    private TimeUnit timeUnit;

    private RejectPolicy<Runnable> rejectPolicy;

    public ThreadPool(int coreSize, long timeOut, TimeUnit timeUnit,int queueCapacity,RejectPolicy<Runnable> rejectPolicy) {
        this.coreSize = coreSize;
        this.timeOut = timeOut;
        this.timeUnit = timeUnit;
        this.taskQueue = new BlockingQueue<>(queueCapacity);
        this.rejectPolicy = rejectPolicy;
    }

    //执行任务
    public void execute(Runnable task){
        //当任务数没有超过coreSize时，直接交给worker对象执行
        //如果任务数超过coreSize，加入任务队列暂存
        synchronized (workers){
            if (workers.size() < coreSize){
                Worker worker = new Worker(task);
                log.debug("新增worker{}",worker);
                workers.add(worker);
                worker.start();
            }else {
                //log.debug("加入任务队列{}",task);
                //taskQueue.put(task);
                taskQueue.tryPut(rejectPolicy,task);
            }
        }
    }

    class Worker extends Thread{
        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
          //执行任务
            //1.当task不为空，执行任务
            //2.当task执行完毕，再接着从任务队列获取任务并执行
            while (task!=null || (task = taskQueue.poll(timeOut,timeUnit))!=null){
                try {
                    log.debug("正在执行...{}",task);
                    task.run();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    task = null;
                }
            }
            synchronized (workers){
                workers.remove(this);

                log.debug("worker被移除{}",this);
            }
        }
    }
}

//自定义线程池
@Slf4j(topic = "c.BlockingQueue")
class BlockingQueue<T>{

    //任务队列
    private Deque<T> queue = new ArrayDeque<T>();

    private ReentrantLock lock = new ReentrantLock();

    //生产者条件变量
    private Condition fullWaitSet = lock.newCondition();

    //消费者条件变量
    private Condition emptyWaitSet = lock.newCondition();

    //容量
    private int capacity;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    //带超时的阻塞获取
    public T poll(long timeOut, TimeUnit unit){
        lock.lock();
        try {
            //将timeOut 统一转换为纳秒
            long nanos = unit.toNanos(timeOut);
            while (queue.isEmpty()){
                try {
                    if (nanos<=0){
                        return null;
                    }
                    //返回的是剩余时间
                    nanos = emptyWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    //阻塞获取
    public T take(){
        lock.lock();
        try {
            while(queue.isEmpty()){
                try {
                    emptyWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }

    }

    //阻塞添加
    public void put(T element){
        lock.lock();
        try {
            while(queue.size() == capacity){
                try {
                    log.debug("等待加入任务队列{}。。。",element);
                    fullWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.addLast(element);
            emptyWaitSet.signal();
        } finally {
            lock.unlock();
        }
    }

    //带超时时间的阻塞添加
    public boolean offer(T task,long timeout,TimeUnit timeUnit){
        lock.lock();
        try {
            long nanos = timeUnit.toNanos(timeout);
            while(queue.size() == capacity){
                try {
                    log.debug("等待加入任务队列{}。。。",task);
                    if (nanos<=0){
                        return false;
                    }
                    nanos = fullWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("加入任务队列{}。。。",task);
            queue.addLast(task);
            emptyWaitSet.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    //大小
    public int size(){
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }


    public void tryPut(RejectPolicy<T> rejectPolicy, T task) {
        lock.lock();
        try {
            if (queue.size() == capacity){
                rejectPolicy.reject(this,task);
            }else{
                log.debug("加入任务队列{}。。。",task);
                queue.addLast(task);
                emptyWaitSet.signal();
            }
        } finally {
            lock.unlock();
        }
    }
}
