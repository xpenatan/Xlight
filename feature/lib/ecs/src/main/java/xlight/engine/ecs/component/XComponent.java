package xlight.engine.ecs.component;

import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.entity.XEntity;

public interface XComponent {
    default void onAttach(XWorld world, XEntity entity) {}
    default void onDetach(XWorld world, XEntity entity) {}

    default Class<?> getComponentType() { return getClass(); }
}