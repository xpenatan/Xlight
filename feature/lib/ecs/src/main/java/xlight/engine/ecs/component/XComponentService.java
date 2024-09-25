package xlight.engine.ecs.component;

import xlight.engine.ecs.entity.XEntity;

public interface XComponentService {
    <T extends XComponent> boolean registerComponent(Class<T> type);
    <T extends XComponent> XComponentType getComponentType(Class<T> type);
    <T extends XComponent> T getComponent(XEntity entity, Class<T> type);
    <T extends XComponent> T getComponentIndex(XEntity entity, int index);
    boolean attachComponent(XEntity entity, XComponent component);
    <T extends XComponent> boolean detachComponent(XEntity entity, Class<T> type);
    boolean detachComponent(XEntity entity, XComponent component);
    XComponentMatcherBuilder getMatcherBuilder();
}