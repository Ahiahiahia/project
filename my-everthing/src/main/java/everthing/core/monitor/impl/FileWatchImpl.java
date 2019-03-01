package everthing.core.monitor.impl;

import everthing.core.common.FileConvertThing;
import everthing.core.common.HandPath;
import everthing.core.dao.FileDatabaseDao;
import everthing.core.monitor.FileWatch;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by hunter on 2019/3/1
 */
public class FileWatchImpl implements FileWatch, FileAlterationListener {
    private FileDatabaseDao fileDatabaseDao;
    private FileAlterationMonitor monitor;
    public FileWatchImpl(FileDatabaseDao fileDatabaseDao) {
        this.fileDatabaseDao = fileDatabaseDao;
        // 10毫秒检查一次（原理是遍历：太短效率低，太长变化检测不及时）
        this.monitor = new FileAlterationMonitor(10);
    }

    /**
     * 文件监听开始
     * @param fileAlterationObserver
     */
    @Override
    public void onStart(FileAlterationObserver fileAlterationObserver) {
        // this就是Listener对象
        //fileAlterationObserver.addListener(this);
    }

    @Override
    public void onDirectoryCreate(File directory) {
        System.out.println("onDirectoryCreate"+directory);
    }

    @Override
    public void onDirectoryChange(File directory) {
        System.out.println("onDirectoryChange"+directory);
    }

    @Override
    public void onDirectoryDelete(File directory) {
        System.out.println("onDirectoryDelete"+directory);
    }

    /**
     * 文件创建
     * @param file
     */
    @Override
    public void onFileCreate(File file) {
        System.out.println("onFileCreate"+file);
        this.fileDatabaseDao.insert(FileConvertThing.convert(file));
    }

    @Override
    public void onFileChange(File file) {
        System.out.println("onFileChange"+file);
    }

    /**
     * 文件删除
     * @param file
     */
    @Override
    public void onFileDelete(File file) {
        System.out.println("onFileDelete"+file);
        this.fileDatabaseDao.delete(FileConvertThing.convert(file));
    }

    /**
     * 文件监听停止
     * @param fileAlterationObserver
     */
    @Override
    public void onStop(FileAlterationObserver fileAlterationObserver) {
        //fileAlterationObserver.removeListener(this);
    }

    @Override
    public void start() {
        try {
            this.monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void monitor(HandPath handPath) {
        // 监控includePath
        for(String path : handPath.getIncludePath()){
            FileAlterationObserver observer = new FileAlterationObserver(
                    path, pathname -> {
                    // true监控，false过滤掉不监控
                    String currentPath = pathname.getAbsolutePath();
                    for(String p : handPath.getExcludePsth()){
                        if(p.startsWith(currentPath)){
                            return false;
                        }
                    }
                    return true;
                }
            );
            observer.addListener(this);
            this.monitor.addObserver(observer);
        }
    }

    @Override
    public void stop() {
        try {
            this.monitor.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
