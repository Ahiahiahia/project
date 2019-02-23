package xiangmu.singleThread;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by hunter on 2019/2/19
 */
public class Sockett {
    public static void main(String[] args) throws IOException {
        Socket cilent = null;
        Scanner inFromServer = null;
        PrintStream outToServer = null;
        try {
            // 建立连接
            cilent = new Socket("127.0.0.1", 2333);

            // 获取输入、输出流
            outToServer = new PrintStream(cilent.getOutputStream(), true, "UTF-8");
            inFromServer = new Scanner(cilent.getInputStream());
            // 客户端先输出，然后获取服务端输入
            outToServer.println("I am Socket");
            if(inFromServer.hasNext()){
                System.out.println("服务器：" + inFromServer.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭客户端连接
            cilent.close();
            // 关闭流
            inFromServer.close();
            outToServer.close();
        }
    }
}
