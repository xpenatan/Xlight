package xlight.engine.impl;

import xlight.engine.ecs.manager.XManager;
import xlight.engine.ecs.manager.XManagerData;

class XManagerDataImpl implements XManagerData {

    private XManager manager;
    private int key;

    public XManagerDataImpl(int key, XManager manager) {
        this.manager = manager;
    }

    @Override
    public XManager getManager() {
        return manager;
    }

    @Override
    public int getKey() {
        return key;
    }
}