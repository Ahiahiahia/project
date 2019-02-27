package everthing.core.search;

import everthing.core.model.Condition;
import everthing.core.model.Thing;

import java.util.List;

/**
 * Created by hunter on 2019/2/27
 */
public interface fileSearch {
    /**
     * 根据Condition条件进行数据库的检索
     */
    List<Thing> search(Condition condition);
}
