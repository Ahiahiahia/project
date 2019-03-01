package everthing.core;

import everthing.config.MyEverthingConfig;
import everthing.core.common.HandPath;
import everthing.core.dao.DataSourceFactory;
import everthing.core.dao.FileDatabaseDao;
import everthing.core.dao.impl.FileDatabaseDaoImpl;
import everthing.core.index.FileScan;
import everthing.core.index.FileScanImpl;
import everthing.core.interceptor.impl.FileIndexInterceptor;
import everthing.core.interceptor.impl.FilePrintInterceptor;
import everthing.core.interceptor.impl.ThingClearInterceptor;
import everthing.core.model.Condition;
import everthing.core.model.Thing;
import everthing.core.monitor.FileWatch;
import everthing.core.monitor.impl.FileWatchImpl;
import everthing.core.search.FileSearch;
import everthing.core.search.FileSearchImpl;

import javax.sql.DataSource;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 统一调度程序
 */
public class MyEverthingManager {
    private static volatile MyEverthingManager manager;
    private FileSearch fileSearch;    // 检索
    private FileScan fileScan;    // 索引
    private ExecutorService executorService;    // 线程池

    /**
     * 清理删除的文件
     */
    private ThingClearInterceptor thingClearInterceptor;
    private Thread clearThread;
    private AtomicBoolean clearThreadStatus = new AtomicBoolean(false);
    /**
     * 文件监听
     */
    private FileWatch fileWatch;

    public MyEverthingManager(){
        this.initComponent();
    }
    private void initComponent(){
        // 数据源对象
        DataSource dataSource = DataSourceFactory.getDataSource();
        // 重置数据库
        ResetDatabase();
        // 业务层对象
        FileDatabaseDao fileDatabaseDao = new FileDatabaseDaoImpl(dataSource);
        this.fileScan = new FileScanImpl();
        this.fileSearch = new FileSearchImpl(fileDatabaseDao);
        // 文件拦截器
        //this.fileScan.interceptor(new FilePrintInterceptor());
        this.fileScan.interceptor(new FileIndexInterceptor(fileDatabaseDao));
        // 清理删除的文件
        this.thingClearInterceptor = new ThingClearInterceptor(fileDatabaseDao);
        this.clearThread = new Thread(this.thingClearInterceptor);
        // 将用户线程变为守护线程
        this.clearThread.setDaemon(true);
        this.clearThread.setName("Thing-Clear");

        // 文件监听对象
        fileWatch = new FileWatchImpl(fileDatabaseDao);
    }

    /**
     * 重置数据库
     */
    private void ResetDatabase() {
            DataSourceFactory.initDatabase();
    }

    public static MyEverthingManager getManager(){
        if(manager == null){
            synchronized (MyEverthingManager.class){
                if(manager == null){
                    manager = new MyEverthingManager();
                }
            }
        }
        return manager;
    }

    /**
     * 检索
     * @param condition
     * @return
     */
    public List<Thing> search(Condition condition){
        // 使用Stream流的方式剔除已经删除的文件
        return this.fileSearch.search(condition)
                .stream()
                .filter(thing -> {
                    String path = thing.getPath();
                    File f = new File(path);
                    boolean flag = f.exists();
                    if (!flag) {
                        //做删除操作
                        thingClearInterceptor.apply(thing);
                    }
                    return flag;

                }).collect(Collectors.toList());
    }

    /**
     * 索引 - 多线程
     */
    public void buildIndex(){
        ResetDatabase();
        Set<String> directories = MyEverthingConfig.getConfig().getIncludePath();
        if(this.executorService == null){
            this.executorService = Executors.newFixedThreadPool(
                    directories.size(), new ThreadFactory() {
                        private final AtomicInteger threadId =
                                new AtomicInteger(0);
                        @Override
                        public Thread newThread(Runnable r) {
                            Thread thread = new Thread(r);
                            thread.setName("Thread-Scan"+threadId.getAndIncrement());
                            return thread;
                        }
                    });
        }
        final CountDownLatch countDownLatch =
                new CountDownLatch(directories.size());
        for(String path : directories){
            this.executorService.submit(() -> {
                MyEverthingManager.this.fileScan.index(path);
                //当前任务完成，值-1
                countDownLatch.countDown();
            });
        }
        // 阻塞，直到任务完成 - 值减为0
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动清理线程
     */
    public void startClear(){
        if(this.clearThreadStatus.compareAndSet(false, true)){
            this.clearThread.start();
        }else{
            System.out.println("不能重复启动清理线程");
        }
    }

    /**
     * 启动文件系统监听
     */
    public void startFileSystemMonitor(){
        MyEverthingConfig config = MyEverthingConfig.getConfig();
        HandPath handPath = new HandPath();
        handPath.setIncludePath(config.getIncludePath());
        handPath.setExcludePsth(config.getExcludePath());
        this.fileWatch.monitor(handPath);
        new Thread(new Runnable() {
            @Override
            public void run() {
                fileWatch.start();
            }
        }).start();
    }
}
