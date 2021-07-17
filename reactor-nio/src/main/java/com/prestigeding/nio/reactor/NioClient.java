package com.prestigeding.nio.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioClient {


    public static void main(String[] args) {
        SocketChannel clientClient;
        Selector selector = null;
        try {
            clientClient = SocketChannel.open();
            clientClient.configureBlocking(false);
            selector = Selector.open();
            clientClient.register(selector, SelectionKey.OP_CONNECT);
            clientClient.connect(new InetSocketAddress("127.0.0.1",9080));
            Set<SelectionKey> ops = null;
            while(true) {
                try {
                    selector.select();
                    ops = selector.selectedKeys();
                    for (Iterator<SelectionKey> it = ops.iterator(); it.hasNext();) {
                        SelectionKey key = it.next();
                        it.remove();
                        if(key.isConnectable()) {
                            System.out.println("client connect");
                            SocketChannel sc =  (SocketChannel) key.channel();
                            // 判断此通道上是否正在进行连接操作。
                            // 完成套接字通道的连接过程。
                            if (sc.isConnectionPending()) {
                                sc.finishConnect();
                                System.out.println("完成连接!");
                                ByteBuffer buffer = ByteBuffer.allocate(1024);
                                buffer.put("Hello,Server".getBytes());
                                buffer.flip();
                                sc.write(buffer);
                            }
                            sc.register(selector, SelectionKey.OP_READ);
                        } else if(key.isWritable()) {
                            System.out.println("客户端写");
                            SocketChannel sc = (SocketChannel)key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            buffer.put("hello server.".getBytes());
                            buffer.flip();
                            sc.write(buffer);
                        } else if(key.isReadable()) {
                            System.out.println("客户端收到服务器的响应....");
                            SocketChannel sc = (SocketChannel)key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            int count = sc.read(buffer);
                            if(count > 0 ) {
                                buffer.flip();
                                byte[] response = new byte[buffer.remaining()];
                                buffer.get(response);
                                System.out.println(new String(response));
                            }

                            // 再次发送消息，重复输出
                            buffer = ByteBuffer.allocate(1024);
                            buffer.put("hello server.".getBytes());
                            buffer.flip();
                            sc.write(buffer);

                        }
                    }
                } catch(Throwable e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
