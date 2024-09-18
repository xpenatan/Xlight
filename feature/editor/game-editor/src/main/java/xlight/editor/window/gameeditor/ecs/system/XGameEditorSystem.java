package xlight.editor.window.gameeditor.ecs.system;

import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.system.XSystem;

public abstract class XGameEditorSystem implements XSystem {

    public static final String PREFERENCE_SECTION = "GAME_EDITOR";

    public static final int SYSTEM_CONTROLLER = -9381368;

    @Override
    public final void onAttach(XWorld world) {
        onSystemAttach(world);
    }

    @Override
    public final void onDetach(XWorld world) {
        onSystemDetach(world);
    }

    protected void onSystemAttach(XWorld world) {}
    protected void onSystemDetach(XWorld world) {}

    @Override
    public final int getSystemController() {
        return SYSTEM_CONTROLLER;
    }
}