package xlight.engine.imgui.ui;

import com.badlogic.gdx.Gdx;
import imgui.ImGui;
import imgui.ImGuiDataType;
import imgui.ImGuiInputTextFlags;
import imgui.ImGuiInternal;
import imgui.ImGuiSliderFlags;
import imgui.ImGuiString;
import imgui.ImGuiStyleVar;
import imgui.ImVec2;
import imgui.idl.helper.IDLFloat;
import static imgui.ImGuiButtonFlags.ImGuiButtonFlags_MouseButtonLeft;
import static imgui.ImGuiCol.ImGuiCol_Text;

public class XEditText {

    private static final XEditTextData DATA = new XEditTextData();

    public static boolean render(String id, ImGuiString value) {
        boolean flag = ImGui.InputText(id, value, value.getSize(), ImGuiInputTextFlags.ImGuiInputTextFlags_EnterReturnsTrue);
        return flag;
    }

    public static boolean render(String id, XEditTextFloatData value01) {
        ImGui.PushID(id);
        boolean flag = renderSingleEditText(1, value01);
        ImGui.PopID();
        return flag;
    }

    public static boolean render(String id, XEditTextFloatData value01, XEditTextFloatData value02) {
        return renderF(id, DATA, value01, value02, null, null) >= 0;
    }

    public static boolean render(String id, XEditTextData data, XEditTextFloatData value01, XEditTextFloatData value02) {
        return renderF(id, data, value01, value02, null, null) >= 0;
    }

    public static boolean render(String id, XEditTextFloatData value01, XEditTextFloatData value02, XEditTextFloatData value03) {
        return renderF(id, DATA, value01, value02, value03, null) >= 0;
    }

    public static boolean render(String id, XEditTextData data, XEditTextFloatData value01, XEditTextFloatData value02, XEditTextFloatData value03) {
        return renderF(id, data, value01, value02, value03, null) >= 0;
    }

    public static boolean render(String id, XEditTextFloatData value01, XEditTextFloatData value02, XEditTextFloatData value03, XEditTextFloatData value04) {
        return renderF(id, DATA, value01, value02, value03, value04) >= 0;
    }

    public static boolean render(String id, XEditTextData data, XEditTextFloatData value01, XEditTextFloatData value02, XEditTextFloatData value03, XEditTextFloatData value04) {
        return renderF(id, data, value01, value02, value03, value04) >= 0;
    }

    public static boolean render(String id, XEditTextIntData value01) {
        ImGui.PushID(id);
        boolean flag = renderSingleEditText(1, value01);
        ImGui.PopID();
        return flag;
    }

    public static boolean render(String id, XEditTextIntData value01, XEditTextIntData value02) {
        return renderI(id, DATA, value01, value02, null, null) >= 0;
    }

    public static boolean render(String id, XEditTextData data, XEditTextIntData value01, XEditTextIntData value02) {
        return renderI(id, data, value01, value02, null, null) >= 0;
    }

    public static boolean render(String id, XEditTextIntData value01, XEditTextIntData value02, XEditTextIntData value03) {
        return renderI(id, DATA, value01, value02, value03, null) >= 0;
    }

    public static boolean render(String id, XEditTextData data, XEditTextIntData value01, XEditTextIntData value02, XEditTextIntData value03) {
        return renderI(id, data, value01, value02, value03, null) >= 0;
    }

    public static boolean render(String id, XEditTextIntData value01, XEditTextIntData value02, XEditTextIntData value03, XEditTextIntData value04) {
        return renderI(id, DATA, value01, value02, value03, value04) >= 0;
    }

    public static boolean render(String id, XEditTextData data, XEditTextIntData value01, XEditTextIntData value02, XEditTextIntData value03, XEditTextIntData value04) {
        return renderI(id, data, value01, value02, value03, value04) >= 0;
    }

    private static int renderF(String id, XEditTextData data, XEditTextFloatData value01, XEditTextFloatData value02, XEditTextFloatData value03, XEditTextFloatData value04) {
        int size = 0;
        boolean ret01 = false;
        boolean ret02 = false;
        boolean ret03 = false;
        boolean ret04 = false;

        if(value01 != null) {
            size = 1;
            if(value02 != null) {
                size = 2;
                if(value03 != null) {
                    size = 3;
                    if(value04 != null) {
                        size = 4;
                    }
                }
            }
        }

        if(size > 0) {
            ImGui.PushStyleVar(ImGuiStyleVar.ImGuiStyleVar_CellPadding, ImVec2.TMP_1.set(2, 0));
            if(ImGui.BeginTable("tableId", size, data.flags, ImVec2.TMP_1.set(data.width, 0))) {
                ImGui.TableNextColumn();
                ImGui.PushID(id);
                ret01 = renderSingleEditText(1, value01);
                if(value02 != null) {
                    ImGui.TableNextColumn();
                    ret02 = renderSingleEditText(2, value02);
                    if(value03 != null) {
                        ImGui.TableNextColumn();
                        ret03 = renderSingleEditText(3, value03);
                        if(value04 != null) {
                            ImGui.TableNextColumn();
                            ret04 = renderSingleEditText(4, value04);
                        }
                    }
                }
                ImGui.PopID();
                ImGui.EndTable();
            }
            ImGui.PopStyleVar();
        }

        if(ret01) {
            return 0;
        }
        else if(ret02) {
            return 1;
        }
        else if(ret03) {
            return 2;
        }
        else if(ret04) {
            return 3;
        }
        return -1;
    }

    private static int renderI(String id, XEditTextData data, XEditTextIntData value01, XEditTextIntData value02, XEditTextIntData value03, XEditTextIntData value04) {
        int size = 0;
        boolean ret01 = false;
        boolean ret02 = false;
        boolean ret03 = false;
        boolean ret04 = false;

        if(value01 != null) {
            size = 1;
            if(value02 != null) {
                size = 2;
                if(value03 != null) {
                    size = 3;
                    if(value04 != null) {
                        size = 4;
                    }
                }
            }
        }

        if(size > 0) {
            ImGui.PushStyleVar(ImGuiStyleVar.ImGuiStyleVar_CellPadding, ImVec2.TMP_1.set(2, 0));
            if(ImGui.BeginTable("tableId", size, data.flags, ImVec2.TMP_1.set(data.width, 0))) {
                ImGui.TableNextColumn();
                ImGui.PushID(id);
                ret01 = renderSingleEditText(1, value01);
                if(value02 != null) {
                    ImGui.TableNextColumn();
                    ret02 = renderSingleEditText(2, value02);
                    if(value03 != null) {
                        ImGui.TableNextColumn();
                        ret03 = renderSingleEditText(3, value03);
                        if(value04 != null) {
                            ImGui.TableNextColumn();
                            ret04 = renderSingleEditText(4, value04);
                        }
                    }
                }
                ImGui.PopID();
                ImGui.EndTable();
            }
            ImGui.PopStyleVar();
        }

        if(ret01) {
            return 0;
        }
        else if(ret02) {
            return 1;
        }
        else if(ret03) {
            return 2;
        }
        else if(ret04) {
            return 3;
        }
        return -1;
    }

    private static boolean renderSingleEditText(int id, XEditTextFloatData data) {
        boolean flag = false;
        String leftLabel = data.leftLabel;
        ImGui.PushID(id);

        ImGui.BeginGroup();

        if(leftLabel != null && !leftLabel.trim().isEmpty()) {
            boolean clicked = renderLeftLabel(leftLabel, 0, data.labelClickDragColor, data.labelClickDragColor);
            String format = "%.3f";
            int flags = ImGuiSliderFlags.ImGuiSliderFlags_None;
            if(ImGuiInternal.DragBehavior(ImGui.GetID(leftLabel), ImGuiDataType.ImGuiDataType_Float, data.value.getPointer(), data.v_speed, IDLFloat.TMP_1.set(data.v_min).getPointer(), IDLFloat.TMP_2.set(data.v_max).getPointer(), format, flags)) {
                flag = true;
            }
            ImGui.SameLine(0, 0);
        }
        if(data.width != 0) {
            ImGui.SetNextItemWidth(data.width);
        }
        if(ImGui.InputFloat("###edittext", data.value, data.step, data.step_fast, data.format, data.flags)) {
            if(data.v_min == 0 && data.v_max == 0) {
                flag = true;
            }
            else {
                float value = data.value.getValue();
                if(value >= data.v_min && value <= data.v_max) {
                    flag = true;
                }
            }
        }
        ImGui.EndGroup();

        if(data.tooltip != null && !data.tooltip.isEmpty() && ImGui.BeginItemTooltip()) {
            ImGui.Text(data.tooltip);
            ImGui.EndTooltip();
        }
        ImGui.PopID();
        return flag;
    }

    private static boolean renderSingleEditText(int id, XEditTextIntData data) {
        String leftLabel = data.leftLabel;
        ImGui.PushID(id);

        ImGui.BeginGroup();

        if(leftLabel != null && !leftLabel.trim().isEmpty()) {
            renderLeftLabel(leftLabel, 0, 0, 0);
        }

        ImGui.SameLine(0, 0);

        if(data.width != 0) {
            ImGui.SetNextItemWidth(-1);
        }
        boolean flag = ImGui.InputInt("###edittext", data.getValue(), data.step, data.step_fast, data.flags);
        ImGui.EndGroup();

        if(data.tooltip != null && !data.tooltip.isEmpty() && ImGui.BeginItemTooltip()) {
            ImGui.Text(data.tooltip);
            ImGui.EndTooltip();
        }
        ImGui.PopID();
        return flag;
    }

    public static boolean renderLeftLabel(String leftLabel, int color, int hoveredColor, int clickColor) {
        boolean flag = false;
        ImGui.BeginGroup();
        ImVec2 cursorPos = ImGui.GetCursorPos();
        ImVec2 textSize = ImGui.CalcTextSize(leftLabel);
        ImVec2.TMP_1.set(textSize.get_x(), textSize.get_y());
        flag = ImGui.InvisibleButton(leftLabel,ImVec2.TMP_1, ImGuiButtonFlags_MouseButtonLeft);
        int itemId = ImGui.GetItemID();
        int navId = ImGuiInternal.GetFocusID();

        if(hoveredColor != 0 && ImGui.IsItemHovered()) {
            color = hoveredColor;
        }

        if(clickColor != 0 && ImGui.IsItemActive()) {
            color = clickColor;
            Gdx.input.setCursorCatched(true);
        }
        else {
            if(itemId == navId && Gdx.input.isCursorCatched()) {
                Gdx.input.setCursorCatched(false);
            }
        }

        ImGui.SetCursorPos(cursorPos);
        ImGui.Dummy(ImVec2.TMP_1.set(0, 0));
        ImGui.SameLine(0, 2);
        ImGui.AlignTextToFramePadding();
        if(color != 0)
            ImGui.PushStyleColor(ImGuiCol_Text, color);
        ImGui.Text(leftLabel);
        if(color != 0)
            ImGui.PopStyleColor();
        ImGui.SameLine(0, 2);
        ImGui.Dummy(ImVec2.TMP_1.set(0, 0));
        ImGui.EndGroup();

        return flag;
    }
}