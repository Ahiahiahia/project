package everthing.core.interceptor.impl;

import everthing.core.common.FileConvertThing;
import everthing.core.dao.FileDatabaseDao;
import everthing.core.interceptor.FileInterceptor;
import everthing.core.model.Thing;

import java.io.File;

/**
 * 插入
 */
public class FileIndexInterceptor implements FileInterceptor {
    private FileDatabaseDao fileDatabaseDao;
    public FileIndexInterceptor(FileDatabaseDao fileDatabaseDao) {
        this.fileDatabaseDao = fileDatabaseDao;
    }

    /**
     * 将File对象转换为Thing，写入数据库
     * @param file
     */
    @Override
    public void apply(File file) {
        Thing thing = FileConvertThing.convert(file);
        fileDatabaseDao.insert(thing);
    }
}
