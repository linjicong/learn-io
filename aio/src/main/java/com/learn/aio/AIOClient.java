package com.learn.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author linjicong
 * @date 2022-04-03 9:18
 */
public class AIOClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        client.connect(new java.net.InetSocketAddress("127.0.0.1", 5555), null, new CompletionHandler<Void, Object>() {
            @Override
            public void completed(Void result, Object attachment) {
                System.out.println("连接成功");
                try {
                    client.write(ByteBuffer.wrap("hello".getBytes())).get();
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    client.read(readBuffer).get();
                    System.out.println(new String(readBuffer.array()));
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("出错了:"+exc.getMessage());
            }
        });
        TimeUnit.MINUTES.sleep(Integer.MAX_VALUE);
    }
}
