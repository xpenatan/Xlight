package xlight.engine.ecs.component;

import xlight.engine.ecs.entity.XEntity;

public interface XComponentService {
    <T extends XComponent> boolean registerComponent(Class<T> type);
    <T extends XComponent> XComponentType getComponentType(Class<T> type);
    <T extends XComponent> T getComponent(XEntity entity, Class<T> type);
    <T extends XComponent> void attachComponent(XEntity entity, XComponent component);
    <T extends XComponent> void detachComponent(XEntity entity, Class<T> type);
    XComponentMatcherBuilder getMatcherBuilder();
}