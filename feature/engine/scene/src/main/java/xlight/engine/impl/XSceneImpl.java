package xlight.engine.impl;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import xlight.engine.datamap.XDataMap;
import xlight.engine.pool.XPoolController;
import xlight.engine.pool.XPoolable;
import xlight.engine.scene.XScene;
import xlight.engine.scene.XSceneKeys;
import xlight.engine.scene.XSceneTypeValue;

class XSceneImpl implements XScene, XPoolable {

    public int id = -1;
    public String name = "";

    public XDataMap sceneDataMap;

    public XSceneTypeValue type = XSceneTypeValue.SCENE;

    private String path = "";
    private Files.FileType fileType;

    public XSceneImpl(XPoolController poolController) {
        sceneDataMap = XDataMap.obtain(poolController);
        onReset();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
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
            id = sceneDataMap.getInt(XSceneKeys.SCENE_ID.getKey(), -1);
            name = sceneDataMap.getString(XSceneKeys.NAME.getKey(), "");
            boolean flag = sceneDataMap.getSize() > 0;
            return flag;
        }
        return false;
    }

    @Override
    public String getJson() {
        sceneDataMap.put(XSceneKeys.SCENE_TYPE.getKey(), type.getValue());
        sceneDataMap.put(XSceneKeys.SCENE_ID.getKey(), id);
        sceneDataMap.put(XSceneKeys.NAME.getKey(), name);
        return sceneDataMap.saveJsonStr();
    }

    @Override
    public void clear() {
        onReset();
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

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public Files.FileType getFileType() {
        return fileType;
    }

    public void setFileType(Files.FileType type) {
        fileType = type;
    }

    @Override
    public boolean doFileExists() {
        if(fileType != null && !path.isEmpty()) {
            FileHandle fileHandle = Gdx.files.getFileHandle(path, fileType);
            return fileHandle.exists();
        }
        return false;
    }

    public void setType(XSceneTypeValue type) {
        this.type = type;
    }

    @Override
    public void onReset() {
        id = -1;
        name = "";
        path = "";
        fileType = null;
        type = XSceneTypeValue.SCENE;
        sceneDataMap.clear();
    }
}