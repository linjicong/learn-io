package com.learn.bio;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * @author linjicong
 * @date 2022-04-02 15:47
 */
public class BIOSocket {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        while (true) {
            Socket client = serverSocket.accept();
            new Thread(()->{
                try {
                    PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    while (true) {
                        String inStr = in.readLine();
                        System.out.println(inStr);
                        out.println(inStr+"=="+new Date());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
