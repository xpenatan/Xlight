package xlight.editor.imgui.ecs.manager;

import com.badlogic.gdx.Gdx;
import imgui.ImGui;
import imgui.ImGuiDir;
import imgui.ImGuiInternal;
import imgui.ImGuiViewport;
import imgui.idl.helper.IDLInt;
import xlight.editor.core.ecs.event.XEditorEvents;
import xlight.editor.imgui.ecs.system.XContentBrowserWindowSystem;
import xlight.editor.imgui.ecs.system.game.XGameEditorAppListener;
import xlight.editor.imgui.ecs.system.game.XGameWindowSystem;
import xlight.editor.imgui.ecs.system.XHierarchyWindowSystem;
import xlight.editor.imgui.ecs.system.XInspectorWindowSystem;
import xlight.editor.imgui.ecs.system.XUIWindowSystem;
import xlight.editor.imgui.window.XImGuiWindowContext;
import xlight.editor.imgui.window.XMainWindow;
import xlight.engine.ecs.XECSWorld;
import xlight.engine.ecs.manager.XManager;
import xlight.engine.ecs.system.XSystemService;
import xlight.engine.init.ecs.service.XInitFeatureService;

public class XImGuiWindowsManager implements XManager {
    @Override
    public void onAttach(XECSWorld world) {
        XInitFeatureService featureService = world.getService(XInitFeatureService.class);
        featureService.addFeatureDependency(() -> Gdx.app.postRunnable(() -> initSystems(featureService, world)), XImGuiManager.FEATURE);
    }

    private void initSystems(XInitFeatureService featureService, XECSWorld world) {
        XSystemService systemService = world.getSystemService();
        systemService.attachSystem(new XHierarchyWindowSystem());
        systemService.attachSystem(new XGameWindowSystem());
        systemService.attachSystem(new XUIWindowSystem());
        systemService.attachSystem(new XInspectorWindowSystem());
        systemService.attachSystem(new XContentBrowserWindowSystem());

        XImGuiWindowContext windowContext = world.getManager(XImGuiManager.class).getWindowContext(XMainWindow.CLASS_ID);

        int dockSpaceId = windowContext.getDockSpaceId();
        layout(dockSpaceId);

        featureService.addFeatureDependency(() -> {
            // When all imgui windows are ready we send a editor ready event
            world.getEventService().sendEvent(XEditorEvents.EVENT_EDITOR_READY);
        }, XGameEditorAppListener.FEATURE);
    }

    private void layout(int dockSpaceId) {
        if(dockSpaceId == -1) {
            System.out.println("Warning: Layout failed with dockspace id -1");
            return;
        }
        ImGuiViewport imGuiViewport = ImGui.GetMainViewport();
        ImGuiInternal.DockBuilderRemoveNode(dockSpaceId); // clear any previous layout
        ImGuiInternal.DockBuilderAddNode(dockSpaceId);
        ImGuiInternal.DockBuilderSetNodeSize(dockSpaceId, imGuiViewport.get_Size());

        IDLInt.TMP_1.set(dockSpaceId);

        // Split Right/Left
        int rightId = ImGuiInternal.DockBuilderSplitNode(IDLInt.TMP_1.getValue(), ImGuiDir.ImGuiDir_Right, 0.3f, null, IDLInt.TMP_1);

        // Split Down with left id
        int assetId = ImGuiInternal.DockBuilderSplitNode(IDLInt.TMP_1.getValue(), ImGuiDir.ImGuiDir_Down, 0.3f, null, IDLInt.TMP_1);
        int centralNodeId = IDLInt.TMP_1.getValue(); // Top left is central node

        ImGuiInternal.DockBuilderSplitNode(centralNodeId, ImGuiDir.ImGuiDir_Left, 0.47f, IDLInt.TMP_1, IDLInt.TMP_2);
        int topLeft = IDLInt.TMP_1.getValue();
        int topRight = IDLInt.TMP_2.getValue();

        IDLInt.TMP_1.set(rightId);
        int rightTopId = ImGuiInternal.DockBuilderSplitNode(IDLInt.TMP_1.getValue(), ImGuiDir.ImGuiDir_Up, 0.5f, null, IDLInt.TMP_1);
        int rightBottomId = IDLInt.TMP_1.getValue();

        ImGuiInternal.DockBuilderDockWindow(XUIWindowSystem.name, topLeft);
        ImGuiInternal.DockBuilderDockWindow(XGameWindowSystem.name, topLeft);
        ImGuiInternal.DockBuilderDockWindow(XHierarchyWindowSystem.name, rightTopId);
        ImGuiInternal.DockBuilderDockWindow(XInspectorWindowSystem.name, rightBottomId);
        ImGuiInternal.DockBuilderDockWindow(XContentBrowserWindowSystem.name, assetId);
        ImGuiInternal.DockBuilderFinish(dockSpaceId);

        // Game and UI Editor are at the same tab stacking. Select Game editor
        int i = ImGuiInternal.ImHashStr(XGameWindowSystem.name);
        ImGuiInternal.DockBuilderGetNode(topLeft).set_SelectedTabId(ImGuiInternal.ImHashStr("#TAB", 0, i));
    }
}