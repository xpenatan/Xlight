package xlight.engine.ecs;

import xlight.engine.ecs.component.XComponentService;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.ecs.event.XEventService;
import xlight.engine.ecs.manager.XManager;
import xlight.engine.ecs.service.XService;
import xlight.engine.ecs.system.XSystemService;
import xlight.engine.impl.XECSWorldImpl;

public interface XECSWorld {
    <T extends XService> void attachService(Class<?> type, XService service);
    boolean detachService(Class<?> type);
    <T> T getService(Class<T> type);
    <T extends XManager> void attachManager(Class<?> type, XManager manager);
    boolean detachManager(Class<?> type);
    <T> T getManager(Class<T> type);

    XSystemService getSystemService();
    XEntityService getEntityService();
    XComponentService getComponentService();
    XEventService getEventService();

    float getDeltaTime();
    void tickUpdate(float deltaTime);
    void tickUI();
    void tickRender();

    static XECSWorld newInstance() { return new XECSWorldImpl(); }
}