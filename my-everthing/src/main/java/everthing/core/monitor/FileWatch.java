package everthing.core.monitor;

import everthing.core.common.HandPath;

/**
 * 文件监控
 */
public interface FileWatch {
    /**
     * 监听启动
     */
    void start();
    void monitor(HandPath handPath);

    /**
     * 监听停止
     */
    void stop();
}
