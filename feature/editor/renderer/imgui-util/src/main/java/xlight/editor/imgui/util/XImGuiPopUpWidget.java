package xlight.editor.imgui.util;

import imgui.ImGui;
import imgui.ImGuiInputTextFlags;
import imgui.ImGuiString;

public class XImGuiPopUpWidget {

    public static boolean renderSceneSavePath(ImGuiString pathString) {
        ImGui.Text("Scene Path:");
        ImGui.SameLine();
        ImGui.SetNextItemWidth(150);
        int flag = ImGuiInputTextFlags.ImGuiInputTextFlags_EnterReturnsTrue;
        if(ImGui.InputText("##saveScene", pathString, pathString.getSize(), flag)) {
            return true;
        }
        return false;
    }
}
