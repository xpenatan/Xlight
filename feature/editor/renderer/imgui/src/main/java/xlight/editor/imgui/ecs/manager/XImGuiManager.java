package xlight.editor.imgui.ecs.manager;

import com.badlogic.gdx.InputMultiplexer;
import imgui.ImGuiContext;
import xlight.editor.imgui.window.XImGuiWindowContext;
import xlight.engine.core.editor.ui.XUIDataTypeListener;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.manager.XManager;
import xlight.engine.ecs.system.XSystem;

public interface XImGuiManager {
    int FEATURE = XImGuiManager.class.hashCode();

    InputMultiplexer getImGuiInput();

    XImGuiWindowContext getCurrentWindowContext();

    boolean containsClassID(int classID);
    boolean addWindowContext(int classID, XImGuiWindowContext windowContext);
    XImGuiWindowContext getWindowContext(int classID);
    void removeWindowContext(int classID);
    ImGuiContext getEditorContext();


    void registerEntityUIListener(XUIDataTypeListener<XEntity> listener);
    XUIDataTypeListener<XEntity> getEntityUIListener();

    <T extends XComponent> void registerUIComponentListener(Class<T> type, XUIDataTypeListener<T> listener);
    <T extends XComponent> XUIDataTypeListener<T> getUIComponentListener(Class<T> type);

    <T extends XSystem> void registerUISystemListener(Class<T> type, XUIDataTypeListener<T> listener);
    <T extends XSystem> XUIDataTypeListener<T> getUISystemListener(Class<T> type);

    <T extends XManager> void registerUIManagerListener(Class<T> type, XUIDataTypeListener<T> listener);
    <T extends XManager> XUIDataTypeListener<T> getUIManagerListener(Class<T> type);
}
