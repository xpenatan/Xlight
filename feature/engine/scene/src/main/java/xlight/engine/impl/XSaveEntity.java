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
import xlight.engine.list.XIntSet;
import xlight.engine.list.XList;
import xlight.engine.pool.XPoolController;
import xlight.engine.scene.XSceneKeys;
import xlight.engine.scene.XSceneType;

class XSaveEntity {

    public static void save(XWorld world, XSceneImpl scene) {
        XEntityService entityService = world.getEntityService();
        XRegisterManager registerManager = world.getManager(XRegisterManager.class);
        XPoolController poolController = world.getGlobalData(XPoolController.class);

        XDataMap sceneDataMap = scene.sceneDataMap;

        XDataMapArray entitiesDataMapArray = poolController.obtainObject(XDataMapArray.class);
        sceneDataMap.put(XSceneKeys.ENTITIES.getKey(), entitiesDataMapArray);

        XList<XEntity> entities = entityService.getEntities();
        for(XEntity entity : entities) {
            if(entity.getParent() == null && entity.isSavable()) {
                XDataMap entityDataMap = saveEntity(entityService, poolController, registerManager, entity, true);
                entitiesDataMapArray.add(entityDataMap);
            }
        }

        String jsonStr = sceneDataMap.saveJsonStr();
        System.out.println("SAVE SCENE:");
        System.out.println(jsonStr);
    }

    /**
     *
     * @param saveChild true will save child entities is they are savable
     */
    private static XDataMap saveEntity(XEntityService entityService, XPoolController poolController, XRegisterManager registerManager, XEntity entity, boolean saveChild) {
        // TODO remove saving recursive
        XDataMap entityDataMap = saveEntity(poolController, registerManager, entity);
        if(saveChild) {
            boolean initSavableArray = false;
            XList<XIntSet.XIntSetNode> childIntList = entity.getChildList();
            XDataMapArray childEtitiesDataMapArray = null;
            for(XIntSet.XIntSetNode node : childIntList) {
                int key = node.getKey();
                XEntity child = entityService.getEntity(key);
                if(child.isSavable()) {
                    if(!initSavableArray) {
                        initSavableArray = true;
                        childEtitiesDataMapArray = poolController.obtainObject(XDataMapArray.class);
                        entityDataMap.put(XSceneKeys.ENTITIES.getKey(), childEtitiesDataMapArray);
                    }
                    XDataMap childEntityDataMap = saveEntity(entityService, poolController, registerManager, child, true);
                    childEtitiesDataMapArray.add(childEntityDataMap);
                }
            }
        }
        return entityDataMap;
    }

    private static XDataMap saveEntity(XPoolController poolController, XRegisterManager registerManager, XEntity entity) {
        XDataMap entityMap = poolController.obtainObject(XDataMap.class);

        entityMap.put(XSceneKeys.SCENE_TYPE.getKey(), XSceneType.ENTITY.getValue());
        entityMap.put(XSceneKeys.NAME.getKey(), entity.getName());
        entityMap.put(XSceneKeys.VISIBLE.getKey(), entity.isVisible());
        // TODO add tag solution
//        String tag = entity.getTag();
//        if(tag != null && !tag.isEmpty()) {
//            entityMap.put(XSceneKeys.TAG.getKey(), tag);
//        }

        int componentSize = entity.getComponentsSize();

        if(componentSize > 0) {
            XDataMapArray componentsDataMapArray = poolController.obtainObject(XDataMapArray.class);
            entityMap.put(XSceneKeys.COMPONENTS.getKey(), componentsDataMapArray);

            for(int i = 0; i < componentSize; i++) {
                XComponent component = entity.getComponentAt(i);
                XDataMap componentDataMap = saveComponent(poolController, registerManager, component);
                if(componentDataMap != null) {
                    componentsDataMapArray.add(componentDataMap);
                }
            }
        }

        return entityMap;
    }

    private static XDataMap saveComponent(XPoolController poolController, XRegisterManager registerManager, XComponent component) {
        XDataMap componentMap = null;
        XMetaClass registeredClass = registerManager.getRegisteredClass(component.getClass());
        if(registeredClass != null) {
            componentMap = poolController.obtainObject(XDataMap.class);
            componentMap.put(XSceneKeys.SCENE_TYPE.getKey(), XSceneType.COMPONENT.getValue());
            componentMap.put(XSceneKeys.CLASS.getKey(), registeredClass.getKey());

            if(component instanceof XDataMapListener) {
                XDataMap componentDataMap = XDataMap.obtain(poolController);
                componentMap.put(XSceneKeys.DATA.getKey(), componentDataMap);
                ((XDataMapListener)component).onSave(componentDataMap);
            }
        }
        return componentMap;
    }
}
