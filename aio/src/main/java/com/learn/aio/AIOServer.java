package com.learn.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author linjicong
 * @date 2022-04-03 9:09
 */
public class AIOServer {
    public static final int port = 5555;

    public static void main(String[] args) throws IOException, InterruptedException {
        //首先打开一个ServerSocket通道并获取AsynchronousServerSocketChannel实例
        AsynchronousServerSocketChannel serverSocketChannel =AsynchronousServerSocketChannel.open();
        //绑定监听端口
        serverSocketChannel.bind(new InetSocketAddress(port));
        //开始接受客户端的连接请求
        CompletionHandler<AsynchronousSocketChannel,Object> handler = new CompletionHandler<AsynchronousSocketChannel, Object>() {

            @Override
            public void completed(AsynchronousSocketChannel result, Object attachment) {
                // 继续监听下一个连接请求
                serverSocketChannel.accept(attachment,this);
                try {
                    System.out.println("接受到一个新的连接请求:"+result.getRemoteAddress().toString());
                    // 给客户端发送一个消息并等待发送完成
                    result.write(ByteBuffer.wrap("你好，我是服务器".getBytes())).get();
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    // 阻塞等待客户端数据
                    result.read(readBuffer).get();
                    System.out.println("收到客户端的消息:"+new String(readBuffer.array()));
                } catch (IOException | ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("出错了:"+exc.getMessage());
            }
        };
        serverSocketChannel.accept(null,handler);
        //让程序一直运行
        TimeUnit.MINUTES.sleep(Integer.MAX_VALUE);
    }
}
