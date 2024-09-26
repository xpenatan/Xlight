package xlight.engine.imgui.ui;

import imgui.ImGuiInputTextFlags;
import imgui.idl.helper.IDLFloat;

public class XEditTextFloatData {
    public String leftLabel;
    public final IDLFloat value = new IDLFloat();;
    public float v_speed;
    public float v_min;
    public float v_max;
    public String format;
    public String tooltip;
    public int flags;
    public int width;
    public float step = 0;
    public float step_fast;
    public int labelClickDragColor = 0;

    public XEditTextFloatData() {
        clear();
    }

    public void clear() {
        leftLabel = "";
        v_speed = 0.01f;
        v_min = 0f;
        v_max = 0f;
        format = "%.3f";
        tooltip = "";
        flags = ImGuiInputTextFlags.ImGuiInputTextFlags_EnterReturnsTrue;
        width = -1;
        step = 0;
        step_fast = 0;
    }
}
