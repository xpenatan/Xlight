package xlight.editor.core.ecs.manager;

import com.badlogic.gdx.math.Vector3;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.gizmo.XGizmoRenderer;
import xlight.engine.list.XList;

public interface XEntitySelectionManager {
    void selectTarget(XEntity target, boolean multiSelect);
    void unselectAllTargets();
    XEntity getCurrentSelectedTarget();
    XList<XEntity> getSelectedTargets();

    void moveAndStartDragging(XGizmoRenderer.TRANSFORM_TYPE transformType, int transformMode, Vector3 position, Vector3 rotation);
    void removeDragging();

    boolean isSelected(XEntity target);
}