package xlight.engine.scene.ecs.component;

import com.badlogic.gdx.Files;
import xlight.engine.core.asset.XAssetUtil;
import xlight.engine.datamap.XDataMap;
import xlight.engine.datamap.XDataMapListener;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.pool.XPoolable;

public final class XSceneComponent implements XComponent, XDataMapListener, XPoolable {

    public int fileHandleType = XAssetUtil.getFileTypeValue(Files.FileType.Internal);
    public String scenePath = "";
    public int entityId = -1;

    public static final int MAP_ENTITY_ID = 1;
    public static final int MAP_SCENE_PATH = 2;
    public static final int MAP_FILE_TYPE = 3;

    @Override
    public void onSave(XDataMap map) {
        map.put(MAP_SCENE_PATH, scenePath);
        map.put(MAP_ENTITY_ID, entityId);
        map.put(MAP_FILE_TYPE, fileHandleType);
    }

    @Override
    public void onLoad(XDataMap map) {
        scenePath = map.getString(MAP_SCENE_PATH, "");
        entityId = map.getInt(MAP_ENTITY_ID, -1);
        fileHandleType = map.getInt(MAP_FILE_TYPE, XAssetUtil.getFileTypeValue(Files.FileType.Internal));
    }

    @Override
    public void onReset() {
        scenePath = "";
        entityId = -1;
        fileHandleType = XAssetUtil.getFileTypeValue(Files.FileType.Internal);
    }
}