package xiangmu.MutiThread;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 服务器端
 */
public class Server {
    // Map存储每个连接的客户端
    private static Map<String, Socket> map = new ConcurrentHashMap<>();

    // 处理每个客户端
    private static class ExecuteClient implements Runnable {
        private Socket client;
        public ExecuteClient(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                // 获取客户端的输入流
                Scanner in = new Scanner(client.getInputStream());
                in.useDelimiter("\n");
                String msgFromClient;
                while(true){
                    if(in.hasNextLine()){
                        msgFromClient = in.nextLine();
                        // windows下将默认换行/r/n中的/r替换为空字符串
                        Pattern pattern = Pattern.compile("\r");
                        Matcher matcher = pattern.matcher(msgFromClient);
                        msgFromClient = matcher.replaceAll("");
                        // 注册账号
                        if(msgFromClient.startsWith("username")){
                            String userName = msgFromClient.split("\\:")[1];
                            registerUser(userName, this.client);
                            continue;
                        }
                        // 群聊消息
                        else if(msgFromClient.startsWith("G")){
                            String msgG = msgFromClient.split("\\:")[1];
                            groupChat(msgG);
                            continue;
                        }
                        // 私聊消息
                        else if(msgFromClient.startsWith("P")){
                            String[] str = msgFromClient.split("\\:");
                            String userName = str[1].split("-")[0];
                            String msgP = str[1].split("-")[1];
                            privateChat(userName, msgP);
                            continue;
                        }
                        // 关闭客户端连接
                        else if(msgFromClient.contains("byebye")){
                            exit(this.client);
                            continue;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 客户端注册
         * @param userName 用户名
         * @param client 端口号
         */
        private void registerUser(String userName, Socket client){
            // 将用户信息保存在Map中
            map.put(userName, client);
            System.out.println(userName+"上线了！");
            System.out.println("当前在线"+map.size()+"人");
            try {
                PrintStream out = new PrintStream(
                        client.getOutputStream(), true, "UTF-8");
                out.println("注册成功，可以开始走套路了~");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 群聊
         * @param str 要发送的群聊消息
         */
        private void groupChat(String str){
            // 将Map集合转换为Set集合遍历
            Set<Map.Entry<String, Socket>> set = map.entrySet();
            for(Map.Entry<String, Socket> client : set){
                Socket socket = client.getValue();
                try {
                    PrintStream out = new PrintStream(
                            socket.getOutputStream(), true, "UTF-8");
                    out.println(str);
                } catch (IOException e) {
                    System.err.println("群聊异常：" + e);
                }
            }
        }

        /**
         * 私聊
         * @param userName 要私聊的对象的用户名
         * @param str 要发送的私聊消息
         */
        private void privateChat(String userName, String str){
            Socket socket = map.get(userName);
            try {
                PrintStream out = new PrintStream(
                        socket.getOutputStream(), true, "UTF-8");
                out.println(str);
            } catch (IOException e) {
                System.err.println("私聊异常：" + e);
            }
        }

        /**
         * 关闭客户端连接
         */
        private void exit(Socket client){
            String name = null;
            for(String username : map.keySet()){
                if(map.get(username).equals(client)){
                    name = username;
                    break;
                }
            }
            map.remove(name);
            System.out.println("用户"+name+"下线了");
            System.out.println("当前在线"+map.size()+"人");
        }
    }

    public static void main(String[] args) throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        ServerSocket serverSocket = new ServerSocket(2333);
        for (int i = 0 ; i < 20 ; i++) {
            System.out.println("等待客户端连接...");
            Socket client = serverSocket.accept();
            System.out.println("有新的客户端连接，端口号为: "+client.getPort());
            executorService.submit(new ExecuteClient(client));
        }
        executorService.shutdown();
        serverSocket.close();

    }
}
