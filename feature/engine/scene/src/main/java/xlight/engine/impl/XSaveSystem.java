package xlight.engine.impl;

import xlight.engine.core.register.XRegisterManager;
import xlight.engine.datamap.XDataMap;
import xlight.engine.datamap.XDataMapArray;
import xlight.engine.datamap.XDataMapListener;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.system.XSystem;
import xlight.engine.ecs.system.XSystemData;
import xlight.engine.ecs.system.XSystemService;
import xlight.engine.list.XList;
import xlight.engine.pool.XPoolController;
import xlight.engine.scene.XScene;
import xlight.engine.scene.XSceneKeys;
import xlight.engine.scene.XSceneTypeValue;

class XSaveSystem {

    public static void save(XWorld world, XScene scene) {
        XRegisterManager registerManager = world.getManager(XRegisterManager.class);
        XPoolController poolController = world.getGlobalData(XPoolController.class);
        XSystemService systemService = world.getWorldService().getSystemService();

        XDataMap sceneDataMap = scene.getSceneDataMap();

        XDataMapArray systemsDataMapArray = poolController.obtainObject(XDataMapArray.class);
        sceneDataMap.put(XSceneKeys.SYSTEMS.getKey(), systemsDataMapArray);
        XList<XSystemData> systems = systemService.getSystems();
        for(XSystemData systemData : systems) {
            XDataMap entityDataMap = saveSystem(poolController, registerManager, systemData);
            systemsDataMapArray.add(entityDataMap);
        }
    }

    public static XDataMap saveSystem(XPoolController poolController, XRegisterManager registerManager, XSystemData systemData) {
        XDataMap systemMap = poolController.obtainObject(XDataMap.class);

        boolean isEnable = systemData.isEnabled();
        boolean forceUpdate = systemData.isForceUpdate();
        int key = systemData.getKey();
        XSystem system = systemData.getSystem();

        systemMap.put(XSceneKeys.SCENE_TYPE.getKey(), XSceneTypeValue.SYSTEM.getValue());
        systemMap.put(XSceneKeys.ENABLE.getKey(), isEnable);
        systemMap.put(XSceneKeys.ENABLE_FORCE.getKey(), forceUpdate);
        systemMap.put(XSceneKeys.CLASS.getKey(), key);

        if(system instanceof XDataMapListener) {
            XDataMap systemDataMap = XDataMap.obtain(poolController);
            systemMap.put(XSceneKeys.DATA.getKey(), systemDataMap);
            ((XDataMapListener)system).onSave(systemDataMap);
        }

        return systemMap;
    }
}