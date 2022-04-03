package com.learn.nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author linjicong
 * @date 2022-04-03 8:43
 */
public class NIOClient {
    private static boolean isEnding = false;
    //表示数字
    private static int flag= 0;
    //缓冲区大小
    private static final int BLOCK = 4096;
    //接受数据缓冲区
    private static final ByteBuffer sendbuffer = ByteBuffer.allocate(BLOCK);
    //发送数据缓冲区
    private static final ByteBuffer receiveBuffer = ByteBuffer.allocate(BLOCK);
    //服务器地址
    private final static InetSocketAddress SERVER_ADDRESS = new InetSocketAddress("localhost",8888);
    static BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));
    public static void main(String[] args) throws IOException {
        //打开socket通道
        SocketChannel socketChannel = SocketChannel.open();
        //设置为非阻塞模式
        socketChannel.configureBlocking(false);
        //打开选择器
        Selector selector = Selector.open();
        //注册连接服务端socket动作
        socketChannel.register(selector,SelectionKey.OP_CONNECT);
        //连接
        socketChannel.connect(SERVER_ADDRESS);
        //分配缓冲区内存大小
        Set<SelectionKey> selectionKeys;
        Iterator<SelectionKey> iterator;
        SelectionKey selectionKey;
        SocketChannel client;
        String receiveText;
        String sendText;
        int count =  0;
        while (!isEnding){
            //选择一组键，其对应的通道已为 I/O 操作准备就绪
            //此方法执行处于阻塞模式的选择操作
            selector.select();
            //返回此选择器的已选择的键集
            selectionKeys = selector.selectedKeys();
            iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                selectionKey = iterator.next();
                if(selectionKey.isConnectable()){
                    System.out.println("client connect");
                    client = (SocketChannel) selectionKey.channel();
                    //判断此通道伤是否正在进行连接操作
                    //完成套接字通道的连接过程
                    if(client.isConnectionPending()){
                        client.finishConnect();
                        System.out.println("完成连接！");
                        sendbuffer.clear();
                        sendbuffer.put("Hello,Server".getBytes());
                        sendbuffer.flip();
                        client.write(sendbuffer);
                    }
                    client.register(selector,SelectionKey.OP_READ);
                }else if(selectionKey.isReadable()){
                    client = (SocketChannel) selectionKey.channel();
                    receiveBuffer.clear();
                    count = client.read(receiveBuffer);
                    if(count >0){
                        receiveText = new String(receiveBuffer.array(),0,count);
                        System.out.println("客户端接受服务器端的数据--："+receiveText);
                        client.register(selector, SelectionKey.OP_WRITE);
                    }
                }else if (selectionKey.isWritable()) {
                    sendbuffer.clear();
                    client = (SocketChannel) selectionKey.channel();
                    sendText = systemIn.readLine();
                    sendbuffer.put(sendText.getBytes());
                    //将缓冲区各标志复位,因为向里面put了数据标志被改变要想从中读取数据发向服务器,就要复位
                    sendbuffer.flip();
                    client.write(sendbuffer);
                    System.out.println("客户端向服务器端发送数据--："+sendText);
                    client.register(selector, SelectionKey.OP_READ);
                }
            }
            selectionKeys.clear();
        }
        selector.close();
        socketChannel.close();
    }
}
