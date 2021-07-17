package com.prestigeding.nio.reactor;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class SubReactorThreadGroup {
    private static final AtomicInteger requestCounter = new AtomicInteger();  //请求计数器
    private final int ioThreadCount;  // 线程池IO线程的数量
    private final int businessTheadCout; // 业务线程池大小
    private static final int DEFAULT_NIO_THREAD_COUNT;
    private SubReactorThread[] ioThreads;
    private ExecutorService businessExecutePool; //业务线程池

    static {
        DEFAULT_NIO_THREAD_COUNT = 4;
    }

    public SubReactorThreadGroup() {
        this(DEFAULT_NIO_THREAD_COUNT);
    }
    public SubReactorThreadGroup(int ioThreadCount) {
        if(ioThreadCount < 1) {
            ioThreadCount = DEFAULT_NIO_THREAD_COUNT;
        }
        //暂时固定为10
        businessTheadCout = 10;
        businessExecutePool = Executors.newFixedThreadPool(businessTheadCout, new ThreadFactory() {
            private AtomicInteger num = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("bussiness-thread-" + num.incrementAndGet());
                return t;
            }
        });
        this.ioThreadCount = ioThreadCount;
        this.ioThreads = new SubReactorThread[ioThreadCount];
        for(int i = 0; i < ioThreadCount; i ++ ) {
            this.ioThreads[i] = new SubReactorThread(businessExecutePool);
            this.ioThreads[i].start(); //构造方法中启动线程，由于nioThreads不会对外暴露，故不会引起线程逃逸
        }
        System.out.println("Nio 线程数量：" + ioThreadCount);
    }
    public void dispatch(SocketChannel socketChannel) {
        if(socketChannel != null ) {
            next().register(new NioTask(socketChannel, SelectionKey.OP_READ));
        }
    }
    protected SubReactorThread next() {
        return this.ioThreads[ requestCounter.getAndIncrement() %  ioThreadCount ];
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }
}
