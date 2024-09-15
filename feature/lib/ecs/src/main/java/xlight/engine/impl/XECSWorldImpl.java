package xlight.engine.impl;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.component.XComponentService;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.ecs.event.XEventService;
import xlight.engine.ecs.manager.XManager;
import xlight.engine.ecs.service.XService;
import xlight.engine.ecs.system.XSystemService;
import xlight.engine.list.XIntMap;

public class XECSWorldImpl implements XWorld {

    private float deltaTime;
    private final XIntMap<XService> services;
    private final IntMap<XManager> managers;

    private final Array<XService> initServices;
    private final Array<XManager> initManagers;

    XComponentServiceImpl componentService;
    XEntityServiceImpl entityService;
    XSystemServiceImpl systemService;
    XEventServiceImpl eventService;

    public XECSWorldImpl() {
        services = new XIntMap<>();
        managers = new IntMap<>();
        entityService = new XEntityServiceImpl();
        componentService = new XComponentServiceImpl(entityService);
        systemService = new XSystemServiceImpl(this);
        eventService = new XEventServiceImpl(this);

        initServices = new Array<>();
        initManagers = new Array<>();
    }

    @Override
    public <T extends XService> void attachService(Class<?> type, XService service) {
        int key = type.hashCode();
        if(!services.contains(key)) {
            services.put(type.hashCode(), service);
            initServices.add(service);
        }
    }

    @Override
    public boolean detachService(Class<?> type) {
        XService service = services.remove(type.hashCode());
        if(service != null) {
            service.onDetach(this);
            return true;
        }
        return false;
    }

    @Override
    public <T> T getService(Class<T> type) {
        return (T)services.get(type.hashCode());
    }

    @Override
    public <T extends XManager> void attachManager(Class<?> type, XManager manager) {
        int key = type.hashCode();
        if(!managers.containsKey(key)) {
            managers.put(type.hashCode(), manager);
            initManagers.add(manager);
        }
    }

    @Override
    public boolean detachManager(Class<?> type) {
        XManager manager = managers.remove(type.hashCode());
        if(manager != null) {
            manager.onDetach(this);
            return true;
        }
        return false;
    }

    @Override
    public <T> T getManager(Class<T> type) {
        return (T)managers.get(type.hashCode());
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

        if(initServices.size > 0) {
            for(int i = 0; i < initServices.size; i++) {
                initServices.get(i).onAttach(this);
            }
            initServices.clear();
        }
        if(initManagers.size > 0) {
            for(int i = 0; i < initManagers.size; i++) {
                initManagers.get(i).onAttach(this);
            }
            initManagers.clear();
        }

        for(XService service: services.getList()) {
            service.onTick(this);
        }
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