package xlight.editor.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import imgui.ImDrawData;
import imgui.ImFontAtlas;
import imgui.ImGui;
import imgui.ImGuiCol;
import imgui.ImGuiConfigFlags;
import imgui.ImGuiDir;
import imgui.ImGuiDockNodeFlags;
import imgui.ImGuiHoveredFlags;
import imgui.ImGuiIO;
import imgui.ImGuiLoader;
import imgui.ImGuiStyle;
import imgui.ImGuiStyleVar;
import imgui.ImGuiViewport;
import imgui.ImGuiWindowClass;
import imgui.ImGuiWindowFlags;
import imgui.ImVec2;
import imgui.gdx.ImGuiGdxImpl;
import imgui.gdx.ImGuiGdxInputMultiplexer;
import xlight.editor.core.ecs.manager.XEditorManager;
import xlight.editor.imgui.ecs.manager.XImGuiManager;
import xlight.editor.imgui.window.XImGuiWindowContext;
import xlight.editor.imgui.window.XMainWindow;
import xlight.engine.ecs.XECSWorld;
import xlight.engine.ecs.event.XEvent;
import xlight.engine.ecs.event.XEventListener;
import xlight.engine.ecs.manager.XManager;
import xlight.engine.ecs.system.XSystemBeginEndListener;
import xlight.engine.ecs.system.XSystemService;
import xlight.engine.ecs.system.XSystemType;
import xlight.engine.init.ecs.service.XInitFeature;
import xlight.engine.init.ecs.service.XInitFeatureService;
import xlight.engine.list.XIntMap;
import xlight.engine.list.XIntMapListNode;
import xlight.engine.list.XList;

class XImGuiManagerImpl implements XImGuiManager, XManager, XSystemBeginEndListener {
    private XECSWorld world;
    private InputMultiplexer input;
    private XImGuiWindowContext curWindowContext;
    private ImGuiGdxImpl impl;
    private XIntMap<XImGuiWindowContext> windowContexts;
    private ImGuiWindowClass rootWindowClass;
    private boolean init = false;
    int rootDockspaceID;

    public XImGuiManagerImpl() {
        windowContexts = new XIntMap<>();
    }

    @Override
    public void onAttach(XECSWorld world) {
        this.world = world;
        XInitFeatureService featureService = world.getService(XInitFeatureService.class);
        featureService.addFeature(XImGuiManager.FEATURE, feature -> ImGuiLoader.init(() -> {
            Gdx.app.postRunnable(() -> init(world, feature));
        }));
    }

    private void init(XECSWorld world, XInitFeature feature) {
        XSystemService systemService = world.getSystemService();

        addWindowContext(XMainWindow.CLASS_ID, new XMainWindow());

        systemService.addTickListener(XSystemType.UI, this);

        initImGui(world);

        feature.initFeature();
    }

    private void initImGui(XECSWorld world) {
        XEditorManager editorManager = world.getManager(XEditorManager.class);
        XImGuiManager imguiManager = world.getManager(XImGuiManager.class);
        InputMultiplexer editorInput = editorManager.getDefaultMultiplexer();

        ImGui.CreateContext();
        ImGuiIO imGuiIO = ImGui.GetIO();
        imGuiIO.set_ConfigFlags(ImGuiConfigFlags.ImGuiConfigFlags_DockingEnable);
        imGuiIO.SetDockingFlags(false, true, false, false);
        imGuiIO.set_IniSavingRate(1f);

        impl = new ImGuiGdxImpl();
        input = new ImGuiGdxInputMultiplexer();

        ImFontAtlas fonts = ImGui.GetIO().get_Fonts();
        FileHandle fontFile01 = Gdx.files.classpath("editor/fonts/Cousine-Regular.ttf");
        FileHandle fontFile02 = Gdx.files.classpath("editor/fonts/DroidSans.ttf");

        fonts.AddFontFromMemoryTTF(fontFile02.readBytes(), 18).setName(fontFile02.name());
        fonts.AddFontFromMemoryTTF(fontFile02.readBytes(), 16).setName(fontFile02.name());
        fonts.AddFontFromMemoryTTF(fontFile02.readBytes(), 20).setName(fontFile02.name());

        fonts.AddFontFromMemoryTTF(fontFile01.readBytes(), 16).setName(fontFile01.name());
        fonts.AddFontFromMemoryTTF(fontFile01.readBytes(), 18).setName(fontFile01.name());
        fonts.AddFontFromMemoryTTF(fontFile01.readBytes(), 20).setName(fontFile01.name());

        InputMultiplexer imGuiInput = imguiManager.getImGuiInput();
        editorInput.addProcessor(imGuiInput);

        initStyle();

        init = true;

        rootWindowClass = new ImGuiWindowClass();
        rootWindowClass.set_ClassId(-9412);
        rootWindowClass.set_DockingAllowUnclassed(true);
        rootWindowClass.set_DockingAlwaysTabBar(true);
        rootWindowClass.get_ClassId();

        world.getEventService().addEventListener(XEvent.EVENT_DISPOSE, new XEventListener() {
            @Override
            public boolean onEvent(XEvent event) {
                impl.dispose();
                ImGui.DestroyContext();
                return false;
            }
        });
    }

    @Override
    public boolean containsClassID(int classID) {
        return windowContexts.contains(classID);
    }

    @Override
    public boolean addWindowContext(int classID, XImGuiWindowContext windowContext) {
        if(!containsClassID(classID)) {
            windowContexts.put(classID, windowContext);
            return true;
        }
        return false;
    }

    @Override
    public XImGuiWindowContext getWindowContext(int classID) {
        XImGuiWindowContext windowContext = windowContexts.get(classID);
        if(windowContext == null) {
            return null;
        }
        return windowContext;
    }

    @Override
    public void removeWindowContext(int classID) {
        XImGuiWindowContext window = windowContexts.remove(classID);
        if(window != null) {
            window.dispose(world);
        }
    }

    @Override
    public InputMultiplexer getImGuiInput() {
        return input;
    }

    @Override
    public XImGuiWindowContext getCurrentWindowContext() {
        return curWindowContext;
    }

    @Override
    public void onBegin(XECSWorld world) {
        if(init) {
            impl.newFrame();

            {
                int window_flags = ImGuiWindowFlags.ImGuiWindowFlags_NoDocking;
                window_flags |= ImGuiWindowFlags.ImGuiWindowFlags_NoTitleBar | ImGuiWindowFlags.ImGuiWindowFlags_NoCollapse | ImGuiWindowFlags.ImGuiWindowFlags_NoResize | ImGuiWindowFlags.ImGuiWindowFlags_NoMove;
                window_flags |= ImGuiWindowFlags.ImGuiWindowFlags_NoBringToFrontOnFocus | ImGuiWindowFlags.ImGuiWindowFlags_NoNavFocus;

                int dockspace_flags = ImGuiDockNodeFlags.ImGuiDockNodeFlags_PassthruCentralNode | ImGuiDockNodeFlags.ImGuiDockNodeFlags_AutoHideTabBar;

                if((dockspace_flags & ImGuiDockNodeFlags.ImGuiDockNodeFlags_PassthruCentralNode) > 0)
                    window_flags |= ImGuiWindowFlags.ImGuiWindowFlags_NoBackground;
                ImGuiViewport imGuiViewport = ImGui.GetMainViewport();

                ImVec2 pos = imGuiViewport.get_Pos();
                ImGui.SetNextWindowPos(pos);
                ImVec2 size = imGuiViewport.get_Size();
                ImGui.SetNextWindowSize(size);

                ImGui.PushStyleVar(ImGuiStyleVar.ImGuiStyleVar_WindowRounding, 0.0f);
                ImGui.PushStyleVar(ImGuiStyleVar.ImGuiStyleVar_WindowPadding, ImVec2.TMP_1.set(0.0f, 0.0f));
                ImGui.Begin("Root DockSpace", null, window_flags);
                ImGui.PopStyleVar(2);

                rootDockspaceID = ImGui.GetID("RootDockSpaceId");
                ImGui.DockSpace(rootDockspaceID, ImVec2.TMP_1.set(0, 0), dockspace_flags, rootWindowClass);
                ImGui.End();
            }

            XImGuiManagerImpl imGuiManager = (XImGuiManagerImpl)world.getManager(XImGuiManager.class);
            if(imGuiManager != null) {
                XList<XIntMapListNode<XImGuiWindowContext>> list = windowContexts.getNodeList();
                for(XIntMapListNode<XImGuiWindowContext> cur : list) {
                    XImGuiWindowContext window = cur.getValue();
                    imGuiManager.curWindowContext = window;
                    int key = cur.getKey();
                    window.render(world, rootDockspaceID, key);
                }
                imGuiManager.curWindowContext = null;
            }
        }
    }

    @Override
    public void onEnd(XECSWorld world) {
        if(init) {
//            ImGui.ShowDemoWindow();
//            ImGui.ShowMetricsWindow();
//            ImGui.ShowStyleEditor();

            ImGui.Render();
            ImDrawData drawData = ImGui.GetDrawData();
            impl.render(drawData);
        }
    }

    private void initStyle() {
        ImGui.StyleColorsDark();

        ImGuiStyle imGuiStyle = ImGui.GetStyle();

        imGuiStyle.set_ColorButtonPosition(ImGuiDir.ImGuiDir_Left);

        ImGui.PushStyleVar(ImGuiStyleVar.ImGuiStyleVar_CellPadding, new ImVec2(4f, 4f));
        ImGui.PushStyleVar(ImGuiStyleVar.ImGuiStyleVar_WindowPadding, ImVec2.TMP_1.set(4.0f, 2.0f));

        int flags = ImGuiHoveredFlags.ImGuiHoveredFlags_DelayNormal | ImGuiHoveredFlags.ImGuiHoveredFlags_NoSharedDelay | ImGuiHoveredFlags.ImGuiHoveredFlags_Stationary;
        imGuiStyle.set_HoverFlagsForTooltipMouse(flags);

        float hoveredAlpha = 0.15f;

        imGuiStyle.Colors(ImGuiCol.ImGuiCol_Text                   , 1.00f, 1.00f, 1.00f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_TextDisabled           , 0.50f, 0.50f, 0.50f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_WindowBg               , 0.10f, 0.10f, 0.10f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_ChildBg                , 0.10f, 0.10f, 0.10f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_PopupBg                , 0.13f, 0.14f, 0.15f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_Border                 , 0.43f, 0.43f, 0.50f, 0.50f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_BorderShadow           , 0.00f, 0.00f, 0.00f, 0.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_FrameBg                , 0.29f, 0.29f, 0.29f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_FrameBgHovered         , 1.00f, 1.00f, 1.00f, hoveredAlpha);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_FrameBgActive          , 0.33f, 0.33f, 0.33f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_TitleBg                , 0.08f, 0.08f, 0.09f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_TitleBgActive          , 0.08f, 0.08f, 0.09f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_TitleBgCollapsed       , 0.00f, 0.00f, 0.00f, 0.51f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_MenuBarBg              , 0.14f, 0.14f, 0.14f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_ScrollbarBg            , 0.02f, 0.02f, 0.02f, 0.53f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_ScrollbarGrab          , 0.31f, 0.31f, 0.31f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_ScrollbarGrabHovered   , 0.41f, 0.41f, 0.41f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_ScrollbarGrabActive    , 0.51f, 0.51f, 0.51f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_CheckMark              , 1.00f, 1.00f, 1.00f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_SliderGrab             , 0.47f, 0.47f, 0.47f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_SliderGrabActive       , 0.55f, 0.55f, 0.55f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_Button                 , 0.22f, 0.22f, 0.22f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_ButtonHovered          , 0.37f, 0.37f, 0.37f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_ButtonActive           , 0.33f, 0.33f, 0.33f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_Header                 , 0.22f, 0.22f, 0.22f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_HeaderHovered          , 1.00f, 1.00f, 1.00f, hoveredAlpha);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_HeaderActive           , 1.00f, 1.00f, 1.00f, 0.23f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_Separator              , 0.43f, 0.43f, 0.50f, 0.50f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_SeparatorHovered       , 0.37f, 0.37f, 0.37f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_SeparatorActive        , 0.33f, 0.33f, 0.33f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_ResizeGrip             , 0.33f, 0.33f, 0.33f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_ResizeGripHovered      , 0.39f, 0.39f, 0.39f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_ResizeGripActive       , 0.33f, 0.33f, 0.33f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_Tab                    , 0.08f, 0.08f, 0.09f, 0.83f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_TabHovered             , 0.33f, 0.34f, 0.36f, 0.83f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_TabActive              , 0.23f, 0.23f, 0.24f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_TabUnfocused           , 0.08f, 0.08f, 0.09f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_TabUnfocusedActive     , 0.13f, 0.14f, 0.15f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_DockingPreview         , 0.39f, 0.39f, 0.39f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_DockingEmptyBg         , 0.20f, 0.20f, 0.20f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_PlotLines              , 0.61f, 0.61f, 0.61f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_PlotLinesHovered       , 1.00f, 0.43f, 0.35f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_PlotHistogram          , 0.90f, 0.70f, 0.00f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_PlotHistogramHovered   , 1.00f, 0.60f, 0.00f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_TableHeaderBg          , 0.19f, 0.19f, 0.20f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_TableBorderStrong      , 0.31f, 0.31f, 0.35f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_TableBorderLight       , 0.23f, 0.23f, 0.25f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_TableRowBg             , 0.00f, 0.00f, 0.00f, 0.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_TableRowBgAlt          , 1.00f, 1.00f, 1.00f, 0.05f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_TextSelectedBg         , 0.47f, 0.47f, 0.47f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_DragDropTarget         , 0.78f, 0.78f, 0.78f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_NavHighlight           , 0.26f, 0.59f, 0.98f, 1.00f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_NavWindowingHighlight  , 1.00f, 1.00f, 1.00f, 0.70f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_NavWindowingDimBg      , 0.80f, 0.80f, 0.80f, 0.20f);
        imGuiStyle.Colors(ImGuiCol.ImGuiCol_ModalWindowDimBg       , 0.00f, 0.00f, 0.00f, 0.78f);
    }
}