package xlight.engine.impl;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import xlight.engine.core.asset.XAssetUtil;
import xlight.engine.core.register.XMetaClass;
import xlight.engine.core.register.XRegisterManager;
import xlight.engine.datamap.XDataMap;
import xlight.engine.datamap.XDataMapArray;
import xlight.engine.datamap.XDataMapListener;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.list.XIntSetNode;
import xlight.engine.list.XList;
import xlight.engine.pool.XPoolController;
import xlight.engine.scene.XScene;
import xlight.engine.scene.XSceneKeys;
import xlight.engine.scene.XSceneMapUtils;
import xlight.engine.scene.XSceneTypeValue;
import xlight.engine.scene.ecs.component.XSceneComponent;

class XSaveEntity {

    public static void save(XWorld world, XScene scene) {
        XEntityService entityService = world.getWorldService().getEntityService();
        XRegisterManager registerManager = world.getManager(XRegisterManager.class);
        XPoolController poolController = world.getGlobalData(XPoolController.class);

        XDataMap sceneDataMap = scene.getSceneDataMap();

        XDataMapArray entitiesDataMapArray = poolController.obtainObject(XDataMapArray.class);
        sceneDataMap.put(XSceneKeys.ENTITIES.getKey(), entitiesDataMapArray);

        XList<XEntity> entities = entityService.getEntities();
        for(XEntity entity : entities) {
            if(entity.getParent() == null && entity.isSavable()) {
                XDataMap entityDataMap = saveEntity(entityService, poolController, registerManager, entity);
                entitiesDataMapArray.add(entityDataMap);
            }
        }

//        String jsonStr = sceneDataMap.saveJsonStr();
//        System.out.println("SAVE SCENE:");
//        System.out.println(jsonStr);
    }

    public static XDataMap saveEntity(XEntityService entityService, XPoolController poolController, XRegisterManager registerManager, XEntity entity) {
        // TODO remove saving recursive
        XDataMap entityDataMap = saveEntity(poolController, registerManager, entity);
        boolean initSavableArray = false;
        XList<XIntSetNode> childIntList = entity.getChildList();
        XDataMapArray childEtitiesDataMapArray = null;
        for(XIntSetNode node : childIntList) {
            int key = node.getKey();
            XEntity child = entityService.getEntity(key);
            if(child.isSavable()) {
                if(!initSavableArray) {
                    initSavableArray = true;
                    childEtitiesDataMapArray = poolController.obtainObject(XDataMapArray.class);
                    entityDataMap.put(XSceneKeys.ENTITIES.getKey(), childEtitiesDataMapArray);
                }
                XDataMap childEntityDataMap = saveEntity(entityService, poolController, registerManager, child);
                childEtitiesDataMapArray.add(childEntityDataMap);
            }
        }
        return entityDataMap;
    }

    private static XDataMap saveEntity(XPoolController poolController, XRegisterManager registerManager, XEntity entity) {
        XDataMap entityMap = poolController.obtainObject(XDataMap.class);

        entityMap.put(XSceneKeys.SCENE_TYPE.getKey(), XSceneTypeValue.ENTITY.getValue());
        entityMap.put(XSceneKeys.NAME.getKey(), entity.getName());
        entityMap.put(XSceneKeys.VISIBLE.getKey(), entity.isVisible());
        entityMap.put(XSceneKeys.ENTITY_JSON_ID.getKey(), entity.getId());
        // TODO add tag solution
//        String tag = entity.getTag();
//        if(tag != null && !tag.isEmpty()) {
//            entityMap.put(XSceneKeys.TAG.getKey(), tag);
//        }

        int componentSize = entity.getComponentsSize();

        if(componentSize > 0) {
            XDataMapArray componentsDataMapArray = poolController.obtainObject(XDataMapArray.class);
            entityMap.put(XSceneKeys.COMPONENTS.getKey(), componentsDataMapArray);

            XSceneComponent sceneComponent = entity.getComponent(XSceneComponent.class);
            XDataMap sceneEntityMap = null;
            if(sceneComponent != null) {
                Files.FileType fileType = XAssetUtil.getFileTypeEnum(sceneComponent.fileHandleType);
                FileHandle fileHandle = Gdx.files.getFileHandle(sceneComponent.scenePath, fileType);
                if(fileHandle.exists()) {
                    String jsonStr = fileHandle.readString();
                    XDataMap sceneDataMap = XDataMap.obtain(poolController);
                    sceneDataMap.loadJson(jsonStr);
                    sceneEntityMap = XSceneMapUtils.getEntityMapFromSceneMap(sceneDataMap, sceneComponent.entityId);
                }
            }

            for(int i = 0; i < componentSize; i++) {
                XComponent component = entity.getComponentAt(i);
                Class<? extends XComponent> componentType = component.getClass();
                XMetaClass metaClass = registerManager.getRegisteredClass(componentType);
                if(metaClass != null) {
                    int componentKey = metaClass.getKey();
                    XDataMap componentMap = saveComponent(poolController, metaClass, component);
                    boolean addComponent = true;
                    if(sceneEntityMap != null) {
                        XDataMap sceneComponentMap = XSceneMapUtils.getComponentMapFromEntityMap(sceneEntityMap, componentKey);
                        if(sceneComponentMap != null) {
                            XDataMap sceneComponentDataMap = sceneComponentMap.getDataMap(XSceneKeys.DATA.getKey());
                            if(sceneComponentDataMap != null) {
                                XDataMap componentDataMap = XSceneMapUtils.getComponentDataMapFromComponentMap(componentMap);
                                if(sceneComponentDataMap.equals(componentDataMap)) {
                                    addComponent = false;
                                }
                            }
                            else {
                                // Component don't have data map so it should be equal
                                addComponent = false;
                            }
                        }
                    }
                    if(addComponent) {
                        componentsDataMapArray.add(componentMap);
                    }
                    else {
                        componentMap.free();
                    }
                }
            }
        }

        return entityMap;
    }

    private static XDataMap saveComponent(XPoolController poolController, XMetaClass metaClass, XComponent component) {
        XDataMap componentMap = null;
        componentMap = poolController.obtainObject(XDataMap.class);
        componentMap.put(XSceneKeys.SCENE_TYPE.getKey(), XSceneTypeValue.COMPONENT.getValue());
        componentMap.put(XSceneKeys.CLASS.getKey(), metaClass.getKey());
        if(component instanceof XDataMapListener) {
            XDataMap componentDataMap = XDataMap.obtain(poolController);
            ((XDataMapListener)component).onSave(componentDataMap);
            if(componentDataMap.getSize() == 0) {
                componentDataMap.free();
            }
            else {
                componentMap.put(XSceneKeys.DATA.getKey(), componentDataMap);
            }
        }
        return componentMap;
    }
}
