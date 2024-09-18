package xlight.editor.window.gameeditor.ecs.system;

import xlight.engine.ecs.system.XEntitySystem;

public abstract class XGameEditorEntitySystem extends XEntitySystem {

    @Override
    public final int getSystemController() {
        return XGameEditorSystem.SYSTEM_CONTROLLER;
    }
}