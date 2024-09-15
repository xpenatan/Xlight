package xlight.editor.imgui.window;

import imgui.ImGui;
import imgui.ImGuiCond;
import imgui.ImGuiWindowClass;
import imgui.ImGuiWindowFlags;
import imgui.ImVec2;
import xlight.engine.ecs.XWorld;

public abstract class XImGuiWindowContext {
    protected ImGuiWindowClass windowClass;
    private String dockWindowName;
    protected int dockspaceId = -1;
    public boolean isVisible;

    public XImGuiWindowContext(String dockWindowName) {
        this.dockWindowName = dockWindowName;
    }

    protected abstract void onAdd(XWorld world, int windowClassID);
    protected abstract void onRemove(XWorld world);
    protected abstract void onRender(XWorld world, int rootDockspaceID);

    public final void dispose(XWorld editorEngine) {
        onRemove(editorEngine);
        windowClass.dispose();
        windowClass = null;
    }

    public final void render(XWorld world, int rootDockspaceID, int windowClassID) {
        if(windowClass == null) {
            windowClass = new ImGuiWindowClass();
            windowClass.set_ClassId(windowClassID);
            windowClass.set_DockingAllowUnclassed(false);
            onAdd(world, windowClassID);
        }
        if(isVisible) {
            renderDockWindow(world, rootDockspaceID);
            onRender(world, rootDockspaceID);
        }
    }

    private void renderDockWindow(XWorld world, int rootDockspaceID) {
        int windowFlags = ImGuiWindowFlags.ImGuiWindowFlags_MenuBar;
        ImGui.SetNextWindowDockID(rootDockspaceID, ImGuiCond.ImGuiCond_FirstUseEver);

        int dockNodeFlags = 0;
        ImGui.Begin(dockWindowName, null, windowFlags);
        dockspaceId = ImGui.GetID("ChildDockSpaceId");
        OnRenderDockspace(world);
        ImVec2 dockspace_size = ImGui.GetContentRegionAvail();
        ImGui.DockSpace(dockspaceId, dockspace_size, dockNodeFlags, windowClass);
        ImGui.End();
    }

    public ImGuiWindowClass getWindowClass() {
        return windowClass;
    }

    public int getDockSpaceId() {
        return dockspaceId;
    }

    protected void OnRenderDockspace(XWorld world) {}
}