package xlight.engine.impl;

import xlight.engine.ecs.XECSWorld;
import xlight.engine.ecs.service.XService;

class XAssetServiceImpl implements XService {

    private XAssetManagerImpl assetManager;

    XAssetServiceImpl(XAssetManagerImpl assetManager) {
        this.assetManager = assetManager;
    }

    @Override
    public void onAttach(XECSWorld world) {

    }

    @Override
    public void onDetach(XECSWorld world) {

    }

    @Override
    public void onTick(XECSWorld world) {
        assetManager.update();
    }
}