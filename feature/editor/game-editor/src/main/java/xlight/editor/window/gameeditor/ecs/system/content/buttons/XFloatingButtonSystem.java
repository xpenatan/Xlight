package xlight.editor.window.gameeditor.ecs.system.content.buttons;

import imgui.ImGui;
import imgui.ImGuiChildFlags;
import imgui.ImGuiCol;
import imgui.ImGuiPopupFlags;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.extension.imlayout.ImLayout;
import xlight.editor.assets.XEditorAssets;
import xlight.editor.window.gameeditor.ecs.system.XGameEditorSystem;
import xlight.editor.window.gameeditor.ecs.system.content.aabb.XAABBDebugSystem;
import xlight.editor.window.gameeditor.ecs.system.content.entity.XBoundingBoxDebugSystem;
import xlight.editor.window.gameeditor.ecs.system.content.selection.XSelectingSystem;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.XWorldService;
import xlight.engine.ecs.system.XSystemData;
import xlight.engine.ecs.system.XSystemType;
import xlight.engine.transform.XGizmoType;

public class XFloatingButtonSystem extends XGameEditorSystem {

    private float buttonSize;

    private XSelectingSystem selectingSystem;

    @Override
    public void onSystemAttach(XWorld world, XSystemData systemData) {
    }

    @Override
    public void onTick(XWorld world) {
        if(selectingSystem == null) {
            selectingSystem = world.getWorldService().getSystemService().getSystem(XSelectingSystem.class);
        }

        buttonSize = 20 * ImLayout.GetDPIScale();

        ImGui.SetCursorPos(ImVec2.TMP_1.set(2, 2));

        ImGui.BeginChild("Buttons", ImVec2.TMP_1.set(0, 0), ImGuiChildFlags.ImGuiChildFlags_AutoResizeX | ImGuiChildFlags.ImGuiChildFlags_AutoResizeY);
        {
            ImGui.ImageButton("config", XEditorAssets.ic_gearTexture.getTextureObjectHandle(), ImVec2.TMP_1.set(buttonSize, buttonSize), ImVec2.TMP_2.set(0, 0), ImVec2.TMP_3.set(1, 1));
            if(ImGui.BeginPopupContextItem("config", ImGuiPopupFlags.ImGuiPopupFlags_MouseButtonLeft)) {
                renderConfigMenu(world);
                ImGui.EndPopup();
            }

            if(selectingSystem != null) {
                ImGui.SameLine(0, 2);

                ImVec4 buttonSelectedColor = ImGui.GetStyle().Colors(ImGuiCol.ImGuiCol_ButtonActive);
                XGizmoType transformType = selectingSystem.getTransformType();

                if(transformType == XGizmoType.POSITION) {
                    ImGui.PushStyleColor(ImGuiCol.ImGuiCol_Button, buttonSelectedColor);
                }
                if(ImGui.ImageButton("position", XEditorAssets.axisPositionTexture.getTextureObjectHandle(), ImVec2.TMP_1.set(buttonSize, buttonSize), ImVec2.TMP_2.set(0, 0), ImVec2.TMP_3.set(1, 1))) {
                    selectingSystem.setTransformType(XGizmoType.POSITION);
                }
                if(transformType == XGizmoType.POSITION) {
                    ImGui.PopStyleColor();
                }

                ImGui.SameLine(0, 2);

                if(transformType == XGizmoType.ROTATE) {
                    ImGui.PushStyleColor(ImGuiCol.ImGuiCol_Button, buttonSelectedColor);
                }
                if(ImGui.ImageButton("rotate", XEditorAssets.axisRotateTexture.getTextureObjectHandle(), ImVec2.TMP_1.set(buttonSize, buttonSize), ImVec2.TMP_2.set(0, 0), ImVec2.TMP_3.set(1, 1))) {
                    selectingSystem.setTransformType(XGizmoType.ROTATE);
                }
                if(transformType == XGizmoType.ROTATE) {
                    ImGui.PopStyleColor();
                }

                ImGui.SameLine(0, 2);

                if(transformType == XGizmoType.SCALE) {
                    ImGui.PushStyleColor(ImGuiCol.ImGuiCol_Button, buttonSelectedColor);
                }
                if(ImGui.ImageButton("scale", XEditorAssets.axisScaleTexture.getTextureObjectHandle(), ImVec2.TMP_1.set(buttonSize, buttonSize), ImVec2.TMP_2.set(0, 0), ImVec2.TMP_3.set(1, 1))) {
                    selectingSystem.setTransformType(XGizmoType.SCALE);
                }
                if(transformType == XGizmoType.SCALE) {
                    ImGui.PopStyleColor();
                }

                ImGui.SameLine(0, 2);

                boolean globalTransform = selectingSystem.isGlobalTransform();
                int globalTexture = globalTransform ? XEditorAssets.axisGlobalTexture.getTextureObjectHandle() : XEditorAssets.axisLocalTexture.getTextureObjectHandle();
                if(ImGui.ImageButton("global", globalTexture, ImVec2.TMP_1.set(buttonSize, buttonSize), ImVec2.TMP_2.set(0, 0), ImVec2.TMP_3.set(1, 1))) {
                    selectingSystem.setGlobalTransform(!globalTransform);
                }
            }
        }
        ImGui.EndChild();
    }

    private void renderConfigMenu(XWorld world) {
        if(ImGui.BeginMenu("Debug")) {
            XWorldService worldService = world.getWorldService();
            XSystemData aabbSystemData = worldService.getSystemService().getSystemData(XAABBDebugSystem.class);
            XSystemData boundingBoxSystemData = worldService.getSystemService().getSystemData(XBoundingBoxDebugSystem.class);

            if(aabbSystemData != null) {
                boolean enabled = aabbSystemData.isEnabled();
                if(ImGui.MenuItem("AABB Tree", "", enabled)) {
                    aabbSystemData.setEnabled(!enabled);
                }
            }

            if(boundingBoxSystemData != null) {
                ImGui.Separator();
                boolean enabled = boundingBoxSystemData.isEnabled();
                if(ImGui.MenuItem("Entity bounding box", "", enabled)) {
                    boundingBoxSystemData.setEnabled(!enabled);
                }
            }
            ImGui.EndMenu();
        }
    }

    @Override
    public XSystemType getType() {
        return XSystemType.UI;
    }
}
