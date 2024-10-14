package xlight.engine.g3d.ecs.component;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.BoundingBox;
import net.mgsx.gltf.loaders.glb.GLBLoader;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import xlight.engine.core.asset.XAssetUtil;
import xlight.engine.core.editor.ui.XDragDropTarget;
import xlight.engine.core.editor.ui.XUIData;
import xlight.engine.core.editor.ui.XUIDataListener;
import xlight.engine.core.editor.ui.options.XUIOpStringEditText;
import xlight.engine.datamap.XDataMap;
import xlight.engine.datamap.XDataMapListener;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.g3d.XBatch3D;

public class XGLTFComponent extends XRender3DComponent implements XUIDataListener, XDataMapListener {
    private static final int DATAMAP_ASSETPATH = 1;
    private static final int DATAMAP_ASSET_TYPE = 2;

    private SceneAsset sceneAsset;
    private Scene scene;

    private boolean assetToLoad;
    private String assetPath = "";

    private boolean componentAttached;

    private int fileType = XAssetUtil.getFileTypeValue(Files.FileType.Internal);

    private boolean assetError;

    public XGLTFComponent() {
    }

    public XGLTFComponent(String path) {
        setAssetInternal(path, fileType);
    }

    public XGLTFComponent(String path, Files.FileType fileType) {
        setAssetInternal(path, XAssetUtil.getFileTypeValue(fileType));
    }

    @Override
    protected void onComponentAttach(XWorld world, XEntity entity) {
        componentAttached = true;
        if(assetToLoad) {
            setAssetInternal(assetPath, fileType);
        }
    }

    @Override
    protected void onComponentDetach(XWorld world, XEntity entity) {
        componentAttached = false;
    }

    @Override
    public void calculateBoundingBox(BoundingBox boundingBox) {
        if(scene != null) {
            scene.modelInstance.calculateBoundingBox(boundingBox);
        }
    }

    @Override
    public void onRender(XBatch3D batch) {
        if(scene != null) {
            batch.drawModel(scene);
        }
    }

    @Override
    public void onUpdate(Matrix4 transform) {
        if(scene != null) {
            scene.modelInstance.transform.set(transform);
        }
    }

    @Override
    public void onReset() {
        super.onReset();
        clearAsset();
    }

    @Override
    public void onSave(XDataMap map) {
        if(!assetPath.isEmpty()) {
            map.put(DATAMAP_ASSETPATH, assetPath);
            map.put(DATAMAP_ASSET_TYPE, fileType);
        }
    }

    @Override
    public void onLoad(XDataMap map) {
        String asset = map.getString(DATAMAP_ASSETPATH, null);
        int type = map.getInt(DATAMAP_ASSET_TYPE, XAssetUtil.getFileTypeValue(Files.FileType.Internal));
        if(asset != null) {
            setAssetInternal(asset, type);
        }
    }

    @Override
    public void onUIDraw(XUIData uiData) {
        if(uiData.button("Bounding Box", "Calculate")) {
            flags.put(FLAG_CALCULATE_BOUNDING_BOX);
        }
        XUIOpStringEditText op = XUIOpStringEditText.get();

        if(assetError) {
            op.lineColor = Color.RED.toIntBits();
        }
        if(uiData.editText("Asset Path", assetPath, op)) {
            setAssetInternal(op.value, fileType);
        }
        if(uiData.dropTarget(XDragDropTarget.FILE_SOURCE)) {
            Object data = uiData.consumeDropTarget();
            if(data instanceof String) {
                String path = (String)data;
                setAsset(path);
            }
        }
    }

    public void setAsset(String path) {
        setAssetInternal(path, XAssetUtil.getFileTypeValue(Files.FileType.Internal));
    }

    public void setAsset(String path, Files.FileType fileType) {
        setAssetInternal(path, XAssetUtil.getFileTypeValue(fileType));
    }

    public ModelInstance getModelInstance() {
        if(scene != null) {
            return scene.modelInstance;
        }
        return null;
    }

    private void setAssetInternal(String path, int fileType) {
        if(path != null) {
            path = path.trim();
        }
        if(!componentAttached) {
            assetToLoad = true;
            assetPath = path;
            this.fileType = fileType;
            return;
        }
        assetToLoad = false;
        clearAsset();

        if(path == null || path.isEmpty()) {
            return;
        }

        this.fileType = fileType;
        assetPath = path;

        FileHandle fileHandle = getFileHandle(path);
        if(fileHandle == null) {
            assetError = true;
            return;
        }

        this.fileType = XAssetUtil.getFileTypeValue(fileHandle.type());
        String extension = fileHandle.extension();

        try {
            if(extension.equals("glb")) {
                sceneAsset = new GLBLoader().load(fileHandle);
            }
            else if(extension.equals("gltf")) {
                sceneAsset = new GLTFLoader().load(fileHandle);
            }
            else {
                assetError = true;
                return;
            }
        }
        catch(Throwable t) {
            t.printStackTrace();
            return;
        }

        scene = new Scene(sceneAsset.scene);
        flags.put(FLAG_CALCULATE_BOUNDING_BOX);
        updateModelInstance(scene.modelInstance);
    }

    private void clearAsset() {
        assetError = false;
        assetPath = "";
        fileType = XAssetUtil.getFileTypeValue(Files.FileType.Internal);
        if(sceneAsset != null) {
            sceneAsset.dispose();
            sceneAsset = null;
            scene = null;
        }
    }

    private FileHandle getFileHandle(String path) {
        Files.FileType type = XAssetUtil.getFileTypeEnum(fileType);
        FileHandle fileHandle = Gdx.files.getFileHandle(path, type);
        if(!fileHandle.exists() || fileHandle.isDirectory()) {
            //TODO fix this hack.
            if(type == Files.FileType.Internal) {
                fileHandle = Gdx.files.local(path);
                if(!fileHandle.exists() || fileHandle.isDirectory()) {
                    fileHandle = null;
                }
            }
            else if(type == Files.FileType.Local) {
                fileHandle = Gdx.files.internal(path);
                if(!fileHandle.exists() || fileHandle.isDirectory()) {
                    fileHandle = null;
                }
            }
        }
        return fileHandle;
    }
}