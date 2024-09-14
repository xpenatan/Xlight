package xlight.engine.impl;

import xlight.engine.ecs.manager.XManager;
import xlight.engine.json.XJson;
import xlight.engine.json.ecs.manager.XJsonManager;

class XJsonManagerImpl implements XJsonManager, XManager {

    XJson json;

    public XJsonManagerImpl() {
        json = XJson.create();
    }

    @Override
    public XJson getJson() {
        return json;
    }
}