package xlight.engine.impl;

import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.XWorldService;
import xlight.engine.ecs.component.XComponentService;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.ecs.event.XEventService;
import xlight.engine.ecs.system.XSystemService;

class XWorldServiceImpl implements XWorldService {
    private XECSWorldImpl world;
    XComponentServiceImpl componentService;
    XEntityServiceImpl entityService;
    XSystemServiceImpl systemService;
    XEventServiceImpl eventService;

    public XWorldServiceImpl(XECSWorldImpl world) {
        this.world = world;

        entityService = new XEntityServiceImpl(world);
        componentService = new XComponentServiceImpl();
        systemService = new XSystemServiceImpl(world);
        eventService = new XEventServiceImpl(world);
    }

    public void init() {
        entityService.init(world);
        componentService.init(world, entityService);
    }

    @Override
    public XSystemService getSystemService() {
        return systemService;
    }

    @Override
    public XEntityService getEntityService() {
        return entityService;
    }

    @Override
    public XComponentService getComponentService() {
        return componentService;
    }

    @Override
    public XEventService getEventService() {
        return eventService;
    }
}