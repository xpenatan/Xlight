package xlight.engine.impl;

import com.badlogic.gdx.utils.IntMap;
import xlight.engine.ecs.system.XSystem;
import xlight.engine.ecs.system.XSystemBeginEndListener;
import xlight.engine.ecs.system.XSystemController;
import xlight.engine.ecs.system.XSystemData;
import xlight.engine.ecs.system.XSystemService;
import xlight.engine.ecs.system.XSystemType;
import xlight.engine.list.XIntMap;

class XSystemServiceImpl implements XSystemService {

    private XECSWorldImpl world;

    private final XIntMap<XSystemInternalData> systemMap;
    private final XSystemControllerImpl defaultController;
    private final IntMap<XSystemControllerImpl> customControllers;


    public XSystemServiceImpl(XECSWorldImpl world) {
        this.world = world;

        systemMap = new XIntMap<>();
        defaultController = new XSystemControllerImpl(world, systemMap);
        customControllers = new IntMap<>();
    }

    @Override
    public boolean attachSystem(XSystem system) {
        Class<?> classType = system.getClassType();
        XSystemData systemData = getSystemData(classType);
        if(systemData != null) {
            return false;
        }
        XSystemInternalData internalSystemData = new XSystemInternalData(system);
        systemMap.put(classType.hashCode(), internalSystemData);

        int systemControllerKey = system.getSystemController();
        XSystemControllerImpl systemController = getOrCreate(systemControllerKey);
        systemController.attachSystem(internalSystemData);
        return true;
    }

    @Override
    public <T extends XSystem> T detachSystem(Class<?> classType) {
        XSystemInternalData systemData = systemMap.remove(classType.hashCode());
        if(systemData == null) {
            return null;
        }
        systemData.system.onDetach(world, systemData);
        int systemControllerKey = systemData.system.getSystemController();
        XSystemControllerImpl systemController = getOrCreate(systemControllerKey);
        systemController.detachSystem(systemData);
        return (T)systemData.system;
    }

    @Override
    public <T extends XSystem> T getSystem(Class<?> type) {
        XSystemInternalData data = systemMap.get(type.hashCode());
        if(data != null) {
            return (T)data.system;
        }
        return null;
    }

    @Override
    public XSystemData getSystemData(Class<?> type) {
        return systemMap.get(type.hashCode());
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
            customController = new XSystemControllerImpl(world, systemMap);
            customControllers.put(key, customController);
        }
        return customController;
    }

    public static class XSystemInternalData implements XSystemData {
        public boolean callAttach = true;

        public boolean isEnabled = true;
        public XSystem system;

        public XSystemInternalData(XSystem system) {
            this.system = system;
        }

        @Override
        public boolean isEnabled() {
            return isEnabled;
        }

        @Override
        public void setEnabled(boolean flag) {
            isEnabled = flag;
        }

        @Override
        public XSystem getSystem() {
            return system;
        }
    }
}