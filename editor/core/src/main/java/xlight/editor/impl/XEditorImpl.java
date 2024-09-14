package xlight.editor.impl;

import xlight.editor.core.XEditor;
import xlight.editor.core.ecs.manager.XEditorManager;
import xlight.engine.core.XEngine;

public class XEditorImpl implements XEditor {

    private XEngine editorEngine;

    public XEditorImpl() {
        editorEngine = XEngine.newInstance();

        XEditorManagerImpl editorManager = new XEditorManagerImpl();
        editorEngine.getWorld().attachManager(XEditorManager.class, editorManager);
    }

    @Override
    public XEngine getEditorEngine() {
        return editorEngine;
    }

    @Override
    public void onSetup(XEngine engine) {

    }
}