package xlight.engine.imgui.ui;

import imgui.ImGui;
import imgui.ImGuiInternal;
import imgui.ImGuiStorage;
import imgui.ImGuiTableColumnFlags;
import imgui.ImGuiTableFlags;
import imgui.ImVec2;
import imgui.extension.imlayout.ImLayout;
import xlight.engine.string.XStringUtil;
import xlight.engine.string.XTextBuilder;
import static imgui.ImGuiCol.ImGuiCol_Text;

public class XUITableUtil {

    public static int globalTableId = "GlobalTable".hashCode();
    static int ID;
    private static boolean beginTable;
    public static XTextBuilder stringBuilder = XStringUtil.createImpl();

    public static void start() {
        ID = 0;
    }

    private static int getID() {
        ID++;
        return ID;
    }


    public static boolean beginTable() {
        if(!beginTable) {
            stringBuilder.setLength(0);
            stringBuilder.append("lineName");
            stringBuilder.append(getID());
            int flags =  ImGuiTableFlags.ImGuiTableFlags_RowBg | ImGuiTableFlags.ImGuiTableFlags_Resizable | ImGuiTableFlags.ImGuiTableFlags_SizingStretchProp;
            String string = stringBuilder.toString();
            if(ImGuiInternal.BeginTableEx(string, globalTableId, 2, flags)) {
                beginTable = true;
                ImGui.TableSetupColumn("CO",  ImGuiTableColumnFlags.ImGuiTableColumnFlags_WidthStretch);
                ImGui.TableSetupColumn("C1", ImGuiTableColumnFlags.ImGuiTableColumnFlags_WidthStretch);
                return true;
            }
        }
        return false;
    }

    public static void endTable() {
        if(beginTable) {
            beginTable = false;
            ImGui.EndTable();
        }
    }

    public static void beginLine(String lineName) {
        beginLine(lineName, 0);
    }

    public static void beginLine(String lineName, int lineColor) {
        nextColum();
        if(!lineName.startsWith("#")) {
            int lineHash = lineName.hashCode();

            ImGuiStorage imGuiStorage = ImGui.GetStateStorage();
            imGuiStorage.SetInt(10, lineHash);

            int i = ImGui.GetID(lineHash);
            float rowHeight = imGuiStorage.GetFloat(i, 0);
            ImLayout.BeginAlign("###lineId" + lineName , ImLayout.MATCH_PARENT, rowHeight == 0 ? ImLayout.WRAP_PARENT : rowHeight, 0.0f, 0.5f);

            ImGui.Dummy(ImVec2.TMP_1.set(0, 0));
            ImGui.SameLine(0, 4);

            if(lineColor != 0) {
                ImGui.PushStyleColor(ImGuiCol_Text, lineColor);
            }
            ImGui.Text(lineName);
            if(lineColor != 0) {
                ImGui.PopStyleColor();
            }
            //        ImLayout.ShowLayoutDebug();
            //        ImLayout.ShowLayoutDebugClipping();
            ImLayout.EndAlign();
            nextColum();
        }
    }

    public static void endLine() {
        ImGuiStorage imGuiStorage = ImGui.GetStateStorage();
        int lineHash = imGuiStorage.GetInt(10, 0);
        int i = ImGui.GetID(lineHash);
        float height = ImLayout.GetTableContentHeight();
        imGuiStorage.SetFloat(i, height);
    }

    public static void nextColum() {
        ImGui.TableNextColumn();
    }
}