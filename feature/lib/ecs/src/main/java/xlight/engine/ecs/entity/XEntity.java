package xlight.engine.ecs.entity;

import com.badlogic.gdx.utils.Bits;

public interface XEntity {
    int getId();
    XEntityState getState();
    /**
     * Return read-only component mask
     */
    Bits getComponentMask();

    boolean isAttached();
    boolean isDetached();

    boolean isVisible();
    void setVisible(boolean flag);
}