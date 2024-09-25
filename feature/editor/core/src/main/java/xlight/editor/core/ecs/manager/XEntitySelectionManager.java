package xlight.editor.core.ecs.manager;

import com.badlogic.gdx.math.Vector3;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.list.XList;
import xlight.engine.transform.XGizmoType;

public interface XEntitySelectionManager {
    void selectTarget(XEntity target, boolean multiSelect);
    void unselectAllTargets();
    XEntity getCurrentSelectedTarget();
    XList<XEntity> getSelectedTargets();
    void unselectTarget(XEntity target);

    void moveAndStartDragging(XGizmoType transformType, int transformMode, Vector3 position, Vector3 rotation);
    void removeDragging();

    boolean isSelected(XEntity target);
}