package everthing.core.interceptor;

import java.io.File;

/**
 * 文件处理类 - 打印、转换、写入
 */
@FunctionalInterface    // 函数式接口
public interface FileInterceptor {
    void apply(File file);
}
