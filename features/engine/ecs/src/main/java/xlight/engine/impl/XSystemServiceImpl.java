package xlight.engine.impl;

import com.badlogic.gdx.utils.Array;
import xlight.engine.ecs.system.XSystem;
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

    public XSystemServiceImpl(XECSWorldImpl world) {
        this.world = world;
        stepSystem = new Array<>();
        updateSystem = new Array<>();
        renderSystem = new Array<>();
        uiSystem = new Array<>();
        timeStep = new XSimpleFixedTimeStep();
        timeStep.addStepListener(this);
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
        else if(type.isGame()) {
            if(getSystem(renderSystem, system.getClass()) == -1) {
                renderSystem.add(new XSystemInternalData(system));
            }
        }
    }

    @Override
    public <T extends XSystem> T detachSystem(Class<T> classType) {
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
    public <T extends XSystem> T getSystem(Class<T> type) {
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
            return (T)data.system;
        }
        return null;
    }

    @Override
    public void onUpdate() {
        tickSystem(stepSystem);
    }

    void tickUpdateSystem() {
        tickSystem(updateSystem);
    }

    void tickStepSystem() {
        timeStep.tick();
    }

    void tickRenderSystem() {
        tickSystem(renderSystem);
    }

    void tickUISystem() {
        tickSystem(uiSystem);
    }

    private static int getSystem(Array<XSystemInternalData> systems, Class<?> type) {
        for(int i = 0; i < systems.size; i++) {
            XSystemInternalData data = systems.get(i);
            XSystem system = data.system;
            if(system.getClass() == type) {
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

            if(system.isEnabled()) {
                system.onTick(world);
            }
        }
    }

    private static class XSystemInternalData {
        public boolean callAttach = true;
        public XSystem system;

        public XSystemInternalData(XSystem system) {
            this.system = system;
        }
    }
}