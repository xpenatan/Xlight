package xlight.editor.imgui.ecs.system.inspector;

import com.badlogic.gdx.graphics.Texture;
import imgui.ImGui;
import imgui.extension.imlayout.ImGuiCollapseLayoutOptions;
import xlight.editor.assets.XEditorAssets;
import xlight.editor.imgui.ecs.manager.XImGuiManager;
import xlight.engine.core.editor.ui.XUIData;
import xlight.engine.core.editor.ui.XUIDataTypeListener;
import xlight.engine.core.editor.ui.options.XUIOpCheckbox;
import xlight.engine.core.editor.ui.options.XUIOpStringEditText;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.imgui.ui.XCollapseWidget;
import xlight.engine.imgui.ui.XUITableUtil;

public class XEntityInspector {
    private XImGuiManager imguiManager;

    public XEntityInspector(XImGuiManager imguiManager) {
        this.imguiManager = imguiManager;
    }

    public void renderEntity(XWorld editorWorld, XWorld gameWorld, XEntity entity, XUIData uiData) {
        XUIDataTypeListener<XEntity> entityUIListener = imguiManager.getEntityUIListener();
        XUITableUtil.start();

        String groupName = "Entity";
        Texture[] texturesArray = XCollapseWidget.getTexturesArray();

        ImGuiCollapseLayoutOptions defaultOptions = XCollapseWidget.defaultOptions;
        defaultOptions.set_paddingBottom(2);
        XCollapseWidget.defaultOptions.set_openDefault(true);
        int id = groupName.hashCode();
        XCollapseWidget.CollaspeWidgetData widgetData = XCollapseWidget.begin(id, groupName, XCollapseWidget.WHITE_COLOR, texturesArray, defaultOptions);
        if(widgetData.isOpen) {

            uiData.beginTable();
            {
                // TODO improve getting id as string
                uiData.text("ID:", "" + entity.getId());
                XUIOpStringEditText strOp = XUIOpStringEditText.get();
                if(uiData.editText("ID:", entity.getName(), strOp)) {
                    entity.setName(strOp.value);
                }
                XUIOpCheckbox op = XUIOpCheckbox.get();
                if(uiData.checkbox("Is Visible:", entity.isVisible(), op)) {
                    entity.setVisible(op.value);
                }
                op = XUIOpCheckbox.get();
                if(uiData.checkbox("Is Savable:", entity.isSavable(), op)) {
                    entity.setSavable(op.value);
                }
                uiData.text("Components Size:", "" + entity.getComponentsSize());

                XEntity parent = entity.getParent();
                uiData.text("Parent:", parent != null ? parent.getName() : "-1");
            }
            uiData.endTable();

            if(entityUIListener != null) {
                entityUIListener.onUIDraw(entity, uiData);
            }

            renderComponents(editorWorld, gameWorld, entity, uiData);
        }
        XCollapseWidget.end();
    }

    private void renderComponents(XWorld editorWorld, XWorld gameWorld, XEntity entity, XUIData uiData) {
        String groupName = "Components";
        Texture[] texturesArray = XCollapseWidget.getTexturesArray();

        texturesArray[0] = XEditorAssets.ic_addTexture;

        ImGuiCollapseLayoutOptions defaultOptions = XCollapseWidget.defaultOptions;
        defaultOptions.set_paddingBottom(0);
        XCollapseWidget.defaultOptions.set_openDefault(true);
        int id = groupName.hashCode();
        XCollapseWidget.CollaspeWidgetData widgetData = XCollapseWidget.begin(id, groupName, XCollapseWidget.WHITE_COLOR, texturesArray, defaultOptions);

        if(widgetData.buttonIndex == 0) {
            ImGui.OpenPopup("OpenComponentList");
        }

        if(ImGui.BeginPopup("OpenComponentList")) {

            ImGui.EndPopup();
        }

        if(widgetData.isOpen) {
            for(int i = 0; i < entity.getComponentsSize(); i++) {
                XComponent component = entity.getComponentAt(i);
                renderComponent(editorWorld, gameWorld, entity, uiData, component);
            }
        }

        XCollapseWidget.end();
    }

    private void renderComponent(XWorld editorWorld, XWorld gameWorld, XEntity entity, XUIData uiData, XComponent component) {


    }
}