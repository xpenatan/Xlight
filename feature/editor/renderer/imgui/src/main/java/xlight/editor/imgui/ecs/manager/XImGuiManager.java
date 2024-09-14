package xlight.editor.imgui.ecs.manager;

import com.badlogic.gdx.InputMultiplexer;
import xlight.editor.imgui.window.XImGuiWindowContext;

public interface XImGuiManager {
    int FEATURE = XImGuiManager.class.hashCode();

    InputMultiplexer getImGuiInput();

    XImGuiWindowContext getCurrentWindowContext();
}
