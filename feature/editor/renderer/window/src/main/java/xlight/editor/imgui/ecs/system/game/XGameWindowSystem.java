package xlight.editor.imgui.ecs.system.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.github.xpenatan.gdx.multiview.EmuApplicationWindow;
import com.github.xpenatan.gdx.multiview.EmuInput;
import com.github.xpenatan.imgui.gdx.frame.viewport.ImGuiGdxFrameWindow;
import imgui.ImGui;
import imgui.ImGuiWindowClass;
import xlight.editor.core.ecs.event.XEditorEvent;
import xlight.editor.core.ecs.manager.XEditorManager;
import xlight.editor.imgui.ecs.manager.XImGuiManager;
import xlight.editor.imgui.window.XImGuiWindowContext;
import xlight.editor.imgui.window.XMainWindow;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.system.XSystem;
import xlight.engine.ecs.system.XSystemData;
import xlight.engine.ecs.system.XSystemType;

public class XGameWindowSystem implements XSystem {

    public final static String name = "Game Editor";

    private ImGuiWindowClass windowClass;

    private EmuInput gameInput;
    private ImGuiGdxFrameWindow frameWindow;
    private EmuApplicationWindow appWindow;
    private XGameEditorAppListener gameAppListener;

    @Override
    public void onAttach(XWorld world, XSystemData systemData) {
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
    public void onTick(XWorld world) {
        ImGui.SetNextWindowClass(windowClass);
        frameWindow.render();

        boolean copyEntities = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.C);
        boolean pasteEntity = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.V);

        if(copyEntities) {
            world.getWorldService().getEventService().sendEvent(XEditorEvent.EVENT_EDITOR_COPY_ENTITY);
        }
        else if(pasteEntity) {
            world.getWorldService().getEventService().sendEvent(XEditorEvent.EVENT_EDITOR_PASTE_ENTITY);
        }
    }

    @Override
    public XSystemType getType() {
        return XSystemType.UI;
    }
}
