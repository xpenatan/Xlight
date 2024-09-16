package xlight.engine.ecs.component;

import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.entity.XEntity;

public interface XComponent {
    /**
     * Called when a component is attached to an entity.
     * Will be called if entity is attached or detached
     */
    default void onAttach(XWorld world, XEntity entity) {}

    /**
     * Called when a component is detached from an entity.
     * Will be called if entity is attached or detached
     */
    default void onDetach(XWorld world, XEntity entity) {}

    default Class<?> getComponentType() { return getClass(); }
}