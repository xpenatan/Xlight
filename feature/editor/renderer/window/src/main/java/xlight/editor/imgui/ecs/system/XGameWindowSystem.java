package xlight.editor.imgui.ecs.system;

import imgui.ImGui;
import imgui.ImGuiWindowClass;
import xlight.editor.imgui.ecs.manager.XImGuiManager;
import xlight.editor.imgui.window.XImGuiWindowContext;
import xlight.editor.imgui.window.XMainWindow;
import xlight.engine.ecs.XECSWorld;
import xlight.engine.ecs.system.XSystem;
import xlight.engine.ecs.system.XSystemType;

public class XGameWindowSystem implements XSystem {

    public final static String name = "Game Editor";

    private ImGuiWindowClass windowClass;

    @Override
    public void onAttach(XECSWorld world) {
        XImGuiManager imguiManager = world.getManager(XImGuiManager.class);
        XImGuiWindowContext windowContext = imguiManager.getWindowContext(XMainWindow.CLASS_ID);
        windowClass = windowContext.getWindowClass();
    }

    @Override
    public void onTick(XECSWorld world) {
        ImGui.SetNextWindowClass(windowClass);

        ImGui.Begin(name);

        ImGui.End();
    }

    @Override
    public XSystemType getType() {
        return XSystemType.UI;
    }
}
