package xlight.editor.imgui.ecs.system.hierarchy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import imgui.ImGui;
import imgui.ImGuiCol;
import imgui.ImGuiContext;
import imgui.ImGuiDragDropFlags;
import imgui.ImGuiInternal;
import imgui.ImGuiMouseButton;
import imgui.ImGuiPayload;
import imgui.ImGuiWindow;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.extension.imlayout.ImLayout;
import xlight.editor.assets.XEditorAssets;
import xlight.editor.core.ecs.event.XEditorEvent;
import xlight.editor.core.ecs.manager.XEditorManager;
import xlight.editor.core.ecs.manager.XEntitySelectionManager;
import xlight.engine.camera.ecs.component.XCameraComponent;
import xlight.engine.core.XEngine;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.ecs.component.XGameComponent;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.ecs.event.XEventService;
import xlight.engine.list.XIntSet;
import xlight.engine.list.XList;
import xlight.engine.pool.XPoolController;
import xlight.engine.pool.ecs.manager.XPoolManager;
import xlight.engine.string.XStringUtil;
import xlight.engine.string.XTextBuilder;
import xlight.engine.transform.ecs.component.XTransformComponent;
import static imgui.ImGuiCond.ImGuiCond_Once;
import static imgui.ImGuiHoveredFlags.ImGuiHoveredFlags_ChildWindows;

public class XEntityHierarchyRenderer { // implements HierarchyPrintFolderListener<XEntityHierarchyRenderer.HierarchyEntityNode> {

    private final static String TAB = "Entities";

    final static String ENTITY_SIZE_LABEL = "Total: ";
    final static String ENTITY_SELECTED_LABEL = " Selected: ";

    public final static String DRAG_ENTITY_ID = "DRAG_ENTITY_ID";

    private XEntitySelectionManager selectionManager;

    private boolean renderTree;

    public XEntityHierarchyRenderer() {

    }

    public void onAttach(XWorld world) {
        XEventService eventService = world.getEventService();

        selectionManager = world.getManager(XEntitySelectionManager.class);

        eventService.addEventListener(XEditorEvent.EVENT_ENGINE_CREATED, event -> {
            renderTree = true;
            return false;
        });
        eventService.addEventListener(XEditorEvent.EVENT_ENGINE_DISPOSED, event -> {
            renderTree = false;
            return false;
        });
    }

    public void onDetach(XWorld world) {

    }

    public void renderEntities(XWorld world, XEditorManager editorManager) {
        if(ImGui.BeginTabItem(TAB)) {
            if(renderTree) {
                renderContent(editorManager);
            }
            ImGui.EndTabItem();
        }
    }

    private void renderContent(XEditorManager editorManager) {
        int childId = 0;
        int entitySize = 0;
        XEngine gameEngine = editorManager.getGameEngine();
        ImGui.BeginChild(TAB, ImVec2.TMP_1.set(0, -ImGui.GetFrameHeightWithSpacing()));
        {
            ImGuiWindow currentWindow = ImGuiInternal.GetCurrentWindow();
            childId = currentWindow.get_ID();
            if(gameEngine != null) {
                XWorld gameEngineWorld = gameEngine.getWorld();

                boolean b = ImGuiInternal.IsDragDropActive();

                XEntityService entityService = gameEngineWorld.getEntityService();
                XList<XEntity> entities = entityService.getEntities();
                entitySize = entities.getSize();
                boolean leftControl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT);
                for(XEntity entity : entities) {
                    if(entity.getParent() != null) {
                        continue;
                    }
                    renderEntityItem(gameEngineWorld, entity, leftControl);
                }
            }
        }
        ImGui.EndChild();

        ImGui.Separator();

        XTextBuilder stringBuilder = XStringUtil.get();

        int selectedEntitySize = selectionManager.getSelectedTargets().getSize();

        stringBuilder.setLength(0);
        stringBuilder.append(ENTITY_SIZE_LABEL);
        stringBuilder.append(entitySize);
        stringBuilder.append(ENTITY_SELECTED_LABEL);
        stringBuilder.append(selectedEntitySize);
        ImGui.Text(stringBuilder.toString());

        if(ImGui.IsMouseClicked(ImGuiMouseButton.ImGuiMouseButton_Left)) {
            // Unselect is click is outside of tree item
            ImGuiContext currentContext = ImGui.GetCurrentContext();
            ImGuiWindow imGuiWindow = currentContext.get_HoveredWindow();
            if(imGuiWindow != null) {
                int hoveredId = currentContext.get_HoveredId();
                int ActiveId = currentContext.get_ActiveId();
                int windowId = imGuiWindow.get_ID();
                if(windowId == childId && ActiveId == 0 && hoveredId == 0) {
                    selectionManager.unselectAllTargets();
                }
            }
        }
        if(gameEngine != null) {
            if(ImGui.IsWindowHovered(ImGuiHoveredFlags_ChildWindows)) {
                if(ImGui.IsMouseClicked(ImGuiMouseButton.ImGuiMouseButton_Right)) {
                    ImGui.OpenPopup("EntityOptions");
                }
            }

            if(ImGui.BeginPopup("EntityOptions")) {
                renderNewEntityPopUp(gameEngine);
                ImGui.EndPopup();
            }
        }
    }

    private void renderNewEntityPopUp(XEngine gameEngine) {
        XWorld world = gameEngine.getWorld();
        XPoolController poolController = world.getManager(XPoolManager.class).getPoolController();
        if(ImGui.BeginMenu("New Entity")) {
            if(ImGui.MenuItem("Empty")) {
                XEntityService entityService = gameEngine.getWorld().getEntityService();
                XEntity newEntity = entityService.obtain();
                newEntity.setName("Empty");
                entityService.attachEntity(newEntity);
                ImGui.CloseCurrentPopup();
            }

            if(ImGui.BeginMenu("Game Entity")) {
                if(ImGui.MenuItem("Basic")) {
                    XEntityService entityService = gameEngine.getWorld().getEntityService();
                    XEntity newEntity = entityService.obtain();
                    newEntity.setName("Basic");
                    XComponent gameComponent = poolController.obtainObject(XGameComponent.class);
                    XComponent transformComponent = poolController.obtainObject(XTransformComponent.class);
                    newEntity.attachComponent(gameComponent);
                    newEntity.attachComponent(transformComponent);

                    entityService.attachEntity(newEntity);
                }
                if(ImGui.BeginItemTooltip()) {
                    ImGui.Text("Components:\nGame\nTransform");
                    ImGui.EndTooltip();
                }
                if(ImGui.MenuItem("Default")) {
                    XEntityService entityService = gameEngine.getWorld().getEntityService();
                    XEntity newEntity = entityService.obtain();
                    newEntity.setName("Default");
                    XComponent gameComponent = poolController.obtainObject(XGameComponent.class);
                    XComponent transformComponent = poolController.obtainObject(XTransformComponent.class);
                    newEntity.attachComponent(gameComponent);
                    newEntity.attachComponent(transformComponent);

                    entityService.attachEntity(newEntity);
                }
                if(ImGui.BeginItemTooltip()) {
                    ImGui.Text("Components:\nGame\nTransform\nAABB");
                    ImGui.EndTooltip();
                }
                if(ImGui.BeginMenu("3D")) {
                    if(ImGui.MenuItem("Asset")) {
//                        XEntityService entityService = gameEngine.getWorld().getEntityService();
//                        XEntity newEntity = entityService.obtain();
//                        newEntity.setName("Asset");
//                        XGameComponent gameComponent = poolController.obtainObject(XGameComponent.class);
//                        XTransformComponent transformComponent = poolController.obtainObject(XTransformComponent.class);
//                        XRender3DComponent modelComponent = poolController.obtainObject(XRender3DComponent.class);
//                        modelComponent.createAndAddShapeType(XAssetModel.MODEL_TYPE);
//
//                        newEntity.attachComponent(gameComponent);
//                        newEntity.attachComponent(transformComponent);
//                        newEntity.attachComponent(modelComponent);
//
//                        entityService.attachEntity(newEntity);
                    }
                    if(ImGui.BeginItemTooltip()) {
                        ImGui.Text("Components:\nGame\nTransform\nAABB\nModel");
                        ImGui.EndTooltip();
                    }
                    ImGui.EndMenu();
                }

                if(ImGui.MenuItem("Camera")) {
                    XEntityService entityService = gameEngine.getWorld().getEntityService();
                    XEntity newEntity = entityService.obtain();
                    newEntity.setName("Camera");
                    XGameComponent gameComponent = poolController.obtainObject(XGameComponent.class);
                    XTransformComponent transformComponent = poolController.obtainObject(XTransformComponent.class);
                    XCameraComponent cameraComponent = poolController.obtainObject(XCameraComponent.class);

                    newEntity.attachComponent(gameComponent);
                    newEntity.attachComponent(transformComponent);
                    newEntity.attachComponent(cameraComponent);

                    entityService.attachEntity(newEntity);
                }
                if(ImGui.BeginItemTooltip()) {
                    ImGui.Text("Components:\nGame\nTransform\nAABB\nCamera");
                    ImGui.EndTooltip();
                }

                ImGui.EndMenu();
            }

            if(ImGui.BeginItemTooltip()) {
                ImGui.Text("UI and Transform component");
                ImGui.EndTooltip();
            }
            ImGui.EndMenu();
        }

    }

    Array<XIntSet.XIntSetNode> list = new Array<>();

    private void renderEntityItem(XWorld gameWorld, XEntity entity, boolean leftControl) {
        boolean isOpen = renderEntity(gameWorld, entity, leftControl);
        if(isOpen) {
            XEntityService entityService = gameWorld.getEntityService();
            XIntSet.XIntSetNode cur = entity.getChildHead();
            while(cur != null) {
                int key = cur.getKey();
                XEntity child = entityService.getEntity(key);

                boolean isChildOpen = renderEntity(gameWorld, child, leftControl);

                if(isChildOpen) {
                    list.add(cur);
                    cur = child.getChildHead();
                }
                else {
                    ImLayout.EndTree();
                    cur = cur.getNext();
                    if(cur == null) {
                        while(cur == null && !list.isEmpty()) {
                            cur = list.pop();
                            cur = cur.getNext();
                            ImLayout.EndTree();
                        }
                    }
                }
            }
        }
        ImLayout.EndTree();
    }

    private boolean renderEntity(XWorld gameWorld, XEntity entity, boolean leftControl) {
        String name = entity.getName();
        String s = entity.getId() + " " + name;
        int size = entity.getChildList().getSize();
        boolean isLeaf = size == 0;
        if(size != 0) {
            s += " - " + size;
        }
        boolean selected = selectionManager.isSelected(entity);

        ImLayout.BeginTree(s);

        ImGui.SetNextItemOpen(true, ImGuiCond_Once);
        float height = ImLayout.GetTreeHeight(10);
        ImLayout.BeginTreeLayout(height, isLeaf, selected);

        ImLayout.BeginAlign("entitYId", ImLayout.WRAP_PARENT, ImLayout.MATCH_PARENT, 0f, 0.5f);
        ImGui.Text(s);
        ImLayout.EndAlign();

        ImGui.SameLine();

        ImLayout.BeginAlign("imlayout2", ImLayout.MATCH_PARENT, ImLayout.MATCH_PARENT, 1, 0.5f, -4, 0);
        {

            ImGui.PushStyleColor(ImGuiCol.ImGuiCol_Button, Color.toIntBits(0, 0, 0, 0));
            boolean savable = entity.isSavable();
            ImLayout.BeginAlign("saveID", ImLayout.WRAP_PARENT, ImLayout.MATCH_PARENT, 0, 0.5f);
            {
                float alpha = savable ? 1.0f : 0.4f;
                if(ImGui.ImageButton("savable", XEditorAssets.saveTexture.getTextureObjectHandle(), ImVec2.TMP_1.set(14, 15), ImVec2.TMP_2.set(0, 0), ImVec2.TMP_3.set(0.9f, 1), ImVec4.TMP_1.set(0, 0, 0, 0), ImVec4.TMP_2.set(1, 1, 1, alpha))) {
                    entity.setSavable(!savable);
                }
            }
            ImLayout.EndAlign();
            if (ImGui.BeginItemTooltip()) {
                ImGui.Text("Savable " + savable);
                ImGui.EndTooltip();
            }
            ImGui.SameLine(0, 0);

            boolean visible = entity.isVisible();
            int tex = visible ? XEditorAssets.ic_eyeOpenTexture.getTextureObjectHandle() : XEditorAssets.ic_eyeCloseTexture.getTextureObjectHandle();

            ImLayout.BeginAlign("saveID", ImLayout.WRAP_PARENT, ImLayout.MATCH_PARENT, 0, 0.5f);
            {
                if(ImGui.ImageButton("visibility", tex, ImVec2.TMP_1.set(15, 15))) {
                    entity.setVisible(!visible);
                }
            }
            ImLayout.EndAlign();
            if (ImGui.BeginItemTooltip()) {
                ImGui.Text("Visibility " + visible);
                ImGui.EndTooltip();
            }

            ImGui.SameLine(0, 0);
            ImLayout.BeginAlign("saveID", ImLayout.WRAP_PARENT, ImLayout.MATCH_PARENT, 0, 0.5f);
            {
                if(ImGui.ImageButton("entityTrash", XEditorAssets.ic_trashTexture.getTextureObjectHandle(), ImVec2.TMP_1.set(15, 15))) {
                    XEntityService entityService = gameWorld.getEntityService();
                    entityService.releaseEntity(entity);
                }
            }
            ImLayout.EndAlign();
            if (ImGui.BeginItemTooltip()) {
                ImGui.Text("Delete entity");
                ImGui.EndTooltip();
            }
            ImGui.PopStyleColor();
        }
        ImLayout.EndAlign();

        if(ImLayout.EndTreeLayout()) {
            selectionManager.selectTarget(entity, leftControl);
        }
        boolean treeOpen = ImLayout.IsTreeOpen();
        if (ImGui.BeginDragDropSource()) {
            int id = entity.getId();
            ImGui.SetDragDropPayload(DRAG_ENTITY_ID, id, ImGuiCond_Once);
            ImGui.Text("Cannot attach");
            ImGui.EndDragDropSource();
        }
        if(ImGui.BeginDragDropTarget()) {
            int flag = ImGuiDragDropFlags.ImGuiDragDropFlags_AcceptBeforeDelivery | ImGuiDragDropFlags.ImGuiDragDropFlags_AcceptNoPreviewTooltip;
            ImGuiPayload dragDropPayload = ImGui.AcceptDragDropPayload(DRAG_ENTITY_ID, flag);

            if(dragDropPayload != null) {
                boolean isDelivery = dragDropPayload.IsDelivery();
                boolean isPreview = dragDropPayload.IsPreview();

                int draggedEntityID = dragDropPayload.get_Data();
                XEntity draggedEntity = gameWorld.getEntityService().getEntity(draggedEntityID);

                if(isPreview) {
                    if(ImGui.BeginTooltip()) {
                        String s2 = draggedEntity.getId() + " " + draggedEntity.getName();
                        int size2 = draggedEntity.getChildList().getSize();
                        if(size2 != 0) {
                            s2 += "- " + size2;
                        }
                        //TODO make a entity icon layout
                        if(draggedEntity.getParent() == entity) {
                            ImGui.Text("Remove: " + s2);
                        }
                        else {
                            ImGui.Text("Add: " + s2);
                        }
                        ImGui.EndTooltip();
                    }
                }
                if(isDelivery) {
                    if(draggedEntity != null) {
                        if(draggedEntity.getParent() == entity) {
                            draggedEntity.setParent(null);
                        }
                        else {
                            draggedEntity.setParent(entity);
                        }
                    }
                }
            }
            else {
                ImGuiPayload dragDropPayload1 = ImGui.GetDragDropPayload();
                if(dragDropPayload1 != null) {
                    int data = dragDropPayload1.get_Data();
                    if(data == entity.getId()) {
                        entity.setParent(null);
                    }
                }
            }
            ImGui.EndDragDropTarget();
        }
        return treeOpen;
    }
}