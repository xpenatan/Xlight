package xlight.engine.scene;

import xlight.engine.datamap.XDataMap;

/**
 * XScene is an object that hold saved data from the scene in memory. This data does not contain real entities or systems.
 */
public interface XScene {

    int getId();

    String getName();

    void loadJson(String jsonStr);

    String getJson();

    void clear();

    XDataMap getSceneDataMap();

    XSceneType getType();
}