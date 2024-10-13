package xlight.editor.imgui.util;

import imgui.ImGui;
import imgui.ImVec2;

public class XImGuiButton {

    public static boolean buttonMatchOrWrap(String text) {
        float sizeX = ImGui.CalcTextSize(text).get_x() + ImGui.GetStyle().get_FramePadding().get_x() * 2.0f;
        float avail = ImGui.GetContentRegionAvail().get_x();
        if(avail > sizeX) {
            sizeX = -1;
        }
        return ImGui.Button(text, ImVec2.TMP_1.set(sizeX, 0));
    }
}
