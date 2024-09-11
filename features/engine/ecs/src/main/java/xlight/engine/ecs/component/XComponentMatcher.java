package xlight.engine.ecs.component;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.IntArray;
import xlight.engine.ecs.entity.XEntity;

public interface XComponentMatcher {
    String getID();
    IntArray getEntities();
    /** Check if matcher matches bit mask */
    boolean matches(Bits mask);
    boolean matches(XEntity entity);
    boolean contains(XEntity entity);

    interface XComponentMatcherListener {
        void onEntityUpdate(XComponentMatcherState state, XEntity entity);
        boolean contains(XEntity entity);
    }

    enum XComponentMatcherState {
        ADD, REMOVE
    }
}