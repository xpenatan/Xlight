package xlight.engine.scene;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import xlight.engine.datamap.XDataMap;

/**
 * XScene is an object that hold saved data from the scene in memory. This data does not contain real entities or systems.
 */
public interface XScene {

    int getId();

    String getName();

    String getJson();

    void clear();

    XDataMap getSceneDataMap();

    XSceneTypeValue getType();

    String getPath();

    Files.FileType getFileType();
}