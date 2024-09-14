package xlight.editor.impl;

import xlight.editor.core.ecs.manager.XEditorManager;
import xlight.engine.core.XEngine;
import xlight.engine.ecs.XECSWorld;
import xlight.engine.ecs.manager.XManager;

class XEditorManagerImpl implements XEditorManager, XManager {
    @Override
    public void onAttach(XECSWorld world) {

    }

    @Override
    public void onDetach(XECSWorld world) {

    }

    @Override
    public XEngine getGameEngine() {
        return null;
    }
}