package xlight.editor.imgui.window;

import imgui.ImGui;
import imgui.ImGuiCond;
import imgui.ImGuiWindowClass;
import imgui.ImGuiWindowFlags;
import imgui.ImVec2;
import xlight.engine.ecs.XECSWorld;

public abstract class XImGuiWindowContext {
    protected ImGuiWindowClass windowClass;
    private String dockWindowName;
    protected int dockspaceId;
    public boolean isVisible;

    public XImGuiWindowContext(String dockWindowName) {
        this.dockWindowName = dockWindowName;
    }

    protected abstract void onAdd(XECSWorld editorEngine, int windowClassID);
    protected abstract void onRemove(XECSWorld editorEngine);
    protected abstract void onRender(XECSWorld editorEngine, int rootDockspaceID);

    public final void dispose(XECSWorld editorEngine) {
        onRemove(editorEngine);
        windowClass.dispose();
        windowClass = null;
    }

    public final void render(XECSWorld editorEngine, int rootDockspaceID, int windowClassID) {
        if(windowClass == null) {
            windowClass = new ImGuiWindowClass();
            windowClass.set_ClassId(windowClassID);
            windowClass.set_DockingAllowUnclassed(false);
            onAdd(editorEngine, windowClassID);
        }
        if(isVisible) {
            renderDockWindow(editorEngine, rootDockspaceID);
            onRender(editorEngine, rootDockspaceID);
        }
    }

    private void renderDockWindow(XECSWorld editorEngine, int rootDockspaceID) {
        int windowFlags = ImGuiWindowFlags.ImGuiWindowFlags_MenuBar;
        ImGui.SetNextWindowDockID(rootDockspaceID, ImGuiCond.ImGuiCond_FirstUseEver);

        int dockNodeFlags = 0;
        ImGui.Begin(dockWindowName, null, windowFlags);
        dockspaceId = ImGui.GetID("ChildDockSpaceId");
        OnRenderDockspace(editorEngine);
        ImVec2 dockspace_size = ImGui.GetContentRegionAvail();
        ImGui.DockSpace(dockspaceId, dockspace_size, dockNodeFlags, windowClass);
        ImGui.End();
    }

    public ImGuiWindowClass getWindowClass() {
        return windowClass;
    }

    protected void OnRenderDockspace(XECSWorld editorEngine) {}
}