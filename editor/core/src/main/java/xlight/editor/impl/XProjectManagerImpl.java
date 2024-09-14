package xlight.editor.impl;

import xlight.editor.core.ecs.event.XEditorEvents;
import xlight.editor.core.ecs.manager.XProjectManager;
import xlight.editor.core.project.XProjectOptions;
import xlight.engine.ecs.XECSWorld;
import xlight.engine.ecs.event.XEventService;
import xlight.engine.ecs.manager.XManager;

public class XProjectManagerImpl implements XProjectManager, XManager {

    private XECSWorld world;

    private XProjectOptions project;

    @Override
    public void onAttach(XECSWorld world) {
        this.world = world;
    }

    @Override
    public XProjectOptions getProject() {
        return project;
    }

    @Override
    public void newProject(XProjectOptions options) {
        if(world != null&& options != null) {
            XEventService eventService = world.getEventService();
            this.project = options;
            eventService.sendEvent(XEditorEvents.EVENT_NEW_PROJECT, options);
        }
    }
}