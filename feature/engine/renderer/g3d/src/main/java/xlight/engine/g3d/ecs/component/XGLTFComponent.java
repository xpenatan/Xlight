package xlight.engine.g3d.ecs.component;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import net.mgsx.gltf.loaders.glb.GLBLoader;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
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

    private SceneAsset sceneAsset;
    private Scene scene;

    private boolean assetToLoad;
    private String assetPath = "";

    private boolean componentAttached;

    Files.FileType fileType = Files.FileType.Internal;

    public XGLTFComponent() {
    }

    public XGLTFComponent(String path) {
        setAssetInternal(path);
    }

    @Override
    protected void onComponentAttach(XWorld world, XEntity entity) {
        componentAttached = true;
        if(assetToLoad) {
            setAssetInternal(assetPath);
        }
    }

    @Override
    protected void onComponentDetach(XWorld world, XEntity entity) {
        componentAttached = false;
    }

    public void setAsset(String path) {
        setAssetInternal(path);
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

    private void setAssetInternal(String path) {
        if(path != null) {
            path = path.trim();
        }
        if(!componentAttached) {
            assetToLoad = true;
            assetPath = path;
            return;
        }
        assetToLoad = false;
        clearAsset();

        FileHandle fileHandle = Gdx.files.getFileHandle(path, fileType);

        if(path == null || !fileHandle.exists() || fileHandle.isDirectory()) {
            return;
        }
        String extension = fileHandle.extension();

        try {
            if(extension.equals("glb")) {
                sceneAsset = new GLBLoader().load(fileHandle);
            }
            else if(extension.equals("gltf")) {
                sceneAsset = new GLTFLoader().load(fileHandle);
            }
            else {
                return;
            }
        }
        catch(Throwable t) {
            t.printStackTrace();
            return;
        }

        assetPath = path;
        scene = new Scene(sceneAsset.scene);
        flags.put(FLAG_CALCULATE_BOUNDING_BOX);
        updateModelInstance(scene.modelInstance);
    }

    @Override
    public void onReset() {
        super.onReset();
        clearAsset();
    }

    private void clearAsset() {
        assetPath = "";
        if(sceneAsset != null) {
            sceneAsset.dispose();
            sceneAsset = null;
            scene = null;
        }
    }

    @Override
    public void onSave(XDataMap map) {
        if(!assetPath.isEmpty()) {
            map.put(DATAMAP_ASSETPATH, assetPath);
        }
    }

    @Override
    public void onLoad(XDataMap map) {
        String asset = map.getString(DATAMAP_ASSETPATH, null);
        if(asset != null) {
            setAssetInternal(asset);
        }
    }

    @Override
    public void onUIDraw(XUIData uiData) {
        if(uiData.button("Bounding Box", "Calculate")) {
            flags.put(FLAG_CALCULATE_BOUNDING_BOX);
        }
        XUIOpStringEditText op = XUIOpStringEditText.get();
        if(uiData.editText("Asset Path", assetPath, op)) {
            setAssetInternal(op.value);
        }
    }
}
