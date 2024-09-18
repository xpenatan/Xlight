package xlight.editor.imgui.ecs.manager;

import com.badlogic.gdx.InputMultiplexer;
import imgui.ImGuiContext;
import xlight.editor.imgui.window.XImGuiWindowContext;

public interface XImGuiManager {
    int FEATURE = XImGuiManager.class.hashCode();

    InputMultiplexer getImGuiInput();

    XImGuiWindowContext getCurrentWindowContext();

    boolean containsClassID(int classID);
    boolean addWindowContext(int classID, XImGuiWindowContext windowContext);
    XImGuiWindowContext getWindowContext(int classID);
    void removeWindowContext(int classID);
    ImGuiContext getEditorContext();
}
