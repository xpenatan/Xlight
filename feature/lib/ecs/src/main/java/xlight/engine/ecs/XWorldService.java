package xlight.engine.ecs;

import xlight.engine.ecs.component.XComponentService;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.ecs.event.XEventService;
import xlight.engine.ecs.system.XSystemService;

public interface XWorldService {

    XSystemService getSystemService();
    XEntityService getEntityService();
    XComponentService getComponentService();
    XEventService getEventService();
}