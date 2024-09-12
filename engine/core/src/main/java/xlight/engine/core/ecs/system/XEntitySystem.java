package xlight.engine.core.ecs.system;

import com.badlogic.gdx.utils.IntArray;
import xlight.engine.ecs.XECSWorld;
import xlight.engine.ecs.component.XComponentMatcher;
import xlight.engine.ecs.component.XComponentMatcherBuilder;
import xlight.engine.ecs.component.XComponentService;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.ecs.system.XSystem;

public abstract class XEntitySystem extends XSystem {

    private XEntityService entityService;
    private XComponentService componentService;
    protected IntArray entities;

    @Override
    public final void onAttach(XECSWorld world) {
        entityService = world.getEntityService();
        componentService = world.getComponentService();
        XComponentMatcherBuilder matcherBuilder = componentService.getMatcherBuilder();
        XComponentMatcher matcher = getMatcher(matcherBuilder);
        entities = matcher.getEntities();
        onAttachSystem(world);
    }

    @Override
    public final void onDetach(XECSWorld world) {
        onDetachSystem(world);
    }

    @Override
    public final void onTick() {
        if(entities == null) {
            return;
        }
        onBeginTick();
        for(int i = 0; i < entities.size; i++) {
            int entityId = entities.get(i);
            XEntity entity = entityService.getEntity(entityId);
            onEntityTick(componentService, entity);
        }
        onEndTick();
    }

    protected void onBeginTick() {}
    protected void onEndTick() {}

    public abstract XComponentMatcher getMatcher(XComponentMatcherBuilder builder);
    public void onAttachSystem(XECSWorld world) {}
    public void onDetachSystem(XECSWorld world) {}
    public abstract void onEntityTick(XComponentService cs, XEntity e);
}