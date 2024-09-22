package xlight.engine.g3d.ecs.component;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.BoundingBox;
import net.mgsx.gltf.loaders.glb.GLBLoader;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.g3d.XBatch3D;

public class XGLTFComponent extends XRender3DComponent{

    private SceneAsset sceneAsset;
    private Scene scene;

    public void setAsset(FileHandle path) {
        if(sceneAsset != null) {
            sceneAsset.dispose();
        }
        if(path.extension().equals("glb")) {
            sceneAsset = new GLBLoader().load(path);
        }
        else {
            sceneAsset = new GLTFLoader().load(path);
        }
        scene = new Scene(sceneAsset.scene);
        flags.put(FLAG_CALCULATE_BOUNDING_BOX);
    }

    public XGLTFComponent(FileHandle path) {
        setAsset(path);
    }

    @Override
    public void onAttach(XWorld world, XEntity entity) {
    }

    @Override
    public void onDetach(XWorld world, XEntity entity) {
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
}
