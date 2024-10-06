package xlight.engine.ecs.entity;

import com.badlogic.gdx.utils.Bits;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.list.XIntSet;
import xlight.engine.list.XIntSetNode;
import xlight.engine.list.XList;

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
    boolean attachComponent(XComponent component);

    /**
     * Quick way to detach component. Same as using ComponentService.
     */
    boolean detachComponent(Class<?> type);

    /**
     * Quick way to detach component. Same as using ComponentService.
     */
    boolean detachComponent(XComponent component);
    boolean containsComponent(Class<?> type);

    int getComponentsSize();
    XComponent getComponentAt(int index);

    void setName(String name);
    String getName();

    boolean isSavable();
    void setSavable(boolean flag);

    XEntity getParent();
    boolean setParent(XEntity parent);

    /**
     * Return child entity. null if not found
     */
    XEntity getChild(int id);

    /**
     * Return children list
     */
    XList<XIntSetNode> getChildList();

    XIntSetNode getChildHead();

    boolean putChild(XEntity entity);

    XEntity removeChild(int id);

    void clearChildren();
}