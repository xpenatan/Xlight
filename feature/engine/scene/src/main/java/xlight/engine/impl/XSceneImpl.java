package xlight.engine.impl;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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

    private String path;
    private Files.FileType fileType;

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

    public boolean loadJson(String path, Files.FileType fileType) {
        this.path = path;
        this.fileType = fileType;
        FileHandle fileHandle = Gdx.files.getFileHandle(path, fileType);
        if(fileHandle.exists()) {
            String jsonStr = fileHandle.readString();
            sceneDataMap.loadJson(jsonStr);
            boolean flag = sceneDataMap.getSize() > 0;
            return flag;
        }
        return false;
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

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Files.FileType getFileType() {
        return fileType;
    }

    public void setType(XSceneTypeValue type) {
        this.type = type;
    }

    @Override
    public void onReset() {
        id = -1;
        name = null;
        path = null;
        fileType = null;
        type = XSceneTypeValue.SCENE;
        sceneDataMap.clear();
    }
}