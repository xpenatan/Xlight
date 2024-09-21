package xlight.editor.impl;

import xlight.editor.core.ecs.manager.XEntitySelectionManager;
import xlight.editor.core.selection.XObjectSelection;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.manager.XManager;
import xlight.engine.list.XDataArray;
import xlight.engine.pool.XPool;

class XEntitySelectionManagerImpl extends XObjectSelection<XEntity, XEntitySelectionManagerImpl.XEntitySelectionNode> implements XEntitySelectionManager, XManager {

    public XEntitySelectionManagerImpl() {
        super(new XPool<>() {
            @Override
            protected XEntitySelectionNode newObject() {
                return new XEntitySelectionNode();
            }
        });
    }

    public static class XEntitySelectionNode extends XDataArray.XDataArrayNode<XEntity> {
    }
}