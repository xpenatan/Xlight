package xlight.engine.scene.ecs.component;

import com.badlogic.gdx.Files;
import xlight.engine.datamap.XDataMap;
import xlight.engine.datamap.XDataMapListener;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.pool.XPoolable;

public final class XSceneComponent implements XComponent, XDataMapListener, XPoolable {

    public static final int FILE_TYPE_INTERNAL = 1;
    public static final int FILE_TYPE_LOCAL = 2;
    public static final int FILE_TYPE_ABSOLUTE = 3;
    public static final int FILE_TYPE_EXTERNAL = 4;
    public static final int FILE_TYPE_CLASSPATH = 5;

    public int fileHandleType = FILE_TYPE_INTERNAL;
    public String scenePath = "";
    public int entityId = -1;

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

    public static final int MAP_ENTITY_ID = 1;
    public static final int MAP_SCENE_PATH = 2;
    public static final int MAP_FILE_TYPE = 3;

    @Override
    public void onSave(XDataMap map) {
        map.put(MAP_SCENE_PATH, scenePath);
        map.put(MAP_ENTITY_ID, entityId);
        map.put(MAP_FILE_TYPE, fileHandleType);
    }

    @Override
    public void onLoad(XDataMap map) {
        scenePath = map.getString(MAP_SCENE_PATH, "");
        entityId = map.getInt(MAP_ENTITY_ID, -1);
        fileHandleType = map.getInt(MAP_FILE_TYPE, FILE_TYPE_INTERNAL);
    }

    @Override
    public void onReset() {
        scenePath = "";
        entityId = -1;
        fileHandleType = FILE_TYPE_INTERNAL;
    }
}