package xlight.engine.scene;

import xlight.engine.core.register.XMetaClass;
import xlight.engine.core.register.XRegisterManager;
import xlight.engine.datamap.XDataMap;
import xlight.engine.datamap.XDataMapArray;
import xlight.engine.list.XIntMap;
import xlight.engine.pool.XPoolController;
import xlight.engine.scene.ecs.component.XSceneComponent;

public class XSceneMapUtils {

    public static boolean isSceneMap(XDataMap dataMap) {
        if(dataMap == null) {
            return false;
        }
        int sceneType = dataMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
        return sceneType == XSceneTypeValue.SCENE.getValue();
    }

    public static boolean isEntityMap(XDataMap dataMap) {
        if(dataMap == null) {
            return false;
        }
        int sceneType = dataMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
        return sceneType == XSceneTypeValue.ENTITY.getValue();
    }

    public static boolean isComponentMap(XDataMap dataMap) {
        if(dataMap == null) {
            return false;
        }
        int sceneType = dataMap.getInt(XSceneKeys.SCENE_TYPE.getKey(), 0);
        return sceneType == XSceneTypeValue.COMPONENT.getValue();
    }

    public static XDataMap getEntityMapFromSceneMap(XDataMap sceneDataMap, int sceneEntityId) {
        if(isSceneMap(sceneDataMap)) {
            return getEntityMapFromEntities(sceneDataMap, sceneEntityId);
        }
        return null;
    }

    public static void addAllEntities(XIntMap<XDataMap> entities, XDataMap sceneMap) {
        XDataMapArray entitiesArray = getEntityArray(sceneMap);
        if(entitiesArray != null) {
            for(int i = 0; i < entitiesArray.getSize(); i++) {
                XDataMap entityMap = entitiesArray.get(i);
                int entityId = entityMap.getInt(XSceneKeys.ENTITY_JSON_ID.getKey(), -1);
                if(entityId != -1) {
                    if(entities.containsKey(entityId)) {
                        throw new RuntimeException("Datamap should not contains multiple entity ids");
                    }
                    entities.put(entityId, entityMap);
                    addAllEntities(entities, entityMap);
                }
            }
        }
    }

    public static void addChildEntityMap(XDataMap entityMap, XDataMap childEntityMap) {
        if(isEntityMap(entityMap) && isEntityMap(childEntityMap)) {
            XDataMapArray entityArray = getEntityArray(entityMap);
            if(entityArray == null) {
                entityArray = entityMap.putDataMapArray(XSceneKeys.ENTITIES.getKey());
            }
            if(entityArray != null) {
                entityArray.add(childEntityMap);
            }
        }
    }

    public static XDataMap getEntityMapFromEntities(XDataMap entityMap, int sceneEntityId) {
        XDataMapArray entitiesArray = getEntityArray(entityMap);
        if(entitiesArray != null) {
            // TODO change to a int map?
            int size = entitiesArray.getSize();
            for(int i = 0; i < size; i++) {
                XDataMap entityDataMap = entitiesArray.get(i);
                if(isEntityMap(entityDataMap)) {
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

    public static XDataMap getEntityMapIdFromSceneComponent(XRegisterManager xRegisterManager, XDataMap entityMap, int sceneEntityId) {
        XDataMapArray entitiesArray = getEntityArray(entityMap);
        if(entitiesArray != null) {
            // TODO change to a int map?
            int size = entitiesArray.getSize();
            for(int i = 0; i < size; i++) {
                XDataMap entityDataMap = entitiesArray.get(i);
                if(isEntityMap(entityDataMap)) {
                    XDataMap sceneComponentDataMap = getComponentDataMapFromEntityMap(xRegisterManager, entityDataMap, XSceneComponent.class);
                    if(sceneComponentDataMap != null) {
                        int id = sceneComponentDataMap.getInt(XSceneComponent.MAP_ENTITY_ID, -1);
                        if(sceneEntityId == id) {
                            return entityDataMap;
                        }
                    }
                    // Check sub entities
                    XDataMap subEntityMap = getEntityMapIdFromSceneComponent(xRegisterManager, entityDataMap, sceneEntityId);
                    if(subEntityMap != null) {
                        return subEntityMap;
                    }
                }
            }
        }
        return null;
    }

    public static int getEntityID(XDataMap entityMap) {
        if(isEntityMap(entityMap)) {
            return entityMap.getInt(XSceneKeys.ENTITY_JSON_ID.getKey(), -1);
        }
        return -1;
    }

    public static String getEntityName(XDataMap entityMap) {
        if(isEntityMap(entityMap)) {
            return entityMap.getString(XSceneKeys.NAME.getKey(), "NoName");
        }
        return null;
    }

    public static XDataMapArray getEntityArray(XDataMap dataMap) {
        return dataMap.getDataMapArray(XSceneKeys.ENTITIES.getKey());
    }

    public static Class<?> getComponentClassFromComponentMap(XRegisterManager registerManager, XDataMap componentMap) {
        int key = getComponentClassValueFromComponentMap(componentMap);
        if(key != -1) {
            XMetaClass registeredClass = registerManager.getRegisteredClass(key);
            if(registeredClass != null) {
                return registeredClass.getType();
            }
        }
        return null;
    }

    public static int getComponentClassValueFromComponentMap(XDataMap componentMap) {
        if(isComponentMap(componentMap)) {
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
        XDataMapArray componentArray = getComponents(entityMap);
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
                int key = getComponentClassValueFromComponentMap(componentMap);
                if(key != -1 && key == componentKey) {
                    return componentMap;
                }
            }
        }
        return null;
    }

    public static XDataMapArray getComponents(XDataMap entityMap) {
        if(isEntityMap(entityMap)) {
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
        if(isComponentMap(componentMap)) {
            return componentMap.getDataMap(XSceneKeys.DATA.getKey());
        }
        return null;
    }

    public static XDataMap addEntity(XPoolController poolController, int id, String name) {
        XDataMap entityMap = poolController.obtainObject(XDataMap.class);
        entityMap.put(XSceneKeys.SCENE_TYPE.getKey(), XSceneTypeValue.ENTITY.getValue());
        entityMap.put(XSceneKeys.ENTITY_JSON_ID.getKey(), id);
        entityMap.put(XSceneKeys.NAME.getKey(), name);
        entityMap.put(XSceneKeys.VISIBLE.getKey(), true);
        entityMap.putDataMapArray(XSceneKeys.COMPONENTS.getKey());
        return entityMap;
    }

    public static XDataMap addComponent(XRegisterManager registerManager, XPoolController poolController, XDataMap entityMap, Class<?> componentType) {
        XDataMap componentMap = null;

        XDataMapArray componentsArray = getComponents(entityMap);
        if(componentsArray != null) {
            XMetaClass metaClass = registerManager.getRegisteredClass(componentType);
            if(metaClass != null) {
                componentMap = poolController.obtainObject(XDataMap.class);
                componentMap.put(XSceneKeys.SCENE_TYPE.getKey(), XSceneTypeValue.COMPONENT.getValue());
                componentMap.put(XSceneKeys.CLASS.getKey(), metaClass.getKey());
                componentsArray.add(componentMap);
            }
        }
        return componentMap;
    }

    public static XDataMap putComponentDataMap(XPoolController poolController, XDataMap componentMap) {
        if(isComponentMap(componentMap)) {
            XDataMap componentDataMap = XDataMap.obtain(poolController);
            componentMap.put(XSceneKeys.DATA.getKey(), componentDataMap);
            return componentDataMap;
        }
        return null;
    }
}