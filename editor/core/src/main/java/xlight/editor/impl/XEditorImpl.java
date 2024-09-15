package xlight.editor.impl;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.File;
import java.io.IOException;
import xlight.editor.assets.XEditorAssets;
import xlight.editor.core.XEditor;
import xlight.editor.core.ecs.event.XEditorEvents;
import xlight.editor.core.ecs.manager.XEditorManager;
import xlight.editor.core.ecs.manager.XProjectManager;
import xlight.editor.core.project.XProjectOptions;
import xlight.editor.imgui.ecs.manager.XImGuiManager;
import xlight.editor.imgui.ecs.manager.XImGuiWindowsManager;
import xlight.engine.core.XEngine;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.event.XEventService;
import xlight.engine.pool.XPoolController;
import xlight.engine.pool.ecs.manager.XPoolManager;

public class XEditorImpl implements XEditor {

    private XEngine editorEngine;

    public XEditorImpl() {
    }

    @Override
    public XEngine getEditorEngine() {
        return editorEngine;
    }

    @Override
    public void onSetup(XEngine engine) {
        editorEngine = engine;

        XEditorAssets.loadAssets();

        XEditorManagerImpl editorManager = new XEditorManagerImpl();
        editorEngine.getWorld().attachManager(XEditorManager.class, editorManager);
        editorEngine.getWorld().attachManager(XProjectManager.class, new XProjectManagerImpl());
        editorEngine.getWorld().attachManager(XImGuiManager.class, new XImGuiManagerImpl());
        editorEngine.getWorld().attachManager(XImGuiWindowsManager.class, new XImGuiWindowsManager());

        editorEngine.update(1); // Do a single step to attach editor data

        XWorld world = engine.getWorld();
        XEventService eventService = world.getEventService();
        eventService.addEventListener(XEditorEvents.EVENT_EDITOR_READY, event -> {
            onEditorReady(event.getWorld());
            return false;
        });
        eventService.addEventListener(XEditorEvents.EVENT_ENGINE_DISPOSED, event -> {
            XEditorAssets.disposeAssets();
            return false;
        });
    }

    private void onEditorReady(XWorld world) {
        loadBasicDemo(world);
    }

    @Deprecated
    private void loadBasicDemo(XWorld world) {
        FileHandle projectPath;
        String path = "demos/g3d/basic";
        if(Gdx.app.getType() == Application.ApplicationType.WebGL) {
            projectPath = Gdx.files.local(path);
        }
        else {
            try {
                String projectPathStr = new File("../../" + path).getCanonicalPath();
                projectPath = Gdx.files.absolute(projectPathStr);
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }

        XProjectOptions options = new XProjectOptions();
        XPoolController poolController = world.getManager(XPoolManager.class).getPoolController();
        options.loadProject(poolController, projectPath);

        XProjectManager projectManager = world.getManager(XProjectManager.class);
        projectManager.newProject(options);
    }
}