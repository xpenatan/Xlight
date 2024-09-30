package xlight.engine.impl;

import com.badlogic.gdx.utils.IntMap;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.system.XSystem;
import xlight.engine.ecs.system.XSystemBeginEndListener;
import xlight.engine.ecs.system.XSystemController;
import xlight.engine.ecs.system.XSystemData;
import xlight.engine.ecs.system.XSystemService;
import xlight.engine.ecs.system.XSystemType;
import xlight.engine.list.XIntMap;
import xlight.engine.list.XList;

class XSystemServiceImpl implements XSystemService {

    private XWorld world;

    private final XIntMap<XSystemData> systemMap;
    private final XSystemControllerImpl defaultController;
    private final IntMap<XSystemControllerImpl> customControllers;

    public XSystemServiceImpl(XWorld world) {
        this.world = world;

        systemMap = new XIntMap<>();
        defaultController = new XSystemControllerImpl(world);
        customControllers = new IntMap<>();
    }

    @Override
    public boolean attachSystem(XSystem system) {
        Class<?> classType = system.getClassType();
        XSystemData systemData = getSystemData(classType);
        if(systemData != null) {
            return false;
        }
        int key = classType.hashCode();
        XSystemInternalData internalSystemData = new XSystemInternalData(key, system);
        systemMap.put(key, internalSystemData);

        int systemControllerKey = system.getSystemController();
        XSystemControllerImpl systemController = getOrCreate(systemControllerKey);
        systemController.attachSystem(internalSystemData);
        return true;
    }

    @Override
    public <T extends XSystem> T detachSystem(Class<?> classType) {
        XSystemData systemData = systemMap.remove(classType.hashCode());
        if(systemData == null) {
            return null;
        }
        XSystem system = systemData.getSystem();
        system.onDetach(world, systemData);
        int systemControllerKey = system.getSystemController();
        XSystemControllerImpl systemController = getOrCreate(systemControllerKey);
        systemController.detachSystem(system);
        return (T)system;
    }

    @Override
    public <T extends XSystem> T getSystem(Class<?> type) {
        XSystemData data = systemMap.get(type.hashCode());
        if(data != null) {
            return (T)data.getSystem();
        }
        return null;
    }

    @Override
    public XList<XSystemData> getSystems() {
        return systemMap.getList();
    }

    @Override
    public XSystemData getSystemData(Class<?> type) {
        return systemMap.get(type.hashCode());
    }

    @Override
    public XSystemData getSystemData(int key) {
        return systemMap.get(key);
    }

    @Override
    public void addTickListener(XSystemType type, XSystemBeginEndListener listener) {
        defaultController.addTickListener(type, listener);
    }

    @Override
    public void removeTickListener(XSystemType type, XSystemBeginEndListener listener) {
        defaultController.removeTickListener(type, listener);
    }

    @Override
    public XSystemController getSystemController(int key) {
        return getOrCreate(key);
    }

    @Override
    public XSystemController getSystemController(XSystem system) {
        return getSystemController(system.getSystemController());
    }

    void tickUpdateSystem() {
        defaultController.tickUpdateSystem();
    }

    void tickStepSystem() {
        defaultController.tickTimeStepSystem();
    }

    void tickRenderSystem() {
        defaultController.tickRenderSystem();
    }

    void tickUISystem() {
        defaultController.tickUISystem();
    }

    private XSystemControllerImpl getOrCreate(int key) {
        if(key == XSystem.DEFAULT_CONTROLLER) {
            return defaultController;
        }
        XSystemControllerImpl customController = customControllers.get(key);
        if(customController == null) {
            customController = new XSystemControllerImpl(world);
            customControllers.put(key, customController);
        }
        return customController;
    }

    public static class XSystemInternalData implements XSystemData {
        public boolean callAttach = true;

        public boolean isEnabled = true;
        public boolean isForceUpdate = false;
        public XSystem system;
        public int key;

        public XSystemInternalData(int key, XSystem system) {
            this.key = key;
            this.system = system;
        }

        @Override
        public boolean isEnabled() {
            return isEnabled || isForceUpdate;
        }

        @Override
        public void setEnabled(boolean flag) {
            isEnabled = flag;
        }

        @Override
        public boolean isForceUpdate() {
            return isForceUpdate;
        }

        @Override
        public void setForceUpdate(boolean flag) {
            isForceUpdate = flag;
        }

        @Override
        public XSystem getSystem() {
            return system;
        }

        @Override
        public int getKey() {
            return key;
        }
    }
}