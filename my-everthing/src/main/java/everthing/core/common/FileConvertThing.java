package everthing.core.common;

import everthing.core.model.FileType;
import everthing.core.model.Thing;

import java.io.File;

/**
 * 将File对象转换为Thing对象
 */
public final class FileConvertThing {
    private FileConvertThing(){

    }

    public static Thing convert(File file) {
        Thing thing = new Thing();
        thing.setName(file.getName());
        thing.setPath(file.getAbsolutePath());  // 绝对路径
        thing.setDepth(computeFileDepth(file));
        thing.setFileType(computeFileType(file));
        return thing;
    }

    /**
     * 计算文件深度
     * @param file
     * @return
     */
    private static int computeFileDepth(File file){
        String[] strs = file.getAbsolutePath().split("\\\\");
        return strs.length;
    }

    /**
     * 计算文件类型
     * @param file
     * @return
     */
    private static FileType computeFileType(File file){
        if(file.isDirectory()){
            return FileType.OTHER;
        }
        // 扩展名从最后一个点开始分隔
        String fileName = file.getName();
        int index = fileName.lastIndexOf(".");
        // 防止 abc. 这种情况
        if(index != -1 || index < fileName.length()-1){
            String extend = fileName.substring(index+1);
            return FileType.findFileType(extend);
        }else {
            return FileType.OTHER;
        }
    }
}
