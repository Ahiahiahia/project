package everthing.core.model;

import lombok.Data;

/**
 * 文件属性信息 - 索引之后的记录（存入数据库）
 */
@Data   // getter setter toString方法生成完成
public class Thing {
    private String name;        // 文件名（只保留名称）
    private String path;        // 文件路径
    private Integer depth;      // 文件路径深度
    private FileType fileType;  // 文件类型
}
