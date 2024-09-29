package xlight.engine.imgui.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import imgui.ImGui;
import imgui.ImGuiCol;
import imgui.ImGuiStyleVar;
import imgui.ImVec2;
import imgui.extension.imlayout.ImGuiCollapseLayoutOptions;
import imgui.extension.imlayout.ImLayout;
import imgui.idl.helper.IDLBoolArray;

public class XCollapseWidget {

    public static ImGuiCollapseLayoutOptions defaultOptions = new ImGuiCollapseLayoutOptions();

    public static final int MAX_TEXTURES = 4;

    private static CollaspeWidgetData collaspeWidgetData = new CollaspeWidgetData();

    private static Texture[] texturesArray = new Texture[MAX_TEXTURES];

    public static int WHITE_COLOR = Color.WHITE.toIntBits();

    public static Texture[] getTexturesArray() {
        texturesArray[0] = null;
        texturesArray[1] = null;
        texturesArray[2] = null;
        texturesArray[3] = null;
        return texturesArray;
    }

    private static int prepareContent(String id, Texture[] textures) {
        return prepareContent(id.hashCode(), textures);
    }

    private static int prepareContent(int id, Texture[] textures) {
        int buttonClickedIndex = -1;

        if(textures != null && textures.length > 0 && textures[0] != null) {
            ImLayout.BeginAlign("##align", ImLayout.MATCH_PARENT, ImLayout.MATCH_PARENT, 1.0f, 0.5f, -3, 0);

            float size = (int)(19 * ImLayout.GetDPIScale());;

            int padding = 0;
            ImGui.PushStyleVar(ImGuiStyleVar.ImGuiStyleVar_FramePadding, ImVec2.TMP_1.set(padding, padding));
            for(int i = textures.length - 1; i >= 0; i--) {
                Texture texture = textures[i];
                if(texture == null)
                    continue;
                ImGui.PushID(id + i);
                if(ImGui.ImageButton("##imageBtn", texture.getTextureObjectHandle(), ImVec2.TMP_1.set(size, size), ImVec2.TMP_2.set(0, 0), ImVec2.TMP_3.set(1, 1)))
                    buttonClickedIndex = i;
                ImGui.PopID();

                if(i > 0)
                    ImGui.SameLine(0, 1);
            }
            ImGui.PopStyleVar();
            ImLayout.EndAlign();
        }

        return buttonClickedIndex;
    }

    public static CollaspeWidgetData begin(String id, String title) {
        texturesArray[0] = null;
        return begin(id, title, texturesArray);
    }

    public static CollaspeWidgetData begin(String id, String title, Texture texture01) {
        texturesArray[0] = texture01;
        return begin(id, title, texturesArray);
    }

    public static CollaspeWidgetData begin(String id, String title, Texture texture01, Texture texture02) {
        texturesArray[0] = texture01;
        texturesArray[1] = texture02;
        return begin(id, title, texturesArray);
    }

    public static CollaspeWidgetData begin(String id, String title, Texture texture01, Texture texture02, Texture texture03) {
        texturesArray[0] = texture01;
        texturesArray[1] = texture02;
        texturesArray[2] = texture03;
        return begin(id, title, texturesArray);
    }

    public static CollaspeWidgetData begin(String id, String title, Texture texture01, Texture texture02, Texture texture03, Texture texture04) {
        texturesArray[0] = texture01;
        texturesArray[1] = texture02;
        texturesArray[2] = texture03;
        texturesArray[3] = texture04;
        return begin(id, title, texturesArray);
    }

    public static CollaspeWidgetData begin(String id, String title, Texture[] textures) {
        return begin(id.hashCode(), title, Color.WHITE.toIntBits(), textures, defaultOptions);
    }

    public static CollaspeWidgetData begin(String id, String title, Texture[] textures, ImGuiCollapseLayoutOptions options) {
        return begin(id.hashCode(), title, Color.WHITE.toIntBits(), textures, options);
    }

    public static CollaspeWidgetData begin(int id, String title, int titleColor, Texture[] textures, ImGuiCollapseLayoutOptions options) {
        ImGui.PushStyleColor(ImGuiCol.ImGuiCol_Text, titleColor);
        boolean isOpen = ImLayout.BeginCollapseLayoutEx(id, title, ImLayout.MATCH_PARENT, ImLayout.WRAP_PARENT, options);
        ImGui.PopStyleColor();

        int buttonClickedIndex = prepareContent(id, textures);

        ImLayout.EndCollapseFrameLayout();

        collaspeWidgetData.isOpen = isOpen;
        collaspeWidgetData.buttonIndex = buttonClickedIndex;

        return collaspeWidgetData;
    }

    public static CollaspeWidgetData begin(String id, IDLBoolArray isOpen, String title) {
        texturesArray[0] = null;
        return begin(id, isOpen, title, texturesArray);
    }

    public static CollaspeWidgetData begin(String id, IDLBoolArray isOpen, String title, Texture texture) {
        texturesArray[0] = texture;
        return begin(id, isOpen, title, texturesArray);
    }

    public static CollaspeWidgetData begin(String id, IDLBoolArray isOpen, String title, Texture[] textures) {
        ImLayout.BeginCollapseLayoutEx(id, isOpen, title, ImLayout.MATCH_PARENT, ImLayout.WRAP_PARENT);

        int buttonClickedIndex = prepareContent(id, textures);

        ImLayout.EndCollapseFrameLayout();

        collaspeWidgetData.isOpen = isOpen.getValue(0);
        collaspeWidgetData.buttonIndex = buttonClickedIndex;

        return collaspeWidgetData;
    }

    public static void end() {
        ImLayout.EndCollapseLayout();
    }

    public static class CollaspeWidgetData {
        public boolean isOpen;
        public int buttonIndex;
    }
}
