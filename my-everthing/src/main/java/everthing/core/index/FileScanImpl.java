package everthing.core.index;

import everthing.config.MyEverthingConfig;
import everthing.core.interceptor.FileInterceptor;

import java.io.File;
import java.util.LinkedList;

/**
 * 索引类
 */
public class FileScanImpl implements FileScan {
    // 排除条件
    private MyEverthingConfig config = MyEverthingConfig.getConfig();
    // 对File的处理集
    private LinkedList<FileInterceptor> interceptors = new LinkedList<>();

    @Override
    public void index(String path) {
        File file = new File(path);
        if(file.isFile()){
            // 如果排除目录里不包含文件的父目录，添加文件
            if(config.getExcludePath().contains(file.getParent())){
                return;
            }
        }else{
            // 如果排除目录里不包含该目录，则添加目录
            if(config.getExcludePath().contains(path)){
                return;
            }else {
                File[] files = file.listFiles();
                if(files != null){
                    for(File f : files){
                        index(f.getAbsolutePath());
                    }
                }
            }
        }
        for(FileInterceptor interceptor : this.interceptors){
            interceptor.apply(file);
        }
    }

    @Override
    public void interceptor(FileInterceptor interceptor) {
        interceptors.add(interceptor);
    }
}
