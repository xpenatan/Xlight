package xlight.editor.imgui;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import imgui.ImGui;
import xlight.editor.imgui.ecs.manager.XImGuiManager;
import xlight.engine.core.editor.ui.XUIData;
import xlight.engine.core.editor.ui.XUIDataTypeListener;
import xlight.engine.ecs.XWorld;
import xlight.engine.g3d.ecs.component.XGLTFComponent;

public class XUIRegisterComponents {

    public static void init(XWorld editorWorld) {

        XImGuiManager imguiManager = editorWorld.getManager(XImGuiManager.class);

        imguiManager.registerUIComponentListener(XGLTFComponent.class, new XUIDataTypeListener<XGLTFComponent>() {
            @Override
            public void onUIDraw(XGLTFComponent value, XUIData uiData) {
                ModelInstance modelInstance = value.getModelInstance();
                if(modelInstance != null) {
                    if(uiData.collapsingHeader("TEST")) {


//                        ImGui.Tree
//                        ImGui.Button("HEELLO");
                    }
                }
            }
        });
    }
}
