package xlight.engine.impl;

import xlight.engine.datamap.XDataMap;
import xlight.engine.pool.XPoolController;
import xlight.engine.pool.XPoolable;
import xlight.engine.scene.XScene;
import xlight.engine.scene.XSceneTypeValue;

class XSceneImpl implements XScene, XPoolable {

    public int id = -1;
    public String name;

    public XDataMap sceneDataMap;

    public XSceneTypeValue type = XSceneTypeValue.SCENE;

    public XSceneImpl(XPoolController poolController) {
        sceneDataMap = XDataMap.obtain(poolController);
        onReset();
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean loadJson(String jsonStr) {
        sceneDataMap.loadJson(jsonStr);
        boolean flag = sceneDataMap.getSize() > 0;
        return flag;
    }

    @Override
    public String getJson() {
        return sceneDataMap.saveJsonStr();
    }

    @Override
    public void clear() {
        sceneDataMap.clear();
    }

    @Override
    public XDataMap getSceneDataMap() {
        return sceneDataMap;
    }

    @Override
    public XSceneTypeValue getType() {
        return type;
    }

    public void setType(XSceneTypeValue type) {
        this.type = type;
    }

    @Override
    public void onReset() {
        id = -1;
        name = null;
        type = XSceneTypeValue.SCENE;
        sceneDataMap.clear();
    }
}