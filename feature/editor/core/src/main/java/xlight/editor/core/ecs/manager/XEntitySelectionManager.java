package xlight.editor.core.ecs.manager;

import com.badlogic.gdx.math.Vector3;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.list.XList;
import xlight.engine.transform.XTransformType;

public interface XEntitySelectionManager {
    void selectTarget(XEntity target, boolean multiSelect);
    void unselectAllTargets();
    XEntity getCurrentSelectedTarget();
    XList<XEntity> getSelectedTargets();
    void unselectTarget(XEntity target);
    boolean isSelected(XEntity target);
    boolean changeCurrentSelectedTarget(XEntity target);

    void moveAndStartDragging(XTransformType transformType, int transformMode, Vector3 position, Vector3 rotation);
    void removeDragging();

}