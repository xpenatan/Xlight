package xlight.engine.impl;

import xlight.engine.core.register.XMetaClass;
import xlight.engine.core.register.XRegisterManager;
import xlight.engine.datamap.XDataMap;
import xlight.engine.datamap.XDataMapArray;
import xlight.engine.datamap.XDataMapListener;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.list.XList;
import xlight.engine.pool.XPoolController;
import xlight.engine.scene.XScene;
import xlight.engine.scene.XSceneKeys;
import xlight.engine.scene.XSceneType;

public class XLoadEntity {
    public static void loadEntities(XWorld world, XScene scene) {
        System.out.println("LOAD SCENE:");
        XDataMap sceneDataMap = scene.getSceneDataMap();
        int sceneType = sceneDataMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
        if(sceneType == XSceneType.SCENE.getValue()) {
            XDataMapArray entitiesArray = sceneDataMap.getDataMapArray(XSceneKeys.ENTITIES.getKey());
            if(entitiesArray != null) {
                int size = entitiesArray.getSize();
                for(int i = 0; i < size; i++) {
                    XDataMap entityDataMap = entitiesArray.get(i);
                    loadEntityAndInit(world, entityDataMap);
                }
            }
        }
    }

    public static XEntity loadEntityAndInit(XWorld world, XDataMap entityDataMap) {
        // TODO remove loading recursive
        XEntityService entityService = world.getEntityService();
        XEntity entity = loadEntity(world, entityDataMap);
        if(entity != null) {
            entityService.attachEntity(entity);
            XDataMapArray childEntitiesArray = entityDataMap.getDataMapArray(XSceneKeys.ENTITIES.getKey());
            if(childEntitiesArray != null) {
                XList<XDataMap> list = childEntitiesArray.getList();
                for(XDataMap childDataMap : list) {
                    XEntity childEntity = loadEntityAndInit(world, childDataMap);
                    if(childEntity != null) {
                        childEntity.setParent(entity);
                    }
                }
            }
        }
        return entity;
    }

    public static XEntity loadEntity(XWorld world, XDataMap entityMap) {
        XEntityService entityService = world.getEntityService();
        int sceneType = entityMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
        if(sceneType == XSceneType.ENTITY.getValue()) {

            String entityName = entityMap.getString(XSceneKeys.NAME.getKey(), "");
            boolean isEnable = entityMap.getBoolean(XSceneKeys.ENABLE.getKey(), true);
            boolean isVisible = entityMap.getBoolean(XSceneKeys.VISIBLE.getKey(), true);
            String tag = entityMap.getString(XSceneKeys.TAG.getKey(), "");

            XEntity entity = entityService.obtain();
            entity.setVisible(isVisible);
            entity.setName(entityName);
//            entity.setTag(tag);
            entity.setSavable(true);

            XDataMapArray componentsDataMap = entityMap.getDataMapArray(XSceneKeys.COMPONENTS.getKey());
            if(componentsDataMap != null) {
                int size = componentsDataMap.getSize();

                for(int i = 0; i < size; i++) {
                    XDataMap componentDataMap = componentsDataMap.get(i);
                    XComponent component = loadComponent(world, componentDataMap);
                    if(component != null) {
                        entity.attachComponent(component);
                    }
                }
            }

            return entity;
        }
        return null;
    }

    public static XComponent loadComponent(XWorld world, XDataMap componentMap) {
        XRegisterManager registerManager = world.getManager(XRegisterManager.class);
        XPoolController poolController = world.getGlobalData(XPoolController.class);

        int sceneType = componentMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
        if(sceneType == XSceneType.COMPONENT.getValue()) {
            int key = componentMap.getInt(XSceneKeys.CLASS.getKey(), -1);
            if(key != -1) {
                XMetaClass registeredClass = registerManager.getRegisteredClass(key);
                if(registeredClass != null) {
                    Object o = poolController.obtainObject(registeredClass.getType());
                    if(o instanceof XComponent) {
                        XComponent component = (XComponent)o;
                        XDataMap dataMap = componentMap.getDataMap(XSceneKeys.DATA.getKey());
                        if(dataMap != null && component instanceof XDataMapListener) {
                            ((XDataMapListener)component).onLoad(dataMap);
                        }
                        return component;
                    }
                    else {
                        throw new RuntimeException("Wrong component type: " + o.getClass());
                    }
                }
            }
            return null;
        }
        return null;
    }
}
