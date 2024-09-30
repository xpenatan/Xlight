package xlight.engine.impl;

import com.badlogic.gdx.utils.IntMap;
import xlight.engine.core.register.XMetaClass;
import xlight.engine.core.register.XRegisterManager;
import xlight.engine.core.util.XClassHelper;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.ecs.component.XComponentService;
import xlight.engine.ecs.manager.XManager;
import xlight.engine.list.XIntMap;
import xlight.engine.list.XList;
import xlight.engine.pool.XPool;
import xlight.engine.pool.XPoolController;

class XRegisterManagerImpl implements XRegisterManager, XManager {

    public final IntMap<XMetaClassImpl> registeredMetaClass;
    public final XIntMap<XMetaClass> keyToHashMap;

    private XWorld world;

    public XRegisterManagerImpl(XWorld world) {
        this.world = world;
        registeredMetaClass = new IntMap<>();
        keyToHashMap = new XIntMap<>();
    }

    @Override
    public XList<XMetaClass> getRegisteredClasses() {
        return keyToHashMap.getList();
    }

    @Override
    public boolean isRegisteredClass(Class<?> type) {
        int hash = type.hashCode();
        return registeredMetaClass.containsKey(hash);
    }

    @Override
    public boolean isRegisteredClass(int key) {
        return keyToHashMap.containsKey(key);
    }

    @Override
    public XMetaClass getRegisteredClass(Class<?> type) {
        return registeredMetaClass.get(type.hashCode());
    }

    @Override
    public XMetaClass getRegisteredClass(int key) {
        return keyToHashMap.get(key);
    }

    @Override
    public XMetaClass registerClass(int key, Class<?> type) {
        return registerClass(key, type, null);
    }

    @Override
    public XMetaClass registerClass(int key, Class<?> type, XPool<?> pool) {
        XMetaClassImpl metaClass = registerMetaClassInternal(key, type);
        if(metaClass != null) {
            if(XClassHelper.classExtends(XComponent.class, type)) {
                XComponentService componentService = world.getWorldService().getComponentService();
                componentService.registerComponent((Class<XComponent>)type);
                if(pool != null) {
                    XPoolController globalData = world.getGlobalData(XPoolController.class);
                    globalData.registerPool(type, pool);
                }
            }
        }
        return metaClass;
    }

    private XMetaClassImpl registerMetaClassInternal(int key, Class<?> type) {
        int hash = type.hashCode();
        XMetaClassImpl metaClass = registeredMetaClass.get(hash);
        if(metaClass == null && !keyToHashMap.containsKey(key)) {
            metaClass = new XMetaClassImpl(key, type);
            registeredMetaClass.put(hash, metaClass);
            keyToHashMap.put(key, metaClass);
            return metaClass;
        }
        return null;
    }
}