package xlight.engine.impl;

import com.badlogic.gdx.utils.Array;
import xlight.engine.ecs.system.XSystem;
import xlight.engine.ecs.system.XSystemBeginEndListener;
import xlight.engine.ecs.system.XSystemController;
import xlight.engine.ecs.system.XSystemType;
import xlight.engine.ecs.util.timestep.timestep.XSimpleFixedTimeStep;
import xlight.engine.ecs.util.timestep.timestep.XStepUpdate;
import xlight.engine.list.XIntMap;

class XSystemControllerImpl implements XSystemController, XStepUpdate {

    private final XIntMap<XSystemServiceImpl.XSystemInternalData> systemMap;
    private final XECSWorldImpl world;

    private final Array<XSystemServiceImpl.XSystemInternalData> stepSystem;
    private final Array<XSystemServiceImpl.XSystemInternalData> updateSystem;
    private final Array<XSystemServiceImpl.XSystemInternalData> renderSystem;
    private final Array<XSystemServiceImpl.XSystemInternalData> uiSystem;
    private final XSimpleFixedTimeStep timeStep;

    private final Array<XSystemBeginEndListener> updateListener;
    private final Array<XSystemBeginEndListener> stepListener;
    private final Array<XSystemBeginEndListener> uiListener;
    private final Array<XSystemBeginEndListener> renderListener;

    public XSystemControllerImpl(XECSWorldImpl world, XIntMap<XSystemServiceImpl.XSystemInternalData> systemMap) {
        this.world = world;
        this.systemMap = systemMap;

        stepSystem = new Array<>();
        updateSystem = new Array<>();
        renderSystem = new Array<>();
        uiSystem = new Array<>();
        timeStep = new XSimpleFixedTimeStep();
        timeStep.addStepListener(this);

        updateListener = new Array<>();
        stepListener = new Array<>();
        uiListener = new Array<>();
        renderListener = new Array<>();
    }

    @Override
    public void onUpdate() {
        tickSystem(stepSystem);
    }

    @Override
    public void tickTimeStepSystem() {
        tickBeginListener(stepListener);
        timeStep.tick();
        tickEndListener(stepListener);
    }

    @Override
    public void tickUpdateSystem() {

        tickBeginListener(updateListener);
        tickSystem(updateSystem);
        tickEndListener(updateListener);
    }

    @Override
    public void tickRenderSystem() {
        tickBeginListener(renderListener);
        tickSystem(renderSystem);
        tickEndListener(renderListener);
    }

    @Override
    public void tickUISystem() {
        tickBeginListener(uiListener);
        tickSystem(uiSystem);
        tickEndListener(uiListener);
    }

    public boolean attachSystem(XSystemServiceImpl.XSystemInternalData systemData) {
        XSystemType type = systemData.system.getType();
        switch(type) {
            case UPDATE: {
                this.updateSystem.add(systemData);
                return true;
            }
            case TIMESTEP: {
                this.stepSystem.add(systemData);
                return true;
            }
            case RENDER: {
                this.renderSystem.add(systemData);
                return true;
            }
            case UI: {
                this.uiSystem.add(systemData);
                return true;
            }
        }
        return false;
    }

    public boolean detachSystem(XSystemServiceImpl.XSystemInternalData systemData) {
        Class<?> classType = systemData.system.getClassType();
        XSystemType type = systemData.system.getType();

        switch(type) {
            case UPDATE: {
                int index = getSystem(this.updateSystem, classType);
                this.updateSystem.removeIndex(index);
                return true;
            }
            case TIMESTEP: {
                int index = getSystem(this.stepSystem, classType);
                this.stepSystem.removeIndex(index);
                return true;
            }
            case RENDER: {
                int index = getSystem(this.renderSystem, classType);
                this.renderSystem.removeIndex(index);
                return true;
            }
            case UI: {
                int index = getSystem(this.uiSystem, classType);
                this.uiSystem.removeIndex(index);
                return true;
            }
        }
        return false;
    }

    @Override
    public void addTickListener(XSystemType type, XSystemBeginEndListener listener) {
        if(type == XSystemType.UPDATE) {
            updateListener.add(listener);
        }
        else if(type == XSystemType.TIMESTEP) {
            stepListener.add(listener);
        }
        else if(type == XSystemType.UI) {
            uiListener.add(listener);
        }
        else if(type == XSystemType.RENDER) {
            renderListener.add(listener);
        }
    }

    @Override
    public void removeTickListener(XSystemType type, XSystemBeginEndListener listener) {
        if(type == XSystemType.UPDATE) {
            updateListener.removeValue(listener, true);
        }
        else if(type == XSystemType.TIMESTEP) {
            stepListener.removeValue(listener, true);
        }
        else if(type == XSystemType.UI) {
            uiListener.removeValue(listener, true);
        }
        else if(type == XSystemType.RENDER) {
            renderListener.removeValue(listener, true);
        }
    }

    private static int getSystem(Array<XSystemServiceImpl.XSystemInternalData> systems, Class<?> type) {
        for(int i = 0; i < systems.size; i++) {
            XSystemServiceImpl.XSystemInternalData data = systems.get(i);
            XSystem system = data.system;
            if(system.getClassType() == type) {
                return i;
            }

        }
        return -1;
    }

    private void tickSystem(Array<XSystemServiceImpl.XSystemInternalData> systems) {
        for(int i = 0; i < systems.size; i++) {
            XSystemServiceImpl.XSystemInternalData data = systems.get(i);
            XSystem system = data.system;
            if(data.callAttach) {
                data.callAttach = false;
                system.onAttach(world, data);
            }

            if(data.isEnabled) {
                system.onTick(world);
            }
        }
    }

    private void tickBeginListener(Array<XSystemBeginEndListener> listeners) {
        for(int i = 0; i < listeners.size; i++) {
            XSystemBeginEndListener listener = listeners.get(i);
            listener.onBegin(world);
        }
    }

    private void tickEndListener(Array<XSystemBeginEndListener> listeners) {
        for(int i = 0; i < listeners.size; i++) {
            XSystemBeginEndListener listener = listeners.get(i);
            listener.onEnd(world);
        }
    }

}