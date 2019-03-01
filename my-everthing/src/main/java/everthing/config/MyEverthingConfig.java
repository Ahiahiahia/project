package everthing.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * 对一些特定的文件或目录进行排除，不放入数据库
 */
@Getter // 只需要get取得路径即可，不需要对路径做修改
public class MyEverthingConfig {
    private static volatile MyEverthingConfig config;   // 单例
    private Set<String> includePath = new HashSet<>();    // 建立索引的路径
    private Set<String> excludePath = new HashSet<>();    // 排除索引的路径

    @Setter
    private Integer maxReturn = 30; // 检索的最大返回数量
    @Setter
    private Boolean deptOrderByAsc = true;  // 按照深度的排序的次序，默认是升序
    /**
     * h2数据库文件路径 - 更安全，减少出错几率
     */
    private String h2Path = System.getProperty("user.dir")+
            File.separator+"my_everthing";

    /**
     * 懒汉式单例 - 双重检查
     * @return
     */
    public static MyEverthingConfig getConfig(){
        if(config == null){
            synchronized (MyEverthingConfig.class){
                if(config == null){
                    config = new MyEverthingConfig();
                    config.initDefaultPathsConfig();
                }
            }
        }
        return config;
    }

    /**
     * 初始化默认的路径配置
     */
    private static void initDefaultPathsConfig(){
        // 1.获取文件系统
        FileSystem fileSystem = FileSystems.getDefault();

        // 2.遍历的目录(JDK1.7提供Path)
        Iterable<Path> iterable = fileSystem.getRootDirectories();
        iterable.forEach(path ->
                config.includePath.add(path.toString()));

        // 3.排除的目录
        // Windows里的：C:\Windows     C:\Program Files (x86)
        // C:\Program Files    C:\ProgramData
        // Linux里的：/tmp     /etc
        // 3.1获取当前系统名称
        String osName = System.getProperty("os.name");
        // 3.2添加需要排除的路径
        if(osName.startsWith("Windows")){
            config.getExcludePath().add("C:\\Windows");
            config.getExcludePath().add("C:\\Program Files (x86)");
            config.getExcludePath().add("C:\\Program Files");
            config.getExcludePath().add("C:\\ProgramData");
        }else {
            config.getExcludePath().add("/tmp");
            config.getExcludePath().add("/etc");
            config.getExcludePath().add("/root");
        }
    }
}
