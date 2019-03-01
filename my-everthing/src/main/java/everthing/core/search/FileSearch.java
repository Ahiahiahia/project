package everthing.core.search;

import everthing.core.model.Condition;
import everthing.core.model.Thing;

import java.util.List;

/**
 * 检索接口
 */
public interface FileSearch {
    /**
     * 根据Condition条件进行数据库的检索
     */
    List<Thing> search(Condition condition);
}
