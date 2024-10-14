package xlight.engine.scene;

import xlight.engine.core.register.XMetaClass;
import xlight.engine.core.register.XRegisterManager;
import xlight.engine.datamap.XDataMap;
import xlight.engine.datamap.XDataMapArray;

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

    private static XDataMap getEntityMapFromEntities(XDataMap entityMap, int sceneEntityId) {
        XDataMapArray entitiesArray = entityMap.getDataMapArray(XSceneKeys.ENTITIES.getKey());
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
        if(entityMap == null) {
            return null;
        }
        int sceneType = entityMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
        if(sceneType == XSceneTypeValue.ENTITY.getValue()) {
            XDataMapArray componentArray = entityMap.getDataMapArray(XSceneKeys.COMPONENTS.getKey());
            if(componentArray != null) {
                int size = componentArray.getSize();
                for(int i = 0; i < size; i++) {
                    XDataMap componentMap = componentArray.get(i);
                    Class<?> type = getComponentTypeFromComponentMap(registerManager, componentMap);
                    if(type == componentType) {
                        return componentMap;
                    }
                }
            }
        }
        return null;
    }

    public static XDataMap getComponentMapFromEntityMap(XDataMap entityMap, int componentKey) {
        if(entityMap == null) {
            return null;
        }
        int sceneType = entityMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
        if(sceneType == XSceneTypeValue.ENTITY.getValue()) {
            XDataMapArray componentArray = entityMap.getDataMapArray(XSceneKeys.COMPONENTS.getKey());
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
}