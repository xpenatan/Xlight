package xlight.engine.impl;

import com.badlogic.gdx.assets.AssetManager;
import xlight.engine.asset.ecs.manager.XAssetManager;
import xlight.engine.ecs.XECSWorld;
import xlight.engine.ecs.manager.XManager;

public class XAssetManagerImpl implements XAssetManager, XManager {

    AssetManager assetManager;

    public XAssetManagerImpl() {
        assetManager = new AssetManager();
    }

    @Override
    public void onAttach(XECSWorld world) {

    }

    @Override
    public void onDetach(XECSWorld world) {

    }

    public void update() {
        assetManager.update();
    }
}