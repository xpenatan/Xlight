package xlight.engine.ecs;

import xlight.engine.ecs.component.XComponentService;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.ecs.event.XEventService;
import xlight.engine.ecs.service.XService;
import xlight.engine.ecs.system.XSystemService;
import xlight.engine.impl.XECSWorldImpl;

public interface XECSWorld {
    <T extends XService> void attachService(Class<T> type, T service);
    <T extends XService> boolean detachService(Class<T> type);
    <T extends XService> T getService(Class<T> type);

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