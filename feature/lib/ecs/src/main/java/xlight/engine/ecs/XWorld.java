package xlight.engine.ecs;

import xlight.engine.ecs.component.XComponentService;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.ecs.event.XEventService;
import xlight.engine.ecs.manager.XManager;
import xlight.engine.ecs.manager.XManagerData;
import xlight.engine.ecs.service.XService;
import xlight.engine.ecs.system.XSystemService;
import xlight.engine.impl.XECSWorldImpl;
import xlight.engine.list.XList;

public interface XWorld {
    <T extends XService> void attachService(Class<?> type, XService service);
    boolean detachService(Class<?> type);
    <T> T getService(Class<T> type);
    // TODO check if type hash will be the same with proguard
    <T extends XManager> void attachManager(Class<?> type, XManager manager);
    boolean detachManager(Class<?> type);
    <T> T getManager(Class<T> type);
    <T> T getManager(int key);
    XList<XManagerData> getManagers();

    XWorldService getWorldService();

    float getDeltaTime();
    void update(float deltaTime);
    void tickUpdate();
    void tickUI();
    void tickRender();

    void registerGlobalData(Class<?> type, Object data);
    boolean removeGlobalData(Class<?> type);
    boolean containsGlobalData(Class<?> type);
    <T> T getGlobalData(Class<T> type);

    static XWorld newInstance() { return new XECSWorldImpl(); }
}