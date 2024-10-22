package xlight.engine.impl;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.XWorldService;
import xlight.engine.ecs.component.XComponentService;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.ecs.event.XEventService;
import xlight.engine.ecs.manager.XManager;
import xlight.engine.ecs.manager.XManagerData;
import xlight.engine.ecs.service.XService;
import xlight.engine.ecs.system.XSystemService;
import xlight.engine.list.XIntMap;
import xlight.engine.list.XList;
import xlight.engine.pool.XPoolController;

public class XECSWorldImpl implements XWorld {

    private float deltaTime;
    private final XIntMap<XService> services;
    private final XIntMap<XManagerData> managers;

    private final Array<XService> initServices;
    private final Array<XManager> initManagers;

    private XWorldServiceImpl worldService;

    private final IntMap<Object> globalData;

    public XECSWorldImpl() {
        globalData = new IntMap<>();
        services = new XIntMap<>();
        managers = new XIntMap<>();
        initServices = new Array<>();
        initManagers = new Array<>();

        XPoolControllerImpl poolController = new XPoolControllerImpl();
        registerGlobalData(XPoolController.class, poolController);

        worldService = new XWorldServiceImpl(this);
        worldService.init();
    }

    @Override
    public <T extends XService> void attachService(Class<?> type, XService service) {
        int key = type.hashCode();
        if(!services.containsKey(key)) {
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
            managers.put(type.hashCode(), new XManagerDataImpl(key, manager));
            initManagers.add(manager);
        }
    }

    @Override
    public boolean detachManager(Class<?> type) {
        XManagerData managerData = managers.remove(type.hashCode());
        if(managerData != null) {
            managerData.getManager().onDetach(this);
            return true;
        }
        return false;
    }

    @Override
    public <T> T getManager(Class<T> type) {
        return getManager(type.hashCode());
    }

    @Override
    public <T> T getManager(int key) {
        XManagerData managerData = managers.get(key);
        if(managerData != null) {
            return (T)managerData.getManager();
        }
        return null;
    }

    @Override
    public XList<XManagerData> getManagers() {
        return managers.getList();
    }

    @Override
    public XWorldService getWorldService() {
        return worldService;
    }

    @Override
    public float getDeltaTime() {
        return deltaTime;
    }

    @Override
    public void update(float deltaTime) {
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
        worldService.eventService.update();
    }

    @Override
    public void tickUpdate() {
        worldService.systemService.tickStepSystem();
        worldService.systemService.tickUpdateSystem();
    }

    @Override
    public void tickUI() {
        worldService.systemService.tickUISystem();
    }

    @Override
    public void tickRender() {
        worldService.systemService.tickRenderSystem();
    }

    @Override
    public void registerGlobalData(Class<?> type, Object data) {
        globalData.put(type.hashCode(), data);
    }

    @Override
    public boolean removeGlobalData(Class<?> type) {
        return globalData.remove(type.hashCode()) != null;
    }

    @Override
    public boolean containsGlobalData(Class<?> type) {
        return globalData.containsKey(type.hashCode());
    }

    @Override
    public <T> T getGlobalData(Class<T> type) {
        return (T)globalData.get(type.hashCode());
    }
}