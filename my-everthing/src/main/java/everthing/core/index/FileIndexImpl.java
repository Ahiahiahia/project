package everthing.core.index;

import everthing.core.dao.FileDatabaseDao;
import everthing.core.model.Thing;

/**
 * 索引类
 */
public class FileIndexImpl implements fileIndex{
    private final FileDatabaseDao fileDatabaseDao;
    public FileIndexImpl(FileDatabaseDao fileDatabaseDao) {
        this.fileDatabaseDao = fileDatabaseDao;
    }

    @Override
    public void insert(Thing thing) {
        this.fileDatabaseDao.insert(thing);
    }
}
