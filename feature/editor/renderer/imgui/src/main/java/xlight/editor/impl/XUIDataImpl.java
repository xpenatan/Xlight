package xlight.editor.impl;

import com.badlogic.gdx.utils.Array;
import imgui.ImGui;
import imgui.ImGuiString;
import imgui.idl.helper.IDLBool;
import xlight.engine.core.editor.ui.XUIData;
import xlight.engine.core.editor.ui.options.XUIOpButton;
import xlight.engine.core.editor.ui.options.XUIOpCheckbox;
import xlight.engine.core.editor.ui.options.XUIOpEditText;
import xlight.engine.core.editor.ui.options.XUIOpEditText2;
import xlight.engine.core.editor.ui.options.XUIOpEditText3;
import xlight.engine.core.editor.ui.options.XUIOpSelectList;
import xlight.engine.core.editor.ui.options.XUIOpStringEditText;
import xlight.engine.core.editor.ui.options.XUIOpTransform;
import xlight.engine.imgui.ui.XEditText;
import xlight.engine.imgui.ui.XEditTextFloatData;
import xlight.engine.imgui.ui.XEditTextIntData;
import xlight.engine.imgui.ui.XUITableUtil;
import xlight.engine.transform.XTransform;
import static imgui.ImGuiTreeNodeFlags.ImGuiTreeNodeFlags_DefaultOpen;

class XUIDataImpl implements XUIData {
    private boolean beginLine;

    public final XEditTextFloatData FLOAT_D_1;
    public final XEditTextFloatData FLOAT_D_2;
    public final XEditTextFloatData FLOAT_D_3;
    public final XEditTextFloatData FLOAT_D_4;

    public final XEditTextIntData INT_D_1;
    public final XEditTextIntData INT_D_2;
    public final XEditTextIntData INT_D_3;
    public final XEditTextIntData INT_D_4;

    public XUIDataImpl() {
        FLOAT_D_1 = new XEditTextFloatData();
        FLOAT_D_2 = new XEditTextFloatData();
        FLOAT_D_3 = new XEditTextFloatData();
        FLOAT_D_4 = new XEditTextFloatData();
        INT_D_1 = new XEditTextIntData();
        INT_D_2 = new XEditTextIntData();
        INT_D_3 = new XEditTextIntData();
        INT_D_4 = new XEditTextIntData();
    }

    @Override
    public boolean beginTable() {
        return XUITableUtil.beginTable();
    }

    @Override
    public void endTable() {
        XUITableUtil.endTable();
    }

    @Override
    public void beginLine(String name) {
        beginLine = true;
        XUITableUtil.beginLine(name);
        ImGui.PushID(name);
    }

    @Override
    public void endLine() {
        ImGui.PopID();
        XUITableUtil.endLine();
        beginLine = false;
    }

    @Override
    public boolean beginHeader(String groupName) {
        XUITableUtil.endTable();
        boolean flag = ImGui.CollapsingHeader(groupName, ImGuiTreeNodeFlags_DefaultOpen);
        if(flag) {
            XUITableUtil.beginTable();
        }
        return flag;
    }

    @Override
    public void endHeader() {
        XUITableUtil.endTable();
    }

    @Override
    public void text(String line, String text) {
        boolean addLine = !beginLine;
        if(addLine) {
            beginLine(line);
        }

        ImGui.Text(text);

        if(addLine) {
            endLine();
        }
    }

    @Override
    public boolean button(String line, String buttonName) {
        return button(line, buttonName, XUIOpButton.get());
    }

    @Override
    public boolean button(String line, String buttonName, XUIOpButton op) {
        boolean addLine = !beginLine;
        if(addLine) {
            beginLine(line);
        }
        boolean button = ImGui.Button(buttonName);

        if(addLine) {
            endLine();
        }
        if(op.tooltip != null && !op.tooltip.isEmpty() && ImGui.BeginItemTooltip()) {
            ImGui.Text(op.tooltip);
            ImGui.EndTooltip();
        }
        return button;
    }

    @Override
    public boolean editText(String line, float value, XUIOpEditText op) {
        FLOAT_D_1.clear();
        FLOAT_D_1.getValue().set(value);
        FLOAT_D_1.tooltip = op.tooltip;
        FLOAT_D_1.leftLabel = op.label;
        FLOAT_D_1.width = op.width;

        if(op.enableStep) {
            FLOAT_D_1.step = op.step;
            FLOAT_D_1.step_fast = 1f;
        }

        boolean addLine = !beginLine;
        if(addLine) {
            beginLine(line);
            line = "##edittext";
        }
        boolean flag = XEditText.render(line, FLOAT_D_1);
        if(flag) {
            op.value = FLOAT_D_1.getValue().getValue();
        }

        if(addLine) {
            endLine();
        }

        return flag;
    }

    @Override
    public boolean editText2(String line, float x, float y, XUIOpEditText2 op) {
        FLOAT_D_1.clear();
        FLOAT_D_2.clear();
        FLOAT_D_1.getValue().set(x);
        FLOAT_D_2.getValue().set(y);
        FLOAT_D_1.leftLabel = op.label1;
        FLOAT_D_1.tooltip = op.tooltip1;
        FLOAT_D_2.leftLabel = op.label2;
        FLOAT_D_2.tooltip = op.tooltip2;

        boolean addLine = !beginLine;
        if(addLine) {
            beginLine(line);
            line = "##edittext";
        }
        boolean flag = XEditText.render(line, FLOAT_D_1, FLOAT_D_2);
        if(flag) {
            float value1 = FLOAT_D_1.getValue().getValue();
            float value2 = FLOAT_D_2.getValue().getValue();
            op.value.set(value1, value2);
        }

        if(addLine) {
            endLine();
        }

        return flag;
    }

    @Override
    public boolean editText3(String line, float x, float y, float z, XUIOpEditText3 op) {
        FLOAT_D_1.clear();
        FLOAT_D_2.clear();
        FLOAT_D_3.clear();
        FLOAT_D_1.getValue().set(x);
        FLOAT_D_2.getValue().set(y);
        FLOAT_D_3.getValue().set(z);
        FLOAT_D_1.leftLabel = op.label1;
        FLOAT_D_1.tooltip = op.tooltip1;
        FLOAT_D_2.leftLabel = op.label2;
        FLOAT_D_2.tooltip = op.tooltip2;
        FLOAT_D_3.leftLabel = op.label3;
        FLOAT_D_3.tooltip = op.tooltip3;

        boolean addLine = !beginLine;
        if(addLine) {
            beginLine(line);
            line = "##edittext";
        }
        boolean flag = XEditText.render(line, FLOAT_D_1, FLOAT_D_2, FLOAT_D_3);
        if(flag) {
            float value1 = FLOAT_D_1.getValue().getValue();
            float value2 = FLOAT_D_2.getValue().getValue();
            float value3 = FLOAT_D_3.getValue().getValue();
            op.value.set(value1, value2, value3);
        }

        if(addLine) {
            endLine();
        }
        return flag;
    }

    @Override
    public boolean editText(String line, int value, XUIOpEditText op) {
        INT_D_1.clear();
        INT_D_1.getValue().set(value);
        INT_D_1.tooltip = op.tooltip;
        INT_D_1.leftLabel = op.label;
        INT_D_1.width = op.width;

        if(op.enableStep) {
            INT_D_1.step = 1;
            INT_D_1.step_fast = 100;
        }

        boolean addLine = !beginLine;
        if(addLine) {
            beginLine(line);
            line = "##edittext";
        }
        boolean flag = XEditText.render(line, INT_D_1);
        if(flag) {
            op.value = INT_D_1.getValue().getValue();
        }
        if(addLine) {
            endLine();
        }

        return flag;
    }

    @Override
    public boolean editText2(String line, int x, int y, XUIOpEditText2 op) {
        INT_D_1.clear();
        INT_D_2.clear();
        INT_D_1.getValue().set(x);
        INT_D_2.getValue().set(y);
        INT_D_1.leftLabel = op.label1;
        INT_D_1.tooltip = op.tooltip1;
        INT_D_2.leftLabel = op.label2;
        INT_D_2.tooltip = op.tooltip2;

        boolean addLine = !beginLine;
        if(addLine) {
            beginLine(line);
            line = "##edittext";
        }
        boolean flag = XEditText.render(line, INT_D_1, INT_D_2);
        if(flag) {
            float value1 = INT_D_1.getValue().getValue();
            float value2 = INT_D_2.getValue().getValue();
            op.value.set(value1, value2);
        }

        if(addLine) {
            endLine();
        }

        return flag;
    }

    @Override
    public boolean editText3(String line, int x, int y, int z, XUIOpEditText3 op) {
        INT_D_1.clear();
        INT_D_2.clear();
        INT_D_3.clear();
        INT_D_1.getValue().set(x);
        INT_D_2.getValue().set(y);
        INT_D_3.getValue().set(z);
        INT_D_1.leftLabel = op.label1;
        INT_D_1.tooltip = op.tooltip1;
        INT_D_2.leftLabel = op.label2;
        INT_D_2.tooltip = op.tooltip2;
        INT_D_3.leftLabel = op.label3;
        INT_D_3.tooltip = op.tooltip3;

        boolean addLine = !beginLine;
        if(addLine) {
            beginLine(line);
            line = "##edittext";
        }
        boolean flag = XEditText.render(line, INT_D_1, INT_D_2, INT_D_3);
        if(flag) {
            float value1 = INT_D_1.getValue().getValue();
            float value2 = INT_D_2.getValue().getValue();
            float value3 = INT_D_3.getValue().getValue();
            op.value.set(value1, value2, value3);
        }

        if(addLine) {
            endLine();
        }

        return flag;
    }

    @Override
    public boolean editText(String line, String value, XUIOpStringEditText op) {
        if(value == null) {
            value = "";
        }
        ImGuiString.TMP_1.setValue(value);

        boolean addLine = !beginLine;
        if(addLine) {
            beginLine(line);
            line = "##edittext";
        }

        String leftLabel = op.label;
        if(leftLabel != null && !leftLabel.trim().isEmpty()) {
            XEditText.renderLeftLabel(leftLabel, 0, 0, 0);
        }

        if(op.width != 0) {
            ImGui.SetNextItemWidth(op.width);
        }
        boolean flag = XEditText.render(line, ImGuiString.TMP_1);
        if(flag) {
            op.value = ImGuiString.TMP_1.getValue();
        }

        if(addLine) {
            endLine();
        }

        if(op.tooltip != null && !op.tooltip.isEmpty() && ImGui.BeginItemTooltip()) {
            ImGui.Text(op.tooltip);
            ImGui.EndTooltip();
        }

        return flag;
    }

    @Override
    public boolean selectList(String line, Array<String> items, int selectedIndex, XUIOpSelectList op) {
        boolean itemSelect = false;
        if(selectedIndex >= 0 && selectedIndex < items.size) {
            String selectedText = items.get(selectedIndex);

            boolean addLine = !beginLine;
            if(addLine) {
                beginLine(line);
            }
            else {
                ImGui.PushID(line.hashCode());
            }
            ImGui.PushItemWidth(-1);
            if (ImGui.BeginCombo("", selectedText)) {
                for(int i = 0; i < items.size; i++) {
                    String item = items.get(i);
                    boolean selected = i == selectedIndex;
                    if(ImGui.Selectable(item, selected)) {
                        itemSelect = true;
                        op.value = i;
                    }
                }
                ImGui.EndCombo();
            }

            if(addLine) {
                endLine();
            }
            else {
                ImGui.PopID();
            }
            if(op.tooltip != null && !op.tooltip.isEmpty() && ImGui.BeginItemTooltip()) {
                ImGui.Text(op.tooltip);
                ImGui.EndTooltip();
            }
        }

        return itemSelect;
    }

    @Override
    public boolean checkbox(String line, boolean value, XUIOpCheckbox op) {
        IDLBool.TMP_1.set(value);

        boolean addLine = !beginLine;
        if(addLine) {
            beginLine(line);
        }
        else {
            ImGui.PushID(line);
        }
        boolean flag = ImGui.Checkbox(op.label, IDLBool.TMP_1);
        if(flag) {
            op.value = IDLBool.TMP_1.getValue();
        }
        if(addLine) {
            endLine();
        }
        else {
            ImGui.PopID();
        }
        if(op.tooltip != null && !op.tooltip.isEmpty() && ImGui.BeginItemTooltip()) {
            ImGui.Text(op.tooltip);
            ImGui.EndTooltip();
        }

        return flag;
    }

    @Override
    public boolean transform(XTransform value, XUIOpTransform op) {
        return false;
    }
}