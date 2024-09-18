package xlight.editor.window.gameeditor.ecs.system;

import xlight.engine.ecs.system.XSystem;

public abstract class XGameEditorSystem implements XSystem {
    public static final int SYSTEM_CONTROLLER = -9381368;

    @Override
    public final int getSystemController() {
        return SYSTEM_CONTROLLER;
    }
}