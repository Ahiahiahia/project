package everthing.core.search;

import everthing.core.dao.FileDatabaseDao;
import everthing.core.model.Condition;
import everthing.core.model.Thing;

import java.util.List;

/**
 * 检索类
 */
public class FileSearchImpl implements fileSearch{
    private final FileDatabaseDao fileDatabaseDao;
    public FileSearchImpl(FileDatabaseDao fileDatabaseDao) {
        this.fileDatabaseDao = fileDatabaseDao;
    }

    @Override
    public List<Thing> search(Condition condition) {
        return this.fileDatabaseDao.search(condition);
    }
}
