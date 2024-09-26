package xlight.engine.ecs.component;

import xlight.engine.ecs.entity.XEntity;

public interface XComponentService {
    <T extends XComponent> boolean registerComponent(Class<T> type);
    XComponentType getComponentType(Class<?> type);
    <T extends XComponent> T getComponent(XEntity entity, Class<T> type);
    boolean containsComponent(XEntity entity, Class<?> type);
    <T extends XComponent> T getComponentIndex(XEntity entity, int index);
    boolean attachComponent(XEntity entity, XComponent component);
    boolean detachComponent(XEntity entity, Class<?> type);
    boolean detachComponent(XEntity entity, XComponent component);
    XComponentMatcherBuilder getMatcherBuilder();
}