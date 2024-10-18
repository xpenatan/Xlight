package xlight.engine.scene;

import xlight.engine.core.register.XMetaClass;
import xlight.engine.core.register.XRegisterManager;
import xlight.engine.datamap.XDataMap;
import xlight.engine.datamap.XDataMapArray;
import xlight.engine.datamap.XDataMapListener;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.pool.XPoolController;

public class XSceneMapUtils {

    public static XDataMap getEntityMapFromSceneMap(XDataMap sceneDataMap, int sceneEntityId) {
        if(sceneDataMap == null) {
            return null;
        }
        int sceneType = sceneDataMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
        if(sceneType == XSceneTypeValue.SCENE.getValue()) {
            return getEntityMapFromEntities(sceneDataMap, sceneEntityId);
        }
        return null;
    }

    public static XDataMap getEntityMapFromEntities(XDataMap entityMap, int sceneEntityId) {
        XDataMapArray entitiesArray = getEntityArray(entityMap);
        if(entitiesArray != null) {
            // TODO change to a int map?
            int size = entitiesArray.getSize();
            for(int i = 0; i < size; i++) {
                XDataMap entityDataMap = entitiesArray.get(i);
                int entityType = entityDataMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
                if(entityType == XSceneTypeValue.ENTITY.getValue()) {
                    int jsonId = entityDataMap.getInt(XSceneKeys.ENTITY_JSON_ID.getKey(), -1);
                    if(sceneEntityId == jsonId) {
                        return entityDataMap;
                    }
                    // Check sub entities
                    XDataMap subEntityMap = getEntityMapFromEntities(entityDataMap, sceneEntityId);
                    if(subEntityMap != null) {
                        return subEntityMap;
                    }
                }
            }
        }
        return null;
    }

    public static int getEntityID(XDataMap entityMap) {
        int entityType = entityMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
        if(entityType == XSceneTypeValue.ENTITY.getValue()) {
            return entityMap.getInt(XSceneKeys.ENTITY_JSON_ID.getKey(), -1);
        }
        return -1;
    }

    public static XDataMapArray getEntityArray(XDataMap entityMap) {
        return entityMap.getDataMapArray(XSceneKeys.ENTITIES.getKey());
    }

    public static Class<?> getComponentTypeFromComponentMap(XRegisterManager registerManager, XDataMap componentMap) {
        if(componentMap == null) {
            return null;
        }
        int sceneType = componentMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
        if(sceneType == XSceneTypeValue.COMPONENT.getValue()) {
            int key = componentMap.getInt(XSceneKeys.CLASS.getKey(), -1);
            if(key != -1) {
                XMetaClass registeredClass = registerManager.getRegisteredClass(key);
                if(registeredClass != null) {
                    return registeredClass.getType();
                }
            }
        }
        return null;
    }

    public static int getComponentKeyFromComponentMap(XDataMap componentMap) {
        if(componentMap == null) {
            return -1;
        }
        int sceneType = componentMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
        if(sceneType == XSceneTypeValue.COMPONENT.getValue()) {
            return componentMap.getInt(XSceneKeys.CLASS.getKey(), -1);
        }
        return -1;
    }

    public static XDataMap getComponentMapFromEntityMap(XRegisterManager registerManager, XDataMap entityMap, Class<?> componentType) {
        XMetaClass metaClass = registerManager.getRegisteredClass(componentType);
        int componentKey = metaClass.getKey();
        return getComponentMapFromEntityMap(entityMap, componentKey);
    }

    public static XDataMap getComponentMapFromEntityMap(XDataMap entityMap, int componentKey) {
        XDataMapArray componentArray = getComponentArrayFromEntityMap(entityMap);
        return getComponentMapFromEntityComponentArray(componentArray, componentKey);
    }

    public static XDataMap getComponentMapFromEntityComponentArray(XRegisterManager registerManager, XDataMapArray componentArray, Class<?> componentType) {
        XMetaClass metaClass = registerManager.getRegisteredClass(componentType);
        int componentKey = metaClass.getKey();
        return getComponentMapFromEntityComponentArray(componentArray, componentKey);
    }

    public static XDataMap getComponentMapFromEntityComponentArray(XDataMapArray componentArray, int componentKey) {
        if(componentArray != null) {
            int size = componentArray.getSize();
            for(int i = 0; i < size; i++) {
                XDataMap componentMap = componentArray.get(i);
                int key = getComponentKeyFromComponentMap(componentMap);
                if(key != -1 && key == componentKey) {
                    return componentMap;
                }
            }
        }
        return null;
    }

    public static XDataMapArray getComponentArrayFromEntityMap(XDataMap entityMap) {
        int sceneType = entityMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
        if(sceneType == XSceneTypeValue.ENTITY.getValue()) {
            return entityMap.getDataMapArray(XSceneKeys.COMPONENTS.getKey());
        }
        return null;
    }

    public static XDataMap getComponentDataMapFromEntityMap(XRegisterManager registerManager, XDataMap entityMap, Class<?> componentType) {
        XDataMap componentMap = getComponentMapFromEntityMap(registerManager, entityMap, componentType);
        if(componentMap != null) {
            return componentMap.getDataMap(XSceneKeys.DATA.getKey());
        }
        return null;
    }

    public static XDataMap getComponentDataMapFromEntityMap(XDataMap entityMap, int componentKey) {
        XDataMap componentMap = getComponentMapFromEntityMap(entityMap, componentKey);
        if(componentMap != null) {
            return componentMap.getDataMap(XSceneKeys.DATA.getKey());
        }
        return null;
    }

    public static XDataMap getComponentDataMapFromComponentMap(XDataMap componentMap) {
        if(componentMap == null) {
            return null;
        }
        int sceneType = componentMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
        if(sceneType == XSceneTypeValue.COMPONENT.getValue()) {
            return componentMap.getDataMap(XSceneKeys.DATA.getKey());
        }
        return null;
    }

    public static XDataMap addComponent(XRegisterManager registerManager, XPoolController poolController, XDataMap entityMap, Class<?> componentType) {
        XDataMap componentMap = null;

        XDataMapArray componentsArray = getComponentArrayFromEntityMap(entityMap);
        if(componentsArray != null) {
            XMetaClass metaClass = registerManager.getRegisteredClass(componentType);
            if(metaClass != null) {
                componentMap = poolController.obtainObject(XDataMap.class);
                componentMap.put(XSceneKeys.SCENE_TYPE.getKey(), XSceneTypeValue.COMPONENT.getValue());
                componentMap.put(XSceneKeys.CLASS.getKey(), metaClass.getKey());
            }
        }
        return componentMap;
    }

    public static XDataMap putComponentDataMap(XPoolController poolController, XDataMap componentMap) {
        XDataMap componentDataMap = XDataMap.obtain(poolController);
        componentMap.put(XSceneKeys.DATA.getKey(), componentDataMap);
        return componentDataMap;
    }
}