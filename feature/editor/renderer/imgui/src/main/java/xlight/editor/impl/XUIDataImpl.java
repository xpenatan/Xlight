package xlight.editor.impl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import imgui.ImGui;
import imgui.ImGuiPayload;
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

    private static int xColor = Color.RED.toIntBits();
    private static int yColor = Color.GREEN.toIntBits();
    private static int zColor = Color.toIntBits(0, 150, 255, 255);
    private static int wColor = Color.toIntBits(255, 255, 255, 255);

    private Object targetData;

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
        beginLine(name, 0);
    }

    @Override
    public void beginLine(String name, int lineColor) {
        beginLine = true;
        XUITableUtil.beginLine(name, lineColor);
        ImGui.PushID(name);
    }

    @Override
    public void endLine() {
        ImGui.PopID();
        XUITableUtil.endLine();
        beginLine = false;
    }

    @Override
    public boolean collapsingHeader(String name) {
        XUITableUtil.endTable();
        boolean flag = ImGui.CollapsingHeader(name, ImGuiTreeNodeFlags_DefaultOpen);
        return flag;
    }

    public void text(String line, String text) {
        beginTable();
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
        beginTable();
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
        beginTable();
        FLOAT_D_1.clear();
        FLOAT_D_1.value.set(value);
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
            op.value = FLOAT_D_1.value.getValue();
        }

        if(addLine) {
            endLine();
        }

        return flag;
    }

    @Override
    public boolean editText2(String line, float x, float y, XUIOpEditText2 op) {
        beginTable();
        FLOAT_D_1.clear();
        FLOAT_D_2.clear();
        FLOAT_D_1.value.set(x);
        FLOAT_D_2.value.set(y);
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
            float value1 = FLOAT_D_1.value.getValue();
            float value2 = FLOAT_D_2.value.getValue();
            op.value.set(value1, value2);
        }

        if(addLine) {
            endLine();
        }

        return flag;
    }

    @Override
    public boolean editText3(String line, float x, float y, float z, XUIOpEditText3 op) {
        beginTable();
        FLOAT_D_1.clear();
        FLOAT_D_2.clear();
        FLOAT_D_3.clear();
        FLOAT_D_1.value.set(x);
        FLOAT_D_2.value.set(y);
        FLOAT_D_3.value.set(z);
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
            float value1 = FLOAT_D_1.value.getValue();
            float value2 = FLOAT_D_2.value.getValue();
            float value3 = FLOAT_D_3.value.getValue();
            op.value.set(value1, value2, value3);
        }

        if(addLine) {
            endLine();
        }
        return flag;
    }

    @Override
    public boolean editText(String line, int value, XUIOpEditText op) {
        beginTable();
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
        beginTable();
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
        beginTable();
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
        beginTable();
        if(value == null) {
            value = "";
        }
        ImGuiString.TMP_1.setValue(value);

        boolean addLine = !beginLine;
        if(addLine) {
            beginLine(line, op.lineColor);
            line = "##edittext";
        }

        String leftLabel = op.label;
        if(leftLabel != null && !leftLabel.trim().isEmpty()) {
            XEditText.renderLeftLabel(leftLabel, 0, 0, 0);
        }

        if(op.width != 0) {
            ImGui.SetNextItemWidth(op.width);
        }
        if(!op.enabled) {
            ImGui.BeginDisabled();
        }
        boolean flag = XEditText.render(line, ImGuiString.TMP_1);
        if(!op.enabled) {
            ImGui.EndDisabled();
        }
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
        beginTable();
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
        beginTable();
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
    public void setDropTarget(Object object) {
        targetData = object;
    }

    @Override
    public boolean dropTarget(String source) {
        boolean ret = false;
        if(ImGui.BeginDragDropTarget()) {
            ImGuiPayload dragDropPayload = ImGui.AcceptDragDropPayload(source);
            if(dragDropPayload != null) {
                ret = true;
            }
            ImGui.EndDragDropTarget();
        }
        return ret;
    }

    @Override
    public Object consumeDropTarget() {
        Object data = targetData;
        targetData = null;
        return data;
    }

    @Override
    public boolean transform(XTransform transform, XUIOpTransform op) {
        beginTable();
        boolean flag = false;
        if(op.drawPosition) {
            XUITableUtil.beginLine(op.posLine);
            Vector3 position = transform.getPosition();
            FLOAT_D_1.clear();
            FLOAT_D_2.clear();
            FLOAT_D_3.clear();
            FLOAT_D_1.value.set(position.x);
            FLOAT_D_2.value.set(position.y);
            FLOAT_D_3.value.set(position.z);
            FLOAT_D_1.leftLabel = op.posLabel1;
            FLOAT_D_2.leftLabel = op.posLabel2;
            FLOAT_D_3.leftLabel = op.posLabel3;
            FLOAT_D_1.tooltip = op.posTooltip1;
            FLOAT_D_2.tooltip = op.posTooltip2;
            FLOAT_D_3.tooltip = op.posTooltip3;
            FLOAT_D_1.labelClickDragColor = xColor;
            FLOAT_D_2.labelClickDragColor = yColor;
            FLOAT_D_3.labelClickDragColor = zColor;
            if(XEditText.render("##POS", FLOAT_D_1, FLOAT_D_2, FLOAT_D_3)) {
                float value1 = FLOAT_D_1.value.getValue();
                float value2 = FLOAT_D_2.value.getValue();
                float value3 = FLOAT_D_3.value.getValue();
                transform.setPosition(value1, value2, value3);
                flag = true;
            }
            XUITableUtil.endLine();
        }

        if(op.drawRotation) {
            XUITableUtil.beginLine(op.rotLine);
            Vector3 rotation = transform.getRotation();
            FLOAT_D_1.clear();
            FLOAT_D_2.clear();
            FLOAT_D_3.clear();
            FLOAT_D_1.value.set(rotation.x);
            FLOAT_D_2.value.set(rotation.y);
            FLOAT_D_3.value.set(rotation.z);
            FLOAT_D_1.leftLabel = op.rotLabel1;
            FLOAT_D_2.leftLabel = op.rotLabel2;
            FLOAT_D_3.leftLabel = op.rotLabel3;
            FLOAT_D_1.tooltip = op.rotTooltip1;
            FLOAT_D_2.tooltip = op.rotTooltip2;
            FLOAT_D_3.tooltip = op.rotTooltip3;
            FLOAT_D_1.labelClickDragColor = xColor;
            FLOAT_D_2.labelClickDragColor = yColor;
            FLOAT_D_3.labelClickDragColor = zColor;
            if(XEditText.render("##ROT", FLOAT_D_1, FLOAT_D_2, FLOAT_D_3)) {
                float value1 = FLOAT_D_1.value.getValue();
                float value2 = FLOAT_D_2.value.getValue();
                float value3 = FLOAT_D_3.value.getValue();
                transform.setRotation(value1, value2, value3);
                flag = true;
            }
            XUITableUtil.endLine();
        }

        if(op.drawQuaternion) {
            XUITableUtil.beginLine(op.quatLine);
            Quaternion rotation = transform.getQuaternion();
            FLOAT_D_1.clear();
            FLOAT_D_2.clear();
            FLOAT_D_3.clear();
            FLOAT_D_1.value.set(rotation.x);
            FLOAT_D_2.value.set(rotation.y);
            FLOAT_D_3.value.set(rotation.z);
            FLOAT_D_4.value.set(rotation.w);
            FLOAT_D_1.leftLabel = op.quatLabel1;
            FLOAT_D_2.leftLabel = op.quatLabel2;
            FLOAT_D_3.leftLabel = op.quatLabel3;
            FLOAT_D_4.leftLabel = op.quatLabel4;
            FLOAT_D_1.tooltip = op.quatTooltip1;
            FLOAT_D_2.tooltip = op.quatTooltip2;
            FLOAT_D_3.tooltip = op.quatTooltip3;
            FLOAT_D_4.tooltip = op.quatTooltip4;
            FLOAT_D_1.labelClickDragColor = xColor;
            FLOAT_D_2.labelClickDragColor = yColor;
            FLOAT_D_3.labelClickDragColor = zColor;
            FLOAT_D_4.labelClickDragColor = wColor;
            if(XEditText.render("##QUAT", FLOAT_D_1, FLOAT_D_2, FLOAT_D_3, FLOAT_D_4)) {
                float value1 = FLOAT_D_1.value.getValue();
                float value2 = FLOAT_D_2.value.getValue();
                float value3 = FLOAT_D_3.value.getValue();
                float value4 = FLOAT_D_4.value.getValue();
                transform.setRotation(value1, value2, value3, value4);
                flag = true;
            }
            XUITableUtil.endLine();
        }

        if(op.drawScale) {
            XUITableUtil.beginLine(op.sclLine);
            Vector3 scale = transform.getScale();
            FLOAT_D_1.clear();
            FLOAT_D_2.clear();
            FLOAT_D_3.clear();
            FLOAT_D_1.value.set(scale.x);
            FLOAT_D_2.value.set(scale.y);
            FLOAT_D_3.value.set(scale.z);
            FLOAT_D_1.leftLabel = op.sclLabel1;
            FLOAT_D_2.leftLabel = op.sclLabel2;
            FLOAT_D_3.leftLabel = op.sclLabel3;
            FLOAT_D_1.tooltip = op.sclTooltip1;
            FLOAT_D_2.tooltip = op.sclTooltip2;
            FLOAT_D_3.tooltip = op.sclTooltip3;
            FLOAT_D_1.labelClickDragColor = xColor;
            FLOAT_D_2.labelClickDragColor = yColor;
            FLOAT_D_3.labelClickDragColor = zColor;
            if(XEditText.render("##SCL", FLOAT_D_1, FLOAT_D_2, FLOAT_D_3)) {
                float value1 = FLOAT_D_1.value.getValue();
                float value2 = FLOAT_D_2.value.getValue();
                float value3 = FLOAT_D_3.value.getValue();
                transform.setScale(value1, value2, value3);
                flag = true;
            }
            XUITableUtil.endLine();
        }

        if(op.drawSize) {
            XUITableUtil.beginLine(op.sizeLine);
            Vector3 size = transform.getSize();
            FLOAT_D_1.clear();
            FLOAT_D_2.clear();
            FLOAT_D_3.clear();
            FLOAT_D_1.value.set(size.x);
            FLOAT_D_2.value.set(size.y);
            FLOAT_D_3.value.set(size.z);
            FLOAT_D_1.leftLabel = op.sizeLabel1;
            FLOAT_D_2.leftLabel = op.sizeLabel2;
            FLOAT_D_3.leftLabel = op.sizeLabel3;
            FLOAT_D_1.tooltip = op.sizeTooltip1;
            FLOAT_D_2.tooltip = op.sizeTooltip2;
            FLOAT_D_3.tooltip = op.sizeTooltip3;
            FLOAT_D_1.labelClickDragColor = xColor;
            FLOAT_D_2.labelClickDragColor = yColor;
            FLOAT_D_3.labelClickDragColor = zColor;
            FLOAT_D_1.v_min = 0f;
            FLOAT_D_1.v_max = 500f;
            FLOAT_D_2.v_min = 0f;
            FLOAT_D_2.v_max = 500f;
            FLOAT_D_3.v_min = 0f;
            FLOAT_D_3.v_max = 500f;
            if(XEditText.render("##SIZE", FLOAT_D_1, FLOAT_D_2, FLOAT_D_3)) {
                float value1 = FLOAT_D_1.value.getValue();
                float value2 = FLOAT_D_2.value.getValue();
                float value3 = FLOAT_D_3.value.getValue();
                transform.setSize(value1, value2, value3);
            }
            XUITableUtil.endLine();
        }

        if(op.drawOffset) {
            XUITableUtil.beginLine(op.offsetLine);
            Vector3 offset = transform.getOffset();
            FLOAT_D_1.clear();
            FLOAT_D_2.clear();
            FLOAT_D_3.clear();
            FLOAT_D_1.value.set(offset.x);
            FLOAT_D_2.value.set(offset.y);
            FLOAT_D_3.value.set(offset.z);
            FLOAT_D_1.leftLabel = op.offsetLabel1;
            FLOAT_D_2.leftLabel = op.offsetLabel2;
            FLOAT_D_3.leftLabel = op.offsetLabel3;
            FLOAT_D_1.tooltip = op.offsetTooltip1;
            FLOAT_D_2.tooltip = op.offsetTooltip2;
            FLOAT_D_3.tooltip = op.offsetTooltip3;
            FLOAT_D_1.labelClickDragColor = xColor;
            FLOAT_D_2.labelClickDragColor = yColor;
            FLOAT_D_3.labelClickDragColor = zColor;

            FLOAT_D_1.v_min = -0.5f;
            FLOAT_D_1.v_max = 0.5f;
            FLOAT_D_2.v_min = -0.5f;
            FLOAT_D_2.v_max = 0.5f;
            FLOAT_D_3.v_min = -0.5f;
            FLOAT_D_3.v_max = 0.5f;
            if(XEditText.render("##OFFSET", FLOAT_D_1, FLOAT_D_2, FLOAT_D_3)) {
                float value1 = FLOAT_D_1.value.getValue();
                float value2 = FLOAT_D_2.value.getValue();
                float value3 = FLOAT_D_3.value.getValue();
                transform.setOffset(value1, value2, value3);
                flag = true;
            }
            XUITableUtil.endLine();
        }
        return flag;
    }
}