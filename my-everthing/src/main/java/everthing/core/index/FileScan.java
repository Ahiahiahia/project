package everthing.core.index;

import everthing.core.interceptor.FileInterceptor;

/**
 * 索引遍历接口
 */
public interface FileScan {
    /**
     * 对数据库的遍历
     * @param path
     */
    void index(String path);

    /**
     * 遍历拦截器
     * @param interceptor
     */
    void interceptor(FileInterceptor interceptor);
}
