package xlight.engine.imgui.ui;

import imgui.ImGuiInputTextFlags;
import imgui.idl.helper.IDLInt;

public class XEditTextIntData {
    public String leftLabel;
    private IDLInt value;
    public int step;
    public int step_fast;
    public int v_min;
    public int v_max;
    public String tooltip;
    public int flags;
    public int width;

    public XEditTextIntData() {
        clear();
    }

    public IDLInt getValue() {
        if(value == null) {
            value = new IDLInt();
        }
        return value;
    }

    public void clear() {
        leftLabel = "";
        step = 0;
        step_fast = 0;
        v_min = 0;
        v_max = 0;
        tooltip = "";
        flags = ImGuiInputTextFlags.ImGuiInputTextFlags_EnterReturnsTrue;
        width = -1;
    }
}
