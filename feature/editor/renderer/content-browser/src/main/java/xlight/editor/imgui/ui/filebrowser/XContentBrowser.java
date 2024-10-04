package xlight.editor.imgui.ui.filebrowser;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import imgui.ImGui;
import imgui.ImGuiTableFlags;
import imgui.ImVec2;
import imgui.extension.imlayout.ImGuiLayout;
import imgui.extension.imlayout.ImLayout;
import imgui.extension.imlayout.ImOrientation;
import xlight.editor.assets.XEditorAssets;
import xlight.editor.core.ecs.event.XEditorEvent;
import xlight.editor.core.ecs.manager.XEditorManager;
import xlight.editor.core.ecs.manager.XProjectManager;
import xlight.editor.core.project.XProjectOptions;
import xlight.engine.core.XEngine;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.event.XEvent;
import xlight.engine.ecs.event.XEventListener;
import xlight.engine.ecs.event.XEventService;
import xlight.engine.imgui.ui.filebrowser.XFile;
import xlight.engine.imgui.ui.filebrowser.XFileBrowserRenderer;
import xlight.engine.imgui.ui.filebrowser.XFileManager;
import xlight.engine.imgui.ui.filebrowser.XFileOpenListener;
import xlight.engine.imgui.ui.filebrowser.XTreeBrowserRenderer;
import xlight.engine.math.XColor;
import xlight.engine.scene.ecs.manager.XSceneManager;
import static imgui.ImGuiTableColumnFlags.ImGuiTableColumnFlags_WidthStretch;
import static imgui.ImGuiTableFlags.ImGuiTableFlags_Resizable;

public class XContentBrowser {

    private XFileManager fileManager;
    private XFileBrowserRenderer fileBrowserRenderer;
    private XTreeBrowserRenderer treeBrowser;

    int menuColor = XColor.toABGRIntBits(255, 255, 255, 15);

    public XContentBrowser() {
        fileManager = new XFileManager();
        fileBrowserRenderer = new XFileBrowserRenderer(XEditorAssets.img_folderTexture, XEditorAssets.img_fileTexture);
        treeBrowser = new XTreeBrowserRenderer(XEditorAssets.ic_folderTexture);
    }

    public void init(XWorld editorWorld) {
        XEventService eventService = editorWorld.getWorldService().getEventService();
        XEditorManager editorManager = editorWorld.getManager(XEditorManager.class);
        XProjectManager projectManager = editorWorld.getManager(XProjectManager.class);

        eventService.addEventListener(XEditorEvent.EVENT_ENGINE_CREATED, new XEventListener() {
            @Override
            public boolean onEvent(XEvent event) {
                XProjectOptions project = projectManager.getProject();
                String projectAssetPath = project.getProjectAssetPath();
                System.out.println("projectAssetPath: " + projectAssetPath);
                updateRoot(projectAssetPath);
                return false;
            }
        });
        eventService.addEventListener(XEditorEvent.EVENT_ENGINE_DISPOSED, new XEventListener() {
            @Override
            public boolean onEvent(XEvent event) {
                return false;
            }
        });

        fileBrowserRenderer.setFileOpenListener(new XFileOpenListener() {
            @Override
            public boolean allowFile(XFile file) {
                if(file.fileHandle != null) {
                    String ext = file.fileHandle.extension();
                    if(ext.equals("xscene")) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void onOpenFile(XFile file) {
                Gdx.app.postRunnable(() -> {
                    if(file.fileHandle != null) {
                        String path = file.getPath();
                        // TODO remove assets_raw ?
                        path = path.replace("assets_raw/", "");
                        String ext = file.fileHandle.extension();
                        if(ext.equals("xscene")) {
                            XEngine gameEngine = editorManager.getGameEngine();
                            if(gameEngine != null) {
                                XWorld gameWorld = gameEngine.getWorld();
                                XSceneManager sceneManager = gameWorld.getManager(XSceneManager.class);
                                sceneManager.loadFromFile(path, Files.FileType.Local);
                            }
                        }
                    }
                });
            }
        });
    }

    private void updateRoot(String value) {
        FileHandle fileHandle = null;
        if(Gdx.app.getType() == Application.ApplicationType.WebGL) {
            fileHandle = Gdx.files.local(value);
            fileHandle.mkdirs();
        }
        else {
            fileHandle = Gdx.files.absolute(value);
        }

        if(fileHandle.exists()) {
            XFile file = fileManager.createFile(fileHandle);
            fileManager.updatedRoot(file);
        }
    }

    public void render() {
        int flags = ImGuiTableFlags_Resizable | ImGuiTableFlags.ImGuiTableFlags_ScrollY;
        if(ImGui.BeginTable("ContentBrowser", 2, flags)) {

            ImGui.TableSetupColumn("C0", ImGuiTableColumnFlags_WidthStretch, 0.2f);
            ImGui.TableSetupColumn("C1", ImGuiTableColumnFlags_WidthStretch, 0.8f);

            ImGui.TableNextRow();
            ImGui.TableSetColumnIndex(0);
            treeBrowser.render(fileManager);
            ImGui.TableSetColumnIndex(1);
            renderFileBrowserRenderer();
            ImGui.EndTable();
        }
    }

    private void renderFileBrowserRenderer() {
        XFile currentFolder = fileManager.currentFolder;
        if(currentFolder == null || !currentFolder.isDirectory()) {
            return;
        }

        ImLayout.BeginLayout("menu", ImLayout.MATCH_PARENT, 22);
        {
            ImLayout.SetOrientation(ImOrientation.HORIZONTAL);
            ImGuiLayout imGuiLayout1 = ImLayout.GetCurrentLayout();
            ImVec2 cursorPos = ImGui.GetCursorPos();
            ImVec2 imVec2 = ImGui.GetContentRegionAvail();
            ImGui.GetWindowDrawList().AddRectFilled(imGuiLayout1.get_position(), imGuiLayout1.getAbsoluteSize(), menuColor);

            if(ImGui.SmallButton("Refresh")) {
                fileManager.refreshCurrentFolder();
            }

            ImGui.SameLine();

            {
                //            ImLayout.ShowLayoutDebug();
                ImGui.Dummy(ImVec2.TMP_1.set(0,0));
                ImGui.SameLine(0, 4);
                ImLayout.BeginAlign("path", ImLayout.WRAP_PARENT, ImLayout.MATCH_PARENT, 0, 0.5f);
                ImGui.Text(fileManager.currentFolder.getPath());
                ImLayout.EndLayout();
            }

            ImGui.SameLine();
            ImLayout.BeginAlign("gear", ImLayout.MATCH_PARENT, ImLayout.MATCH_PARENT, 1.0f, 0.0f);
            {
//                ImLayout.ShowLayoutDebug();
                int gearTexture = XEditorAssets.ic_gearTexture.getTextureObjectHandle();
                if(ImGui.ImageButton("ContentGear", gearTexture, ImVec2.TMP_1.set(16, 16))) {

                }
            }
            ImLayout.EndLayout();
        }
        ImLayout.EndLayout();

        fileBrowserRenderer.render(fileManager);
    }
}