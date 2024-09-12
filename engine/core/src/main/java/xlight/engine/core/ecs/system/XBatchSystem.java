package xlight.engine.core.ecs.system;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import xlight.engine.ecs.component.XComponentMatcher;
import xlight.engine.ecs.component.XComponentMatcherBuilder;
import xlight.engine.ecs.component.XComponentService;
import xlight.engine.ecs.entity.XEntity;

public class XBatchSystem extends XEntitySystem {

    private SpriteBatch batch;

    public XBatchSystem() {
        batch = new SpriteBatch();
    }

    @Override
    public XComponentMatcher getMatcher(XComponentMatcherBuilder builder) {
        return null;
    }

    @Override
    public void onEntityTick(XComponentService cs, XEntity e) {

    }
}