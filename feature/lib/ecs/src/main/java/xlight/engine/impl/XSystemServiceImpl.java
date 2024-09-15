package xlight.engine.impl;

import com.badlogic.gdx.utils.Array;
import xlight.engine.ecs.system.XSystem;
import xlight.engine.ecs.system.XSystemBeginEndListener;
import xlight.engine.ecs.system.XSystemData;
import xlight.engine.ecs.system.XSystemService;
import xlight.engine.ecs.system.XSystemType;
import xlight.engine.ecs.util.timestep.timestep.XSimpleFixedTimeStep;
import xlight.engine.ecs.util.timestep.timestep.XStepUpdate;

public class XSystemServiceImpl implements XSystemService, XStepUpdate {

    private XECSWorldImpl world;

    private final Array<XSystemInternalData> stepSystem;
    private final Array<XSystemInternalData> updateSystem;
    private final Array<XSystemInternalData> renderSystem;
    private final Array<XSystemInternalData> uiSystem;
    private final XSimpleFixedTimeStep timeStep;

    private final Array<XSystemBeginEndListener> updateListener;
    private final Array<XSystemBeginEndListener> stepListener;
    private final Array<XSystemBeginEndListener> uiListener;
    private final Array<XSystemBeginEndListener> renderListener;

    public XSystemServiceImpl(XECSWorldImpl world) {
        this.world = world;
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
    public void attachSystem(XSystem system) {
        XSystemType type = system.getType();
        if(type.isTimestep()) {
            if(getSystem(stepSystem, system.getClass()) == -1) {
                stepSystem.add(new XSystemInternalData(system));
            }
        }
        else if(type.isUpdate()) {
            if(getSystem(updateSystem, system.getClass()) == -1) {
                updateSystem.add(new XSystemInternalData(system));
            }
        }
        else if(type.isUI()) {
            if(getSystem(uiSystem, system.getClass()) == -1) {
                uiSystem.add(new XSystemInternalData(system));
            }
        }
        else if(type.isRender()) {
            if(getSystem(renderSystem, system.getClass()) == -1) {
                renderSystem.add(new XSystemInternalData(system));
            }
        }
    }

    @Override
    public <T extends XSystem> T detachSystem(Class<?> classType) {
        int index = getSystem(this.updateSystem, classType);
        XSystemInternalData data = null;
        if(index >= 0) {
            data = updateSystem.removeIndex(index);
        }
        index = getSystem(this.stepSystem, classType);
        if(index >= 0) {
            data =  stepSystem.removeIndex(index);
        }
        index = getSystem(this.renderSystem, classType);
        if(index >= 0) {
            data =  renderSystem.removeIndex(index);
        }
        index = getSystem(this.uiSystem, classType);
        if(index >= 0) {
            data =  uiSystem.removeIndex(index);
        }
        if(data != null) {
            XSystem system = data.system;
            system.onDetach(world);
            return (T)system;
        }
        return null;
    }

    @Override
    public <T extends XSystem> T getSystem(Class<?> type) {
        XSystemInternalData data = getInternalSystemData(type);
        if(data != null) {
            return (T)data.system;
        }
        return null;
    }

    @Override
    public <T extends XSystem> XSystemData getSystemData(Class<T> type) {
        return getInternalSystemData(type);
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

    @Override
    public void onUpdate() {
        tickSystem(stepSystem);
    }

    void tickUpdateSystem() {
        tickBeginListener(updateListener);
        tickSystem(updateSystem);
        tickEndListener(updateListener);
    }

    void tickStepSystem() {
        tickBeginListener(stepListener);
        timeStep.tick();
        tickEndListener(stepListener);
    }

    void tickRenderSystem() {
        tickBeginListener(renderListener);
        tickSystem(renderSystem);
        tickEndListener(renderListener);
    }

    void tickUISystem() {
        tickBeginListener(uiListener);
        tickSystem(uiSystem);
        tickEndListener(uiListener);
    }

    private <T extends XSystem> XSystemInternalData getInternalSystemData(Class<?> type) {
        int index = getSystem(this.updateSystem, type);
        XSystemInternalData data = null;
        if(index >= 0) {
            data = updateSystem.get(index);
        }
        index = getSystem(this.stepSystem, type);
        if(index >= 0) {
            data = stepSystem.get(index);
        }
        index = getSystem(this.renderSystem, type);
        if(index >= 0) {
            data = renderSystem.get(index);
        }
        index = getSystem(this.uiSystem, type);
        if(index >= 0) {
            data = uiSystem.get(index);
        }
        if(data != null) {
            return data;
        }
        return null;
    }

    private static int getSystem(Array<XSystemInternalData> systems, Class<?> type) {
        for(int i = 0; i < systems.size; i++) {
            XSystemInternalData data = systems.get(i);
            XSystem system = data.system;
            if(system.getClassType() == type) {
                return i;
            }

        }
        return -1;
    }

    private void tickSystem(Array<XSystemInternalData> systems) {
        for(int i = 0; i < systems.size; i++) {
            XSystemInternalData data = systems.get(i);
            XSystem system = data.system;
            if(data.callAttach) {
                data.callAttach = false;
                system.onAttach(world);
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

    private static class XSystemInternalData implements XSystemData {
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
    }
}