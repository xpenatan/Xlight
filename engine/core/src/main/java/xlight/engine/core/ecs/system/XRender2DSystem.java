package xlight.engine.core.ecs.system;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import xlight.engine.ecs.component.XComponentMatcher;
import xlight.engine.ecs.component.XComponentMatcherBuilder;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.system.XEntitySystem;

public class XRender2DSystem extends XEntitySystem {

    private SpriteBatch batch;

    public XRender2DSystem() {
        batch = new SpriteBatch();
    }

    @Override
    public XComponentMatcher getMatcher(XComponentMatcherBuilder builder) {
        return null;
    }

    @Override
    public void onEntityTick(XEntity e) {

    }
}