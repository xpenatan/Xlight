package xlight.editor.imgui.ecs.system;

import imgui.ImGui;
import imgui.ImGuiTabBarFlags;
import imgui.ImGuiWindowClass;
import xlight.editor.core.ecs.manager.XEditorManager;
import xlight.editor.imgui.ecs.manager.XImGuiManager;
import xlight.editor.imgui.ecs.system.hierarchy.XEntityHierarchyRenderer;
import xlight.editor.imgui.ecs.system.scene.XSceneWindowRenderer;
import xlight.editor.imgui.window.XImGuiWindowContext;
import xlight.editor.imgui.window.XMainWindow;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.system.XSystem;
import xlight.engine.ecs.system.XSystemData;
import xlight.engine.ecs.system.XSystemType;

public class XHierarchyWindowSystem implements XSystem {

    public final static String name = "Hierarchy";

    private final static String TAB_ID = "HIERARCHY_TAB_ID";

    private ImGuiWindowClass windowClass;

    private XEntityHierarchyRenderer entityHierarchyRenderer;
    private XSceneWindowRenderer sceneRenderer;

    public XHierarchyWindowSystem() {
        entityHierarchyRenderer = new XEntityHierarchyRenderer();
        sceneRenderer = new XSceneWindowRenderer();
    }

    @Override
    public void onAttach(XWorld world, XSystemData systemData) {
        XImGuiManager imguiManager = world.getManager(XImGuiManager.class);
        XImGuiWindowContext windowContext = imguiManager.getWindowContext(XMainWindow.CLASS_ID);
        windowClass = windowContext.getWindowClass();

        entityHierarchyRenderer.onAttach(world);
        sceneRenderer.onAttach(world);
    }

    @Override
    public void onDetach(XWorld world, XSystemData systemData) {
        entityHierarchyRenderer.onDetach(world);
        sceneRenderer.onDetach(world);
    }

    @Override
    public void onTick(XWorld world) {
        ImGui.SetNextWindowClass(windowClass);
        ImGui.Begin(name);
        if(ImGui.BeginTabBar(TAB_ID, ImGuiTabBarFlags.ImGuiTabBarFlags_Reorderable | ImGuiTabBarFlags.ImGuiTabBarFlags_NoTooltip)) {
            XEditorManager editorManager = world.getManager(XEditorManager.class);
            entityHierarchyRenderer.render(world, editorManager);
            sceneRenderer.render(editorManager);
            ImGui.EndTabBar();
        }
        ImGui.End();
    }

    @Override
    public XSystemType getType() {
        return XSystemType.UI;
    }
}
