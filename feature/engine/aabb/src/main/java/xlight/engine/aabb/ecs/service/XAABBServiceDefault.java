package xlight.engine.aabb.ecs.service;

import xlight.engine.aabb.XAABBTree;
import xlight.engine.aabb.XAABBTreeDefault;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.service.XService;

public class XAABBServiceDefault implements XAABBService, XService {

    XAABBTree gameTree;

    public XAABBServiceDefault() {
        gameTree = new XAABBTreeDefault();
    }


    @Override
    public void onTick(XWorld world) {
        gameTree.update();
    }

    @Override
    public XAABBTree getGameTree() {
        return gameTree;
    }
}