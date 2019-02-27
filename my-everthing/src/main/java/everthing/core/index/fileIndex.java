package everthing.core.index;

import everthing.core.model.Thing;

/**
 * Created by hunter on 2019/2/27
 */
public interface fileIndex {
    /**
     * 对数据库的插入
     * @param thing
     */
    void insert(Thing thing);
}
