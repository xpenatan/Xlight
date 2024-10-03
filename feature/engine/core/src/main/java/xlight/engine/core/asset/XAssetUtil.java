package xlight.engine.core.asset;

import com.badlogic.gdx.Files;

public class XAssetUtil {

    private static final int FILE_TYPE_INTERNAL = 1;
    private static final int FILE_TYPE_LOCAL = 2;
    private static final int FILE_TYPE_ABSOLUTE = 3;
    private static final int FILE_TYPE_EXTERNAL = 4;
    private static final int FILE_TYPE_CLASSPATH = 5;

    public static Files.FileType getFileTypeEnum(int fileHandleType) {
        if(fileHandleType == FILE_TYPE_INTERNAL) {
            return Files.FileType.Internal;
        }
        if(fileHandleType == FILE_TYPE_LOCAL) {
            return Files.FileType.Local;
        }
        if(fileHandleType == FILE_TYPE_ABSOLUTE) {
            return Files.FileType.Absolute;
        }
        if(fileHandleType == FILE_TYPE_EXTERNAL) {
            return Files.FileType.External;
        }
        if(fileHandleType == FILE_TYPE_CLASSPATH) {
            return Files.FileType.Classpath;
        }
        return null;
    }

    public static int getFileTypeValue(Files.FileType fileType) {
        int fileHandleType = 0;
        if(fileType == Files.FileType.Internal) {
            fileHandleType = FILE_TYPE_INTERNAL;
        }
        if(fileType == Files.FileType.Local) {
            fileHandleType = FILE_TYPE_LOCAL;
        }
        if(fileType == Files.FileType.Absolute) {
            fileHandleType = FILE_TYPE_ABSOLUTE;
        }
        if(fileType == Files.FileType.External) {
            fileHandleType = FILE_TYPE_EXTERNAL;
        }
        if(fileType == Files.FileType.Classpath) {
            fileHandleType = FILE_TYPE_CLASSPATH;
        }
        return fileHandleType;
    }
}