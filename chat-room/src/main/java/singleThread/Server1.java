package singleThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by hunter on 2019/2/19
 */
public class Server1 {
    public static void main(String[] args) throws IOException {
        // 建立基站
        ServerSocket serverSocket = null;
        Scanner inFromSocket = null;
        PrintStream outToSocket = null;
        try {
            serverSocket = new ServerSocket(2333);
            // 一直阻塞，直到有客户端的连接
            System.out.println("等待客户端连接。。。。。");
            Socket socket = serverSocket.accept();

            // 获取远程和本地的端口号
            System.out.println("连接成功，客户端的端口号为：" + socket.getPort());
            System.out.println("本机端口号：" + socket.getLocalPort());

            // 获取输入、输出流
            inFromSocket = new Scanner(socket.getInputStream());
            outToSocket = new PrintStream(socket.getOutputStream(), true, "UTF-8");
            // 服务端先获取客户端输入，然后输出
            if(inFromSocket.hasNext()){
                System.out.println("客户端：" + inFromSocket.nextLine());
            }
            outToSocket.println("I am Server");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭基站
            serverSocket.close();
            // 关闭流
            inFromSocket.close();
            outToSocket.close();
        }
    }
}
