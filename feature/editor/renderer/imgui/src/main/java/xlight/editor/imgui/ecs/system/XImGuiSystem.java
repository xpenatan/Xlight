package xlight.editor.imgui.ecs.system;

import xlight.editor.imgui.window.XImGuiWindowContext;

public interface XImGuiSystem {
    boolean containsClassID(int classID);
    boolean addWindowContext(int classID, XImGuiWindowContext windowContext);
    <T extends XImGuiWindowContext> T getWindowContext(int classID);
    void removeWindowContext(int classID);
}