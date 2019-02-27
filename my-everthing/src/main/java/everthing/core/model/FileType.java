package everthing.core.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件类型 - 枚举类
 */
public enum FileType {
    IMG("png", "jpeg", "jpe", "gif"),
    DOC("ppt", "pptx", "doc", "docx", "pdf"),
    BIN("exe", "sh", "jar", "msi"),
    ARCTIVE("zip", "rar"),
    OTHER("*");

    /**
     * 对应文件类型的扩展名集合
     */
    private Set<String> extend = new HashSet<>();
    FileType(String... extend){
        this.extend.addAll(Arrays.asList(extend));
    }

    /**
     * 根据文件扩展名获取文件类型
     * @param extend
     * @return
     */
    public static FileType findFileType(String extend){
        for(FileType fileType : FileType.values()){
            if(fileType.extend.contains(extend)){
                return fileType;
            }
        }
        return FileType.OTHER;
    }

    /**
     * 根据文件类型名获取文件类型
     * @param name
     * @return
     */
    public static FileType findFileTypeByName(String name){
        for(FileType fileType : FileType.values()){
            if(fileType.name().equals(name)){
                return fileType;
            }
        }
        return FileType.OTHER;
    }

    public static void main(String[] args){
        System.out.println(FileType.findFileType("jpeg"));
        System.out.println(FileType.findFileType("mp4"));
        System.out.println(FileType.findFileType("doc"));
    }
}
