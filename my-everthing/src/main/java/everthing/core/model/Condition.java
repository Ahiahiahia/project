package everthing.core.model;

import lombok.Data;

/**
 * 检索条件
 */
@Data
public class Condition {
    // name：    like模糊匹配
    // fileType：=清晰匹配
    // limit：   limit offset
    // orderByAsc：order by
    private String name;        // 文件名
    private String fileType;    // 文件类型（输入的时候是String类型）
    private Integer limit;      // 限制返回结果的数量
    /**
     * 检索结果的文件信息的显示排序规则
     * 1.默认是true：asc    按照文件深度升序排列
     * 2.false：desc       按照文件深度降序排序
     */
    private  Boolean orderByAsc = true;
}
