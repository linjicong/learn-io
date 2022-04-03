package com.learn.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author linjicong
 * @date 2022-04-02 18:00
 */
public class BIOClient {
    public static void main(String[] args) {
        Socket socket = null;
        BufferedReader in = null;
        BufferedReader systemIn = null;
        PrintWriter out = null;
        String readline=null;
        try {
            socket = new Socket("127.0.0.1", 8080);
            systemIn = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            while (!"bye".equals(readline)){
                readline = systemIn.readLine();
                out.println(readline);
                out.flush();
                System.out.println("服务器返回:"+in.readLine());
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
