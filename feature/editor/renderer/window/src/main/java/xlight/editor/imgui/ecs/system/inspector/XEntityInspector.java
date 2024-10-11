package xlight.editor.imgui.ecs.system.inspector;

import com.badlogic.gdx.graphics.Texture;
import imgui.ImGui;
import imgui.extension.imlayout.ImGuiCollapseLayoutOptions;
import xlight.editor.assets.XEditorAssets;
import xlight.editor.imgui.ecs.manager.XImGuiManager;
import xlight.engine.core.editor.ui.XUIData;
import xlight.engine.core.editor.ui.XUIDataListener;
import xlight.engine.core.editor.ui.XUIDataTypeListener;
import xlight.engine.core.editor.ui.options.XUIOpCheckbox;
import xlight.engine.core.editor.ui.options.XUIOpStringEditText;
import xlight.engine.core.register.XRegisterManager;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.event.XEvent;
import xlight.engine.ecs.event.XEventService;
import xlight.engine.imgui.ui.XCollapseWidget;
import xlight.engine.imgui.ui.XUITableUtil;
import xlight.engine.pool.XPoolController;

public class XEntityInspector {
    private XImGuiManager imguiManager;
    private XUIComponentsWidget componentsWidget;

    public XEntityInspector(XImGuiManager imguiManager, XPoolController poolController) {
        this.imguiManager = imguiManager;
        componentsWidget = new XUIComponentsWidget(poolController);
    }

    public void renderEntity(XWorld editorWorld, XWorld gameWorld, XEntity entity, XUIData uiData) {
        XUIDataTypeListener<XEntity> entityUIListener = imguiManager.getEntityUIListener();
        XUITableUtil.start();

        String groupName = "Entity";
        Texture[] texturesArray = XCollapseWidget.getTexturesArray();

        ImGuiCollapseLayoutOptions defaultOptions = XCollapseWidget.defaultOptions;
        defaultOptions.set_paddingBottom(2);
        XCollapseWidget.defaultOptions.set_openDefault(true);
        int id = groupName.hashCode() + entity.hashCode();
        XCollapseWidget.CollaspeWidgetData widgetData = XCollapseWidget.begin(id, groupName, XCollapseWidget.WHITE_COLOR, texturesArray, defaultOptions);
        if(widgetData.isOpen) {

            {
                // TODO improve getting id as string
                uiData.text("ID:", "" + entity.getId());
                XUIOpStringEditText strOp = XUIOpStringEditText.get();
                if(uiData.editText("Name:", entity.getName(), strOp)) {
                    entity.setName(strOp.value);
                }
                XUIOpCheckbox op = XUIOpCheckbox.get();
                if(uiData.checkbox("Visible:", entity.isVisible(), op)) {
                    entity.setVisible(op.value);
                }
                op = XUIOpCheckbox.get();
                if(uiData.checkbox("Savable:", entity.isSavable(), op)) {
                    entity.setSavable(op.value);
                }
                XEntity parent = entity.getParent();
                uiData.text("Parent:", parent != null ? parent.getName() : "-1");
            }

            if(entityUIListener != null) {
                entityUIListener.onUIDraw(entity, uiData);
            }
            uiData.endTable();

            renderComponents(editorWorld, gameWorld, entity, uiData);
        }
        XCollapseWidget.end();
    }

    private void renderComponents(XWorld editorWorld, XWorld gameWorld, XEntity entity, XUIData uiData) {
        XRegisterManager registerManager = gameWorld.getManager(XRegisterManager.class);
        String groupName = "Components - " + entity.getComponentsSize();
        Texture[] texturesArray = XCollapseWidget.getTexturesArray();

        texturesArray[0] = XEditorAssets.ic_addTexture;

        ImGuiCollapseLayoutOptions defaultOptions = XCollapseWidget.defaultOptions;
        defaultOptions.set_paddingBottom(0);
        XCollapseWidget.defaultOptions.set_openDefault(true);
        int id = groupName.hashCode();
        XCollapseWidget.CollaspeWidgetData widgetData = XCollapseWidget.begin(id, groupName, XCollapseWidget.WHITE_COLOR, texturesArray, defaultOptions);

        if(widgetData.buttonIndex == 0) {
            ImGui.OpenPopup("OpenComponentList");
            componentsWidget.clear();
        }

        if(ImGui.BeginPopup("OpenComponentList")) {
            if(componentsWidget.render(gameWorld, entity)) {
                ImGui.CloseCurrentPopup();
            }
            ImGui.EndPopup();
        }

        if(widgetData.isOpen) {
            for(int i = 0; i < entity.getComponentsSize(); i++) {
                XComponent component = entity.getComponentAt(i);
                Class<?> classOrInterfaceType = component.getClassOrInterfaceType();
                registerManager.getRegisteredClass(classOrInterfaceType);
                renderComponent(registerManager, gameWorld, entity, uiData, component);
            }
        }

        XCollapseWidget.end();
    }

    private void renderComponent(XRegisterManager registerManager, XWorld gameWorld, XEntity entity, XUIData uiData, XComponent component) {
        String groupName = component.getClass().getSimpleName();
        Texture[] texturesArray = XCollapseWidget.getTexturesArray();
        texturesArray[0] = XEditorAssets.ic_trashTexture;

        ImGuiCollapseLayoutOptions defaultOptions = XCollapseWidget.defaultOptions;
        defaultOptions.set_paddingBottom(0);
        XCollapseWidget.defaultOptions.set_openDefault(true);
        int id = groupName.hashCode();
        XCollapseWidget.CollaspeWidgetData widgetData = XCollapseWidget.begin(id, groupName, XCollapseWidget.WHITE_COLOR, texturesArray, defaultOptions);

        if(widgetData.buttonIndex == 0) {
            gameWorld.getWorldService().getEventService().sendEvent(-1, null, new XEventService.XSendEventListener() {
                @Override
                public void onEndEvent(XEvent event) {
                    entity.detachComponent(component);
                }
            });
        }

        if(widgetData.isOpen) {
            if(component instanceof XUIDataListener) {
                XUIDataListener dataListener = (XUIDataListener)component;
                dataListener.onUIDraw(uiData);
            }
            Class<XComponent> classType = (Class<XComponent>)component.getClassOrInterfaceType();
            XUIDataTypeListener<XComponent> uiListener = imguiManager.getUIComponentListener(classType);
            uiData.endTable();
            if(uiListener != null) {
                uiListener.onUIDraw(component, uiData);
            }
        }

        XCollapseWidget.end();
    }
}