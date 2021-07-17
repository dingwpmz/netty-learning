package com.prestigeding.nio.reactor;

import java.io.IOException;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class MainReactor implements Runnable{
    private Selector selector;
    private SubReactorThreadGroup subReactorThreadGroup;
    private static final int DEFAULT_IO_THREAD_COUNT = 4;
    private int ioThreadCount = DEFAULT_IO_THREAD_COUNT;
    public MainReactor(ServerSocketChannel channel) {
        try {
            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        subReactorThreadGroup = new SubReactorThreadGroup(ioThreadCount);
    }
    public void run() {
        System.out.println("MainReactor is running");
        while (!Thread.interrupted()) {
            Set<SelectionKey> ops = null;
            try {
                selector.select(1000);
                ops = selector.selectedKeys();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 处理相关事件
            for (Iterator<SelectionKey> it = ops.iterator(); it.hasNext();) {
                SelectionKey key = it.next();
                it.remove();
                try {
                    if (key.isAcceptable()) { // 客户端建立连接
                        System.out.println("收到客户端的连接请求。。。");
                        ServerSocketChannel serverSc = (ServerSocketChannel) key.channel();// 这里其实，可以直接使用ssl这个变量
                        SocketChannel clientChannel = serverSc.accept();
                        clientChannel.configureBlocking(false);
                        subReactorThreadGroup.dispatch(clientChannel); // 转发该请求
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    System.out.println("客户端主动断开连接。。。。。。。");
                }
            }
        }
    }
}
