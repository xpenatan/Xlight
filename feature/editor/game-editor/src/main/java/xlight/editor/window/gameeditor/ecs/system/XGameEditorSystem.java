package xlight.editor.window.gameeditor.ecs.system;

import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.system.XSystem;
import xlight.engine.ecs.system.XSystemData;

public abstract class XGameEditorSystem implements XSystem {

    public static final String PREFERENCE_SECTION = "GAME_EDITOR";

    public static final int SYSTEM_CONTROLLER = -9381368;

    @Override
    public final void onAttach(XWorld world, XSystemData systemData) {
        onSystemAttach(world, systemData);
    }

    @Override
    public final void onDetach(XWorld world, XSystemData systemData) {
        onSystemDetach(world, systemData);
    }

    protected void onSystemAttach(XWorld world, XSystemData systemData) {}
    protected void onSystemDetach(XWorld world, XSystemData systemData) {}

    @Override
    public final int getSystemController() {
        return SYSTEM_CONTROLLER;
    }
}