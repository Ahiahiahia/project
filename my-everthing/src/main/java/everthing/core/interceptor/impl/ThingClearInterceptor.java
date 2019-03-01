package everthing.core.interceptor.impl;

import everthing.core.dao.FileDatabaseDao;
import everthing.core.interceptor.ThingInterceptor;
import everthing.core.model.Thing;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by hunter on 2019/2/28
 */
public class ThingClearInterceptor implements ThingInterceptor, Runnable {
    private Queue<Thing> queue = new ArrayBlockingQueue<>(1024);
    private final FileDatabaseDao fileDatabaseDao;
    public ThingClearInterceptor(FileDatabaseDao fileDatabaseDao) {
        this.fileDatabaseDao = fileDatabaseDao;
    }

    @Override
    public void apply(Thing thing) {
        this.queue.add(thing);
    }

    @Override
    public void run() {
        while(true){
            Thing thing = this.queue.poll();
            if(thing != null){
                fileDatabaseDao.delete(thing);
            }
            // TODO 1.优化：批量删除
            // List<Thing> list = new ArrayList<>();    delete改为批量删除
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
