package xlight.editor.imgui.ecs.system.scene;

import imgui.ImGui;
import xlight.editor.core.ecs.manager.XEditorManager;
import xlight.engine.core.XEngine;
import xlight.engine.core.editor.ui.XUIData;
import xlight.engine.core.editor.ui.options.XUIOpEditText;
import xlight.engine.core.editor.ui.options.XUIOpStringEditText;
import xlight.engine.ecs.XWorld;
import xlight.engine.scene.XScene;
import xlight.engine.scene.ecs.manager.XSceneManager;

public class XSceneWindowRenderer {

    private final static String TAB = "Scene";

    private XUIData uiData;

    public void onAttach(XWorld world) {
        uiData = world.getGlobalData(XUIData.class);
    }

    public void onDetach(XWorld world) {

    }

    public void render(XEditorManager editorManager) {
        if(ImGui.BeginTabItem(TAB)) {

            XEngine gameEngine = editorManager.getGameEngine();

            if(gameEngine != null) {
                XWorld world = gameEngine.getWorld();
                XSceneManager sceneManager = world.getManager(XSceneManager.class);
                renderScene(sceneManager);
            }

            ImGui.EndTabItem();
        }
    }

    private void renderScene(XSceneManager sceneManager) {
        XScene scene = sceneManager.getScene();
        uiData.beginTable();

        XUIOpStringEditText strOp = XUIOpStringEditText.get();
        if(uiData.editText("Name", scene.getName(), strOp)) {
            String trim = strOp.value.trim();
            if(!trim.isEmpty()) {
                scene.setName(trim);
            }
        }
        XUIOpEditText op = XUIOpEditText.get();
        if(uiData.editText("Id", scene.getId(), op)) {
            scene.setId((int)op.value);
        }
        strOp.enabled = false;
        if(uiData.editText("Path", scene.getPath(), strOp)) {
        }
        if(uiData.button("Scene", "Clear")) {
            sceneManager.newScene(-1, "New Scene");
        }
        uiData.endTable();
    }
}