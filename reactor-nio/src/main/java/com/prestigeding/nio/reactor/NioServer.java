package com.prestigeding.nio.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NioServer {

    private static final int SERVER_PORT = 9080;

    public static void main(String[] args) {
        (new Thread(new Acceptor())).start();
    }

    private static class Acceptor implements Runnable {

        // main Reactor 线程池，用于处理客户端的连接请求
        private static ExecutorService mainReactor = Executors.newSingleThreadExecutor(new ThreadFactory() {
            private AtomicInteger num = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("main-reactor-" + num.incrementAndGet());
                return t;
            }
        });

        public void run() {
            ServerSocketChannel ssc = null;
            try {
                ssc = ServerSocketChannel.open();
                ssc.configureBlocking(false);
                ssc.bind(new InetSocketAddress(SERVER_PORT));

                //转发到 MainReactor反应堆
                dispatch(ssc);
                System.out.println("服务端成功启动。。。。。。");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void dispatch(ServerSocketChannel ssc) {
            mainReactor.submit(new MainReactor(ssc));
        }

    }



}
