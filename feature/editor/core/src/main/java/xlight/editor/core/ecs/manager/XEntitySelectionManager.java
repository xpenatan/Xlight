package xlight.editor.core.ecs.manager;

import xlight.engine.ecs.entity.XEntity;
import xlight.engine.list.XList;

public interface XEntitySelectionManager {
    void selectTarget(XEntity target, boolean multiSelect);
    void unselectAllTargets();
    XEntity getCurrentSelectedTarget();
    XList<XEntity> getSelectedTargets();
}