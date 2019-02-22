package xiangmu.MutiThread;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * 客户端
 */

// 读线程 - 只从服务器读
class ReadThread implements Runnable {
    private Socket client;
    public ReadThread(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            // 获取服务端输入流
            Scanner in = new Scanner(client.getInputStream());
            in.useDelimiter("\n");
            while(true){
                if(in.hasNextLine()){
                    System.out.println(in.nextLine());
                }
                // 判断客户端是否已经关闭
                if(client.isClosed()){
                    System.out.println("客户端已关闭");
                    break;
                }
            }
            in.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}

// 写线程 - 先读取键盘输入，然后写到服务器
class WeiteThread implements Runnable {
    private Socket client;
    public WeiteThread(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        // 获取键盘输入
        Scanner in = new Scanner(System.in);
        in.useDelimiter("\n");
        try {
            // 获取客户端输出流
            PrintStream out = new PrintStream(
                    client.getOutputStream(), true, "UTF-8");
            while(true){
                String msg;
                System.out.println("请输入要发送的内容：");
                if(in.hasNextLine()){
                    // 去掉字符串前后的空格
                    msg = in.nextLine().trim();
                    out.println(msg);
                    // 客户端退出标志
                    if(msg.equals("byebye")){
                        System.out.println("客户端关闭");
                        // 关闭流
                        in.close();
                        out.close();
                        // 关闭客户端
                        client.close();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class Client {
    public static void main(String[] args){
        try {
            Socket socket = new Socket("127.0.0.1", 2333);
            ReadThread readThread = new ReadThread(socket);
            WeiteThread weiteThread = new WeiteThread(socket);
            new Thread(readThread).start();
            new Thread(weiteThread).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
