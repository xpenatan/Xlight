package xlight.editor.window.gameeditor.ecs.system.content.buttons;

import imgui.ImGui;
import imgui.ImGuiChildFlags;
import imgui.ImGuiPopupFlags;
import imgui.ImVec2;
import xlight.editor.assets.XEditorAssets;
import xlight.editor.window.gameeditor.ecs.system.XGameEditorSystem;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.system.XSystemType;

public class XFloatingButtonSystem extends XGameEditorSystem {

    private final float buttonSize = 20;

    boolean menuOpen;

    @Override
    public void onAttach(XWorld world) {
    }

    @Override
    public void onTick(XWorld world) {
        ImGui.SetCursorPos(ImVec2.TMP_1.set(2, 2));

        ImGui.BeginChild("Buttons", ImVec2.TMP_1.set(0, 0), ImGuiChildFlags.ImGuiChildFlags_AutoResizeX | ImGuiChildFlags.ImGuiChildFlags_AutoResizeY);


        if(ImGui.ImageButton("config", XEditorAssets.ic_gearTexture.getTextureObjectHandle(), ImVec2.TMP_1.set(buttonSize, buttonSize), ImVec2.TMP_2.set(0, 0), ImVec2.TMP_3.set(1, 1))) {

        }
        if(ImGui.BeginPopupContextItem("config", ImGuiPopupFlags.ImGuiPopupFlags_MouseButtonLeft)) {
            renderConfigMenu();
            ImGui.EndPopup();
        }

        ImGui.SameLine(0, 2);
        if(ImGui.ImageButton("position", XEditorAssets.axisPositionTexture.getTextureObjectHandle(), ImVec2.TMP_1.set(buttonSize, buttonSize), ImVec2.TMP_2.set(0, 0), ImVec2.TMP_3.set(1, 1))) {
        }
        ImGui.SameLine(0, 2);
        if(ImGui.ImageButton("rotate", XEditorAssets.axisRotateTexture.getTextureObjectHandle(), ImVec2.TMP_1.set(buttonSize, buttonSize), ImVec2.TMP_2.set(0, 0), ImVec2.TMP_3.set(1, 1))) {
        }
        ImGui.SameLine(0, 2);
        if(ImGui.ImageButton("scale", XEditorAssets.axisScaleTexture.getTextureObjectHandle(), ImVec2.TMP_1.set(buttonSize, buttonSize), ImVec2.TMP_2.set(0, 0), ImVec2.TMP_3.set(1, 1))) {
        }

        ImGui.EndChild();
    }

    private void renderConfigMenu() {
        ImGui.MenuItem("Item1");
        ImGui.Separator();
        ImGui.MenuItem("Item2");
    }

    @Override
    public XSystemType getType() {
        return XSystemType.UI;
    }
}
