package everthing.core.dao;

import everthing.core.model.Condition;
import everthing.core.model.Thing;

import java.util.List;

/**
 * 业务层访问数据库的CRUD
 */
public interface FileDatabaseDao {

    /**
     * 对数据库的插入
     * @param thing
     */
    void insert(Thing thing);

    /**
     * 根据Condition条件进行数据库的检索
     */
    List<Thing> search(Condition condition);

    /**
     * 删除thing
     * @param thing
     */
    void delete(Thing thing);
}
