package xlight.editor.impl;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import xlight.editor.core.XEditor;
import xlight.editor.core.ecs.manager.XEditorManager;
import xlight.editor.core.ecs.manager.XProjectManager;
import xlight.editor.core.project.XProjectOptions;
import xlight.engine.core.XEngine;
import xlight.engine.ecs.XECSWorld;
import xlight.engine.pool.XPoolController;
import xlight.engine.pool.ecs.manager.XPoolManager;

public class XEditorImpl implements XEditor {

    private XEngine editorEngine;

    public XEditorImpl() {
        editorEngine = XEngine.newInstance();
        XEditorManagerImpl editorManager = new XEditorManagerImpl();
        editorEngine.getWorld().attachManager(XEditorManager.class, editorManager);
        editorEngine.getWorld().attachManager(XProjectManager.class, new XProjectManagerImpl());
    }

    @Override
    public XEngine getEditorEngine() {
        return editorEngine;
    }

    @Override
    public void onSetup(XEngine engine) {
        XECSWorld world = editorEngine.getWorld();
        XProjectManager projectManager = world.getManager(XProjectManager.class);

        XProjectOptions projectOptions = vehicleDemo();
        projectManager.newProject(projectOptions);
    }


    @Deprecated
    private XProjectOptions vehicleDemo() {
        FileHandle projectPath;
        if(Gdx.app.getType() == Application.ApplicationType.WebGL) {
            projectPath = Gdx.files.local("demos/g3d/vehicle");
        }
        else {
            projectPath = Gdx.files.absolute("E:/Dev/Projects/java/Escplay/XpeEngine/demos/g3d/vehicle");
        }
        XECSWorld world = editorEngine.getWorld();

        XProjectOptions options = new XProjectOptions();
        XPoolController poolController = world.getManager(XPoolManager.class).getPoolController();
        options.loadProject(poolController, projectPath);
        return options;
    }
}