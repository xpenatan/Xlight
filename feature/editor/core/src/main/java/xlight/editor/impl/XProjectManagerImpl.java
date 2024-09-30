package xlight.editor.impl;

import xlight.editor.core.ecs.event.XEditorEvent;
import xlight.editor.core.ecs.manager.XProjectManager;
import xlight.editor.core.project.XProjectOptions;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.event.XEventService;
import xlight.engine.ecs.manager.XManager;

public class XProjectManagerImpl implements XProjectManager, XManager {

    private XWorld world;

    private XProjectOptions project;

    @Override
    public void onAttach(XWorld world) {
        this.world = world;
    }

    @Override
    public XProjectOptions getProject() {
        return project;
    }

    @Override
    public void newProject(XProjectOptions options) {
        if(world != null&& options != null) {
            XEventService eventService = world.getWorldService().getEventService();
            this.project = options;
            eventService.sendEvent(XEditorEvent.EVENT_NEW_PROJECT, options);
        }
    }
}