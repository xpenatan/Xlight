package xlight.engine.impl;

import com.badlogic.gdx.utils.IntMap;
import xlight.engine.ecs.XECSWorld;
import xlight.engine.ecs.component.XComponentService;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.ecs.event.XEventService;
import xlight.engine.ecs.service.XService;
import xlight.engine.ecs.system.XSystemService;

public class XECSWorldImpl implements XECSWorld {

    private float deltaTime;
    private final IntMap<XService> services;

    XComponentServiceImpl componentService;
    XEntityServiceImpl entityService;
    XSystemServiceImpl systemService;
    XEventServiceImpl eventService;

    public XECSWorldImpl() {
        services = new IntMap<>();
        entityService = new XEntityServiceImpl();
        componentService = new XComponentServiceImpl(entityService);
        systemService = new XSystemServiceImpl(this);
        eventService = new XEventServiceImpl();
    }

    @Override
    public <T extends XService> void attachService(Class<T> type, T service) {
        services.put(type.hashCode(), service);
    }

    @Override
    public <T extends XService> boolean detachService(Class<T> type) {
        return services.remove(type.hashCode()) != null;
    }

    @Override
    public <T extends XService> T getService(Class<T> type) {
        return (T)services.get(type.hashCode());
    }

    @Override
    public XSystemService getSystemService() {
        return systemService;
    }

    @Override
    public XEntityService getEntityService() {
        return entityService;
    }

    @Override
    public XComponentService getComponentService() {
        return componentService;
    }

    @Override
    public XEventService getEventService() {
        return eventService;
    }

    @Override
    public float getDeltaTime() {
        return deltaTime;
    }

    @Override
    public void tickUpdate(float deltaTime) {
        this.deltaTime = deltaTime;
        eventService.update();
        systemService.tickStepSystem();
        systemService.tickUpdateSystem();
    }

    @Override
    public void tickUI() {
        systemService.tickUISystem();
    }

    @Override
    public void tickRender() {
        systemService.tickRenderSystem();
    }
}