package xlight.engine.ecs.component;

import xlight.engine.ecs.XECSWorld;
import xlight.engine.ecs.entity.XEntity;

public interface XComponent {
    default void onAttach(XECSWorld world, XEntity entity) {}
    default void onDetach(XECSWorld world, XEntity entity) {}
}