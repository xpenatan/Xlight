package xlight.engine.impl;


import xlight.engine.datamap.XDataMap;
import xlight.engine.datamap.XDataMapArray;
import xlight.engine.datamap.XDataMapListener;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.system.XSystem;
import xlight.engine.ecs.system.XSystemData;
import xlight.engine.ecs.system.XSystemService;
import xlight.engine.scene.XScene;
import xlight.engine.scene.XSceneKeys;
import xlight.engine.scene.XSceneType;

class XLoadSystem {

    public static void load(XWorld world, XScene scene) {
        XDataMap sceneDataMap = scene.getSceneDataMap();

        int sceneType = sceneDataMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
        if(sceneType == XSceneType.SCENE.getValue()) {
            XDataMapArray systemsArray = sceneDataMap.getDataMapArray(XSceneKeys.SYSTEMS.getKey());
            if(systemsArray != null) {
                int size = systemsArray.getSize();
                for(int i = 0; i < size; i++) {
                    XDataMap systemDataMap = systemsArray.get(i);
                    loadSystem(world, systemDataMap);
                }
            }
        }
    }

    private static void loadSystem(XWorld world, XDataMap systemMap) {
        XSystemService systemService = world.getSystemService();
        int sceneType = systemMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
        if(sceneType == XSceneType.SYSTEM.getValue()) {
            int key = systemMap.getInt(XSceneKeys.CLASS.getKey(), -1);
            if(key == -1) {
                return;
            }
            XSystemData systemData = systemService.getSystemData(key);
            if(systemData == null) {
                return;
            }
            boolean isEnable = systemMap.getBoolean(XSceneKeys.ENABLE.getKey(), true);
            boolean isForceUpdate = systemMap.getBoolean(XSceneKeys.ENABLE_FORCE.getKey(), false);

            systemData.setEnabled(isEnable);
            systemData.setForceUpdate(isForceUpdate);
            XDataMap systemDataMap = systemMap.getDataMap(XSceneKeys.DATA.getKey());
            XSystem system = systemData.getSystem();
            if(systemDataMap != null && system instanceof XDataMapListener) {
                ((XDataMapListener)system).onLoad(systemDataMap);
            }
        }
    }
}