package xlight.engine.impl;

import xlight.engine.datamap.XDataMap;
import xlight.engine.datamap.XDataMapArray;
import xlight.engine.datamap.XDataMapListener;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.manager.XManager;
import xlight.engine.scene.XScene;
import xlight.engine.scene.XSceneKeys;
import xlight.engine.scene.XSceneType;

class XLoadManager {

    public static void load(XWorld world, XScene scene) {
        XDataMap sceneDataMap = scene.getSceneDataMap();

        int sceneType = sceneDataMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
        if(sceneType == XSceneType.SCENE.getValue()) {
            XDataMapArray managersArray = sceneDataMap.getDataMapArray(XSceneKeys.MANAGERS.getKey());
            if(managersArray != null) {
                int size = managersArray.getSize();
                for(int i = 0; i < size; i++) {
                    XDataMap managerDataMap = managersArray.get(i);
                    loadManager(world, managerDataMap);
                }
            }
        }
    }

    private static void loadManager(XWorld world, XDataMap managerMap) {
        int sceneType = managerMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
        if(sceneType == XSceneType.MANAGER.getValue()) {
            int key = managerMap.getInt(XSceneKeys.CLASS.getKey(), -1);
            if(key == -1) {
                return;
            }
            XManager manager = world.getManager(key);
            if(manager == null) {
                return;
            }
            XDataMap managerDataMap = managerMap.getDataMap(XSceneKeys.DATA.getKey());
            if(managerDataMap != null && manager instanceof XDataMapListener) {
                ((XDataMapListener)manager).onLoad(managerDataMap);
            }
        }
    }
}