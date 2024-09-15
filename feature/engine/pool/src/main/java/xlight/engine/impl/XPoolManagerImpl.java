package xlight.engine.impl;

import xlight.engine.datamap.pool.XDataMapPoolUtil;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.manager.XManager;
import xlight.engine.json.XJson;
import xlight.engine.json.ecs.manager.XJsonManager;
import xlight.engine.lang.pool.XPrimitivePoolUtil;
import xlight.engine.list.pool.XListPoolUtil;
import xlight.engine.pool.XPoolController;
import xlight.engine.pool.ecs.manager.XPoolManager;
import xlight.engine.properties.pool.XPropertiesPoolUtil;

class XPoolManagerImpl implements XPoolManager, XManager {

    private XPoolControllerImpl poolController;

    public XPoolManagerImpl() {
        poolController = new XPoolControllerImpl();
    }

    @Override
    public XPoolController getPoolController() {
        return poolController;
    }

    @Override
    public void onAttach(XWorld world) {
        XJson json = world.getManager(XJsonManager.class).getJson();
        XListPoolUtil.registerPool(poolController);
        XPrimitivePoolUtil.registerPool(poolController);
        XDataMapPoolUtil.registerPool(json, poolController);
        XPropertiesPoolUtil.registerPool(json, poolController);
    }
}