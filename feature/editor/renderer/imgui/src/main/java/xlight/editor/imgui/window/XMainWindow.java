package xlight.editor.imgui.window;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import imgui.ImGui;
import imgui.ImGuiCol;
import imgui.ImGuiDockNodeFlags;
import imgui.ImGuiDockNodeFlagsPrivate_;
import imgui.ImGuiInputTextFlags;
import imgui.ImGuiMouseButton;
import imgui.ImGuiString;
import imgui.ImGuiStyleVar;
import imgui.ImGuiWindowClass;
import imgui.ImGuiWindowFlags;
import imgui.ImVec2;
import imgui.extension.imlayout.ImLayout;
import xlight.editor.assets.XEditorAssets;
import xlight.editor.core.ecs.manager.XEditorManager;
import xlight.editor.core.ecs.manager.XProjectManager;
import xlight.editor.imgui.util.XImGuiPopUpWidget;
import xlight.engine.core.XEngine;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.scene.ecs.manager.XSceneManager;

public class XMainWindow extends XImGuiWindowContext {

    public static final int CLASS_ID = 1;

    private static final String BIG_MENU = "Big Menu";

    private static final String MENU_FILE = "File";
    private static final String MENU_FILE_NEW_PROJECT = "New Project";
    private static final String MENU_FILE_OPEN_PROJECT = "Open Project";
    private static final String MENU_FILE_SAVE_SCENE_AS = "Save Scene As..";
    private static final String MENU_FILE_OPEN_SCENE = "Open Scene";
    private static final String MENU_FILE_DISPOSE = "Dispose Engine";

    private static final String MENU_BUILD = "Build";
    private static final String MENU_BUILD_GENERATOR_WEB = "Web";
    private static final String MENU_BUILD_GENERATOR_ANDROID = "Android";
    private static final String MENU_BUILD_GENERATOR_IOS = "IOS";
    private static final String MENU_BUILD_GENERATOR_DESKTOP = "Desktop";

    private static final String MENU_VIEW = "View";
    private static final String MENU_VIEW_DEBUG = "Debug";
    private static final String MENU_VIEW_DEBUG_ENTITY_POSITION = "Entity";
    private static final String MENU_VIEW_DEBUG_AABB_TREE = "AABBTree";
    private static final String MENU_VIEW_DEBUG_WIREFRAME = "Wireframe";
    private static final String MENU_VIEW_DEBUG_ACTIVE_CAMERA = "Active Camera";

    private static final String POPUP_SAVE = "PopupMenuSave";
    private static final String POPUP_LOAD = "PopupMenuLoad";
    private static final String POPUP_LOAD_PATH = "PopupLoadPath";
    private static final String POPUP_SAVE_PATH = "PopupSavePath";

    public static int MENU_BAR_BIG_SIZE;
    private static int BTN_COLOR_SELECTED = Color.toIntBits(110, 110, 110, 255);

    private boolean first = true;
    private XEditorManager editorManager;

    private ImGuiWindowClass menuWindowClass;

    private ImGuiString pathString;

    public XMainWindow() {
        super("Main");
        isVisible = true;

        pathString = new ImGuiString();
    }

    @Override
    protected void onAdd(XWorld world, int windowClassID) {

    }

    @Override
    protected void onRemove(XWorld world) {

    }

    @Override
    protected void onRender(XWorld world, int rootDockspaceID) {
        if(first) {
            layout();
        }
    }

    @Override
    protected void OnRenderDockspace(XWorld world) {
        if(editorManager == null) {
            editorManager = world.getManager(XEditorManager.class);
        }

        float frameHeight = ImGui.GetFrameHeight();

        MENU_BAR_BIG_SIZE = (int)(frameHeight + (5 * ImLayout.GetDPIScale()));

        if(ImGui.BeginMenuBar()) {
            ImVec2 imVec2 = ImGui.GetWindowSize();
            if(ImGui.BeginMenu(MENU_FILE)) {
                if(ImGui.MenuItem(MENU_FILE_NEW_PROJECT)) {
                    XProjectManager projectManager = world.getManager(XProjectManager.class);
                    projectManager.newProject(null);
                }
                ImGui.EndMenu();
            }

            if(ImGui.BeginMenu(MENU_BUILD)) {

                ImGui.EndMenu();
            }

            if(ImGui.BeginMenu(MENU_VIEW)) {

                ImGui.EndMenu();
            }

            ImGui.EndMenuBar();
        }

        if(menuWindowClass == null) {
            menuWindowClass = new ImGuiWindowClass();
            menuWindowClass.set_ClassId(-9563);
            menuWindowClass.set_DockingAllowUnclassed(false);
            menuWindowClass.set_DockingAlwaysTabBar(false);
            menuWindowClass.set_DockNodeFlagsOverrideSet(ImGuiDockNodeFlags.ImGuiDockNodeFlags_NoUndocking | ImGuiDockNodeFlagsPrivate_.ImGuiDockNodeFlags_NoTabBar);
        }
        int bigMenuDockspaceId = ImGui.GetID("ChildBigMenuDockSpaceId");
        ImGui.PushStyleVar(ImGuiStyleVar.ImGuiStyleVar_WindowPadding, ImVec2.TMP_1.set(0, 0));
        ImGui.DockSpace(bigMenuDockspaceId, ImVec2.TMP_1.set(0, MENU_BAR_BIG_SIZE), 0, menuWindowClass);

        XEngine gameEngine = editorManager.getGameEngine();

        int flags = ImGuiWindowFlags.ImGuiWindowFlags_NoMove;
        flags = flags | ImGuiWindowFlags.ImGuiWindowFlags_NoTitleBar;
        flags = flags | ImGuiWindowFlags.ImGuiWindowFlags_NoResize;
        flags = flags | ImGuiWindowFlags.ImGuiWindowFlags_NoCollapse;
        flags = flags | ImGuiWindowFlags.ImGuiWindowFlags_NoScrollbar;
        ImGui.SetNextWindowClass(menuWindowClass);
        ImGui.SetNextWindowDockID(bigMenuDockspaceId);
        ImGui.Begin(BIG_MENU, null, flags);

        ImLayout.BeginAlign("##BigMenuAlign", ImLayout.MATCH_PARENT, MENU_BAR_BIG_SIZE, 0.5f);
        if(gameEngine != null) {
            renderImGuiButtons(gameEngine.getWorld());
        }
        ImLayout.EndAlign();

        ImGui.End();
        ImGui.PopStyleVar();
    }

    private void renderImGuiButtons(XWorld gameWorld) {
        int padding = (int)(6 * ImLayout.GetDPIScale());
        boolean startHoldingStep = false;
        boolean isPlay = false;

        int colorPlay = isPlay ? BTN_COLOR_SELECTED : 0;
        int colorStop = startHoldingStep || isPlay ? 0 : BTN_COLOR_SELECTED;

        int buttonSize = MENU_BAR_BIG_SIZE - padding * 2;

        ImGui.PushStyleVar(ImGuiStyleVar.ImGuiStyleVar_FramePadding, ImVec2.TMP_1.set(padding, padding));

        if(colorStop != 0) {
            ImGui.PushStyleColor(ImGuiCol.ImGuiCol_Button, colorStop);
        }
        if(renderMenuButton(XEditorAssets.stopTexture, "##stopTexture", buttonSize)) {
            // TODO call stop
        }
        if(colorStop != 0) {
            ImGui.PopStyleColor();
        }
        ImGui.SameLine();

        if(colorPlay != 0) {
            ImGui.PushStyleColor(ImGuiCol.ImGuiCol_Button, colorPlay);
        }
        if(renderMenuButton(XEditorAssets.playTexture, "##playTexture", buttonSize)) {
            // TODO call play
        }
        if(colorPlay != 0) {
            ImGui.PopStyleColor();
        }
        ImGui.SameLine();
        boolean isCtrl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT);

        if(startHoldingStep) {
            ImGui.PushStyleColor(ImGuiCol.ImGuiCol_Button, BTN_COLOR_SELECTED);
        }
        ImGui.SameLine();
        if(renderMenuButton(XEditorAssets.playStepTexture, "##playStepTexture", buttonSize)) {
            // TODO play step
        }
        if(startHoldingStep) {
            ImGui.PopStyleColor();
        }

        ImGui.SameLine();
        if(renderMenuButton(XEditorAssets.saveTexture, "##saveTexture", buttonSize)) {
            XSceneManager sceneManager = gameWorld.getManager(XSceneManager.class);
            sceneManager.save();
        }

        if(ImGui.IsItemClicked(ImGuiMouseButton.ImGuiMouseButton_Right)) {
            ImGui.OpenPopup(POPUP_SAVE);
        }

        ImGui.SameLine();

        if(renderMenuButton(XEditorAssets.loadTexture, "##loadTexture", buttonSize)) {
            XSceneManager sceneManager = gameWorld.getManager(XSceneManager.class);
            sceneManager.load();
        }

        if(ImGui.IsItemClicked(ImGuiMouseButton.ImGuiMouseButton_Right)) {
            ImGui.OpenPopup(POPUP_LOAD);
        }
        ImGui.PopStyleVar();

        boolean loadScenePath = false;
        if(ImGui.BeginPopup(POPUP_LOAD)) {
            if(ImGui.MenuItem("Load from file")) {
                ImGui.CloseCurrentPopup();
                loadScenePath = true;
            }
            ImGui.EndPopup();
        }
        if(loadScenePath) {
            ImGui.OpenPopup(POPUP_LOAD_PATH);
        }


        boolean saveScenePath = false;
        if(ImGui.BeginPopup(POPUP_SAVE)) {
            if(ImGui.MenuItem("Save to file")) {
                ImGui.CloseCurrentPopup();
                saveScenePath = true;
            }
            ImGui.EndPopup();
        }

        if(saveScenePath) {
            ImGui.OpenPopup(POPUP_SAVE_PATH);
        }

        int menuFlag = ImGuiWindowFlags.ImGuiWindowFlags_NoResize | ImGuiWindowFlags.ImGuiWindowFlags_NoSavedSettings;
        if(ImGui.BeginPopup(POPUP_LOAD_PATH, menuFlag)) {
            if(XImGuiPopUpWidget.renderSceneSavePath(pathString)) {
                String newPath = pathString.getValue().trim();
                pathString.clear();
                if(!newPath.isEmpty()) {
                    XSceneManager sceneManager = gameWorld.getManager(XSceneManager.class);
                    sceneManager.loadToCurrentScene(newPath, Files.FileType.Local);
                }
                ImGui.CloseCurrentPopup();
            }
            ImGui.EndPopup();
        }

        if(ImGui.BeginPopup(POPUP_SAVE_PATH, menuFlag)) {
            if(XImGuiPopUpWidget.renderSceneSavePath(pathString)) {
                String newPath = pathString.getValue().trim();
                pathString.clear();
                if(!newPath.isEmpty()) {
                    FileHandle path = Gdx.files.local(newPath);
                    XSceneManager sceneManager = gameWorld.getManager(XSceneManager.class);
                    String jsonStr = sceneManager.saveCurrentScene();
                    path.writeString(jsonStr, false);
                }
                ImGui.CloseCurrentPopup();
            }
            ImGui.EndPopup();
        }
    }

    private boolean renderMenuButton(Texture texture, String id, int buttonSize) {
        if(ImGui.ImageButton(id, texture.getTextureObjectHandle(), ImVec2.TMP_1.set(buttonSize, buttonSize), ImVec2.TMP_2.set(0, 0), ImVec2.TMP_3.set(1, 1))) {
            return true;
        }
        return false;
    }

    private void layout() {
//        int dockspace_flags =  0;
//        ImGuiWindow editorWindow = ImGuiInternal.FindWindowByName(EditorWindowEntity.WINDOW_GAME_EDITOR);
//        ImGuiWindow guiWindow = ImGuiInternal.FindWindowByName(EditorWindowEntity.WINDOW_GUI_EDITOR);
//
//        if(editorWindow != null && guiWindow != null) {
//            ImGuiViewport imGuiViewport = ImGui.GetMainViewport();
//            first = false;
//            ImGuiInternal.DockBuilderRemoveNode(dockspaceId); // clear any previous layout
//            ImGuiInternal.DockBuilderAddNode(dockspaceId, dockspace_flags | 1 << 10);
//            ImGuiInternal.DockBuilderSetNodeSize(dockspaceId, imGuiViewport.get_Size());
//
//            // Split the main dockspace node into left and right nodes
//            IDLInt.TMP_1.set(dockspaceId);
//            int rightId = ImGuiInternal.DockBuilderSplitNode(IDLInt.TMP_1.getValue(), ImGuiDir.ImGuiDir_Right, 0.3f, null, IDLInt.TMP_1);
//            int assetId = ImGuiInternal.DockBuilderSplitNode(IDLInt.TMP_1.getValue(), ImGuiDir.ImGuiDir_Down, 0.3f, null, IDLInt.TMP_1);
//            int centralNodeId = IDLInt.TMP_1.getValue();
//
//            ImGuiInternal.DockBuilderSplitNode(centralNodeId, ImGuiDir.ImGuiDir_Left, 0.47f, IDLInt.TMP_1, IDLInt.TMP_2);
//            int topLeft = IDLInt.TMP_1.getValue();
//            int topRight = IDLInt.TMP_2.getValue();
//
//            IDLInt.TMP_1.set(rightId);
//            int rightTopId = ImGuiInternal.DockBuilderSplitNode(IDLInt.TMP_1.getValue(), ImGuiDir.ImGuiDir_Up, 0.5f, null, IDLInt.TMP_1);
//            int rightBottomId = IDLInt.TMP_1.getValue();
//
//            ImGuiInternal.DockBuilderDockWindow(EditorWindowEntity.WINDOW_GUI_EDITOR, topRight);
//            ImGuiInternal.DockBuilderDockWindow(EditorWindowEntity.WINDOW_GAME_EDITOR, topRight);
//            ImGuiInternal.DockBuilderDockWindow(XGameWindowSystemImpl.WINDOW_GAME, topLeft);
//            ImGuiInternal.DockBuilderDockWindow(XHierarchyWindowSystemImpl.name, rightTopId);
//            ImGuiInternal.DockBuilderDockWindow(XInspectorWindowSystemImpl.name, rightBottomId);
//            ImGuiInternal.DockBuilderDockWindow(XContentBrowserWindowSystemImpl.name, assetId);
//            ImGuiInternal.DockBuilderFinish(dockspaceId);
//
//            int i = ImGuiInternal.ImHashStr(EditorWindowEntity.WINDOW_GAME_EDITOR);
//
//            ImGuiInternal.DockBuilderGetNode(topRight).set_SelectedTabId(ImGuiInternal.ImHashStr("#TAB", 0, i));
//        }
    }
}