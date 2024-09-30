package xlight.engine.impl;

import xlight.engine.core.register.XRegisterManager;
import xlight.engine.datamap.XDataMap;
import xlight.engine.datamap.XDataMapArray;
import xlight.engine.datamap.XDataMapListener;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.manager.XManager;
import xlight.engine.ecs.manager.XManagerData;
import xlight.engine.list.XList;
import xlight.engine.pool.XPoolController;
import xlight.engine.scene.XScene;
import xlight.engine.scene.XSceneKeys;
import xlight.engine.scene.XSceneTypeValue;

class XSaveManager {

    public static void save(XWorld world, XScene scene) {
        XRegisterManager registerManager = world.getManager(XRegisterManager.class);
        XPoolController poolController = world.getGlobalData(XPoolController.class);
        XDataMap sceneDataMap = scene.getSceneDataMap();

        XDataMapArray managersDataMapArray = poolController.obtainObject(XDataMapArray.class);
        sceneDataMap.put(XSceneKeys.MANAGERS.getKey(), managersDataMapArray);
        XList<XManagerData> managers = world.getManagers();
        for(XManagerData managerData : managers) {
            XDataMap entityDataMap = saveManager(poolController, registerManager, managerData);
            managersDataMapArray.add(entityDataMap);
        }
    }

    public static XDataMap saveManager(XPoolController poolController, XRegisterManager registerManager, XManagerData managerData) {
        XDataMap managerMap = poolController.obtainObject(XDataMap.class);

        int key = managerData.getKey();
        XManager manager = managerData.getManager();

        managerMap.put(XSceneKeys.SCENE_TYPE.getKey(), XSceneTypeValue.MANAGER.getValue());
        managerMap.put(XSceneKeys.CLASS.getKey(), key);

        if(manager instanceof XDataMapListener) {
            XDataMap systemDataMap = XDataMap.obtain(poolController);
            managerMap.put(XSceneKeys.DATA.getKey(), systemDataMap);
            ((XDataMapListener)manager).onSave(systemDataMap);
        }

        return managerMap;
    }
}