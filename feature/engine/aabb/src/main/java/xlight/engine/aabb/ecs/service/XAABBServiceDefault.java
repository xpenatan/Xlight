package xlight.engine.aabb.ecs.service;

import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntSet;
import xlight.engine.aabb.XAABBTree;
import xlight.engine.aabb.XAABBTreeDefault;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.component.XComponentMatcher;
import xlight.engine.ecs.component.XComponentMatcherBuilder;
import xlight.engine.ecs.component.XGameComponent;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.service.XService;
import xlight.engine.transform.XTransform;
import xlight.engine.transform.ecs.component.XTransformComponent;

public class XAABBServiceDefault implements XAABBService, XService {

    XAABBTree gameTree;

    public XAABBServiceDefault() {
        gameTree = new XAABBTreeDefault();
    }

    @Override
    public void onAttach(XWorld world) {
        XComponentMatcherBuilder matcherBuilder = world.getComponentService().getMatcherBuilder();
        XComponentMatcher matcher = matcherBuilder.all(XGameComponent.class, XTransformComponent.class).build(-100, new XComponentMatcher.XComponentMatcherListener() {
            @Override
            public void onEntityUpdate(XComponentMatcher.XComponentMatcherState state, XEntity entity) {
                int id = entity.getId();
                XTransformComponent transformComponent = entity.getComponent(XTransformComponent.class);
                XTransform transform = transformComponent.transform;
                if(state == XComponentMatcher.XComponentMatcherState.ADD) {
                    gameTree.addAABB(id, transform);
                }
                else if(state == XComponentMatcher.XComponentMatcherState.REMOVE) {
                    gameTree.removeAABB(id);
                }
            }

            @Override
            public boolean contains(XEntity entity) {
                return gameTree.containsAABB(entity.getId());
            }
        });
    }

    @Override
    public void onTick(XWorld world) {
        gameTree.update();
    }

    @Override
    public XAABBTree getGameTree() {
        return gameTree;
    }
}