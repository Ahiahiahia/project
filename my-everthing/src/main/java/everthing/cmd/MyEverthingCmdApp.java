package everthing.cmd;

import everthing.config.MyEverthingConfig;
import everthing.core.MyEverthingManager;
import everthing.core.model.Condition;
import everthing.core.model.Thing;

import java.util.List;
import java.util.Scanner;

/**
 * 主程序
 */
public class MyEverthingCmdApp {
    private static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args){
        // 解析用户参数
        parseParams(args);
        // 欢迎
        welcome();
        // 统一调度器
        MyEverthingManager manager = MyEverthingManager.getManager();
        // 启动后台清理线程
        manager.startClear();
        // 启动文件系统监控
        manager.startFileSystemMonitor();
        // 交互式
        interactive(manager);
    }

    /**
     * 处理用户参数
     * 如果用户参数格式不对 或没有参数，采用默认参数
     * @param args
     */
    private static void parseParams(String[] args) {
        if(args == null){
            return;
        }
        MyEverthingConfig config = MyEverthingConfig.getConfig();
        for(String param : args){
            String maxReturnParam = "--maxReturn=";
            if(param.startsWith(maxReturnParam)){
                int index = param.lastIndexOf("=");
                String maxReturnStr = param.substring(index+1);
                try{
                    int maxReturn = Integer.parseInt(maxReturnStr);
                    config.setMaxReturn(maxReturn);
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }
            String deptOrderByAscParam = "--deptOrderByAsc=";
            if(param.startsWith(deptOrderByAscParam)){
                int index = param.lastIndexOf("=");
                String deptOrderByAscStr = param.substring(index+1);
                config.setDeptOrderByAsc(Boolean.parseBoolean(deptOrderByAscStr));
            }
            String includePathParam = "--includePath=";
            if(param.startsWith(includePathParam)){
                int index = param.lastIndexOf("=");
                    String includePathStr = param.substring(index+1);
                    String[] paths = includePathStr.split(";");
                    if(paths.length > 0){
                        config.getIncludePath().clear();
                    }
                    for(String path : paths){
                        config.getIncludePath().add(path);
                    }
            }
            String excludePathStr = "--excludePath=";
            if(param.startsWith(excludePathStr)){
                int index = param.lastIndexOf("=");
                String excludePath = param.substring(index+1);
                String[] paths = excludePath.split(";");
                config.getExcludePath().clear();
                for(String path : paths){
                    config.getExcludePath().add(path);
                }
            }
        }
    }

    public static void interactive(MyEverthingManager manager){
        while(true){
            System.out.print("everthing >>");
            String input = scanner.nextLine();
            if (input.startsWith("search")){
                // search name FileType
                String[] values = input.split(" ");
                if(values.length >= 2){
                    if(!values[0].equals("search")){
                        help();
                        continue;
                    }
                    Condition condition = new Condition();
                    String name = values[1];
                    condition.setName(name);
                    if (values.length >= 3) {
                        String fileType = values[2];
                        condition.setFileType(fileType.toUpperCase());
                    }
                    search(manager, condition);
                    continue;
                }else {
                    help();
                    continue;
                }
            }
            switch (input) {
                case "help":
                    help();
                    break;
                case "quit":
                    quit();
                    return;
                case "index":
                    index(manager);
                    break;
                default:
                    help();
            }
        }
    }

    private static void search(MyEverthingManager manager, Condition condition) {
        condition.setLimit(MyEverthingConfig.getConfig().getMaxReturn());
        condition.setOrderByAsc(MyEverthingConfig.getConfig().getDeptOrderByAsc());
        List<Thing> thingList = manager.search(condition);
        for (Thing thing : thingList) {
            System.out.println(thing.getPath());
        }

    }

    private static void index(MyEverthingManager manager) {
        //统一调度器中的index
        new Thread(manager::buildIndex).start();
    }

    private static void quit() {
        System.out.println("再见");
        System.exit(0);
    }

    private static void welcome() {
        System.out.println("欢迎使用，Everything");
    }

    private static void help() {
        System.out.println("命令列表：");
        System.out.println("退出：quit");
        System.out.println("帮助：help");
        System.out.println("索引：index");
        System.out.println("搜索：search <name> [<file-Type> img | doc | bin | archive | other]");
    }
}
