package xlight.editor.imgui.ecs.system.game;

import com.github.xpenatan.gdx.multiview.EmuApplicationWindow;
import com.github.xpenatan.gdx.multiview.EmuInput;
import com.github.xpenatan.imgui.gdx.frame.viewport.ImGuiGdxFrameWindow;
import imgui.ImGui;
import imgui.ImGuiWindowClass;
import xlight.editor.core.ecs.manager.XEditorManager;
import xlight.editor.imgui.ecs.manager.XImGuiManager;
import xlight.editor.imgui.window.XImGuiWindowContext;
import xlight.editor.imgui.window.XMainWindow;
import xlight.engine.ecs.XECSWorld;
import xlight.engine.ecs.system.XSystem;
import xlight.engine.ecs.system.XSystemType;

public class XGameWindowSystem implements XSystem {

    public final static String name = "Game Editor";

    private ImGuiWindowClass windowClass;

    private EmuInput gameInput;
    private ImGuiGdxFrameWindow frameWindow;
    private EmuApplicationWindow appWindow;
    private XGameEditorAppListener gameAppListener;

    @Override
    public void onAttach(XECSWorld world) {
        XImGuiManager imguiManager = world.getManager(XImGuiManager.class);
        XEditorManager editorManager = world.getManager(XEditorManager.class);
        XImGuiWindowContext windowContext = imguiManager.getWindowContext(XMainWindow.CLASS_ID);
        windowClass = windowContext.getWindowClass();
        gameInput = new EmuInput(editorManager.getDefaultInput());
        appWindow = new EmuApplicationWindow(gameInput);
        frameWindow = new ImGuiGdxFrameWindow(appWindow, 400, 400, 0, 0);
        frameWindow.setName(name);
        imguiManager.getImGuiInput().addProcessor(gameInput);
        gameAppListener = new XGameEditorAppListener(world);
        appWindow.setApplicationListener(gameAppListener);
    }

    @Override
    public void onTick(XECSWorld world) {
        ImGui.SetNextWindowClass(windowClass);
        frameWindow.render();
    }

    @Override
    public XSystemType getType() {
        return XSystemType.UI;
    }
}
