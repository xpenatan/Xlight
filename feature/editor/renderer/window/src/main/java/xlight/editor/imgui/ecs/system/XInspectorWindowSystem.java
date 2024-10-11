package xlight.editor.imgui.ecs.system;

import imgui.ImGui;
import imgui.ImGuiWindowClass;
import xlight.editor.core.ecs.manager.XEditorManager;
import xlight.editor.core.ecs.manager.XEntitySelectionManager;
import xlight.editor.imgui.ecs.manager.XImGuiManager;
import xlight.editor.imgui.ecs.system.inspector.XEntityInspector;
import xlight.editor.imgui.window.XImGuiWindowContext;
import xlight.editor.imgui.window.XMainWindow;
import xlight.engine.core.XEngine;
import xlight.engine.core.editor.ui.XUIData;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.system.XSystem;
import xlight.engine.ecs.system.XSystemData;
import xlight.engine.ecs.system.XSystemType;
import xlight.engine.list.XList;
import xlight.engine.pool.XPoolController;

public class XInspectorWindowSystem implements XSystem {

    public final static String name = "Inspector";

    private ImGuiWindowClass windowClass;

    private XEntityInspector entityInspector;
    private XEntitySelectionManager entitySelectionManager;
    private XEditorManager editorManager;

    private XUIData uiData;

    @Override
    public void onAttach(XWorld world, XSystemData systemData) {
        XImGuiManager imguiManager = world.getManager(XImGuiManager.class);
        XImGuiWindowContext windowContext = imguiManager.getWindowContext(XMainWindow.CLASS_ID);
        windowClass = windowContext.getWindowClass();
        entitySelectionManager = world.getManager(XEntitySelectionManager.class);
        editorManager = world.getManager(XEditorManager.class);

        XPoolController poolController = world.getGlobalData(XPoolController.class);
        entityInspector = new XEntityInspector(imguiManager, poolController);

        if(uiData == null) {
            uiData = world.getGlobalData(XUIData.class);
        }
    }

    @Override
    public void onTick(XWorld editorWorld) {
        ImGui.SetNextWindowClass(windowClass);

        ImGui.Begin(name);

        XEngine gameEngine = editorManager.getGameEngine();
        if(gameEngine != null) {
            XWorld engineWorld = gameEngine.getWorld();
            XEntity currentEntity = entitySelectionManager.getCurrentSelectedTarget();
            if(currentEntity != null) {
                entityInspector.renderEntity(editorWorld, engineWorld, currentEntity, uiData);
                XEntity secondEntity = entitySelectionManager.getSelectedTargetIndex(1);
                if(secondEntity != null) {
                    entityInspector.renderEntity(editorWorld, engineWorld, secondEntity, uiData);
                }
            }
        }
        ImGui.End();
    }

    @Override
    public XSystemType getType() {
        return XSystemType.UI;
    }
}
