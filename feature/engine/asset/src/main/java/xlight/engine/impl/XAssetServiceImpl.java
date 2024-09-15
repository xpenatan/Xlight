package xlight.engine.impl;

import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.service.XService;

class XAssetServiceImpl implements XService {

    private XAssetManagerImpl assetManager;

    XAssetServiceImpl(XAssetManagerImpl assetManager) {
        this.assetManager = assetManager;
    }

    @Override
    public void onAttach(XWorld world) {

    }

    @Override
    public void onDetach(XWorld world) {

    }

    @Override
    public void onTick(XWorld world) {
        assetManager.update();
    }
}