package xlight.engine.ecs.entity;

import com.badlogic.gdx.utils.Bits;
import xlight.engine.ecs.component.XComponent;

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

    /**
     * Quick way to get component. Same as using ComponentService.
     */
    <T extends XComponent> T getComponent(Class<T> type);
    /**
     * Quick way to attach component. Same as using ComponentService.
     */
    <T extends XComponent> void attachComponent(XComponent component);
    /**
     * Quick way to detach component. Same as using ComponentService.
     */
    <T extends XComponent> void detachComponent(Class<T> type);

    void setName(String name);
    String getName();
}