package xlight.demo.basic;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import xlight.engine.camera.PROJECTION_MODE;
import xlight.engine.camera.ecs.component.XCameraComponent;
import xlight.engine.core.XApplication;
import xlight.engine.core.XEngine;
import xlight.engine.core.ecs.system.XCameraSystem;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.XWorldService;
import xlight.engine.ecs.component.XGameWorldComponent;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.ecs.system.XSystemService;
import xlight.engine.ecs.system.XSystemType;
import xlight.engine.g3d.ecs.component.XGLTFComponent;
import xlight.engine.g3d.ecs.system.XGLTFSystem;
import xlight.engine.scene.XSceneListener;
import xlight.engine.scene.ecs.manager.XSceneManager;
import xlight.engine.transform.ecs.component.XTransformComponent;

public class MainApp implements XApplication {

    @Override
    public void onSetup(XEngine engine) {

        XWorld world = engine.getWorld();
        XWorldService worldService = world.getWorldService();
        XSystemService systemService = worldService.getSystemService();

        systemService.attachSystem(new XCameraSystem(XSystemType.RENDER));
//        systemService.attachSystem(new XRender3DSystem(XSystemType.RENDER));
        systemService.attachSystem(new XGLTFSystem(XSystemType.RENDER));

        XEntityService es = worldService.getEntityService();

        XSceneManager sceneManager = world.getManager(XSceneManager.class);

        sceneManager.setSceneListener(new XSceneListener() {
            @Override
            public void onLoadSceneEnd(int sceneId) {
                int size = es.getEntities().getSize();
                if(sceneId == 0 && size == 0) {
                    createCameraEntity(es, 0, 0, 3.0f);
                    createGroundEntity(es);
                    createModelEntity(es, "models/DamagedHelmet.glb", 0, 0, 0);

                    Array<String> models = new Array<>();
                    models.add("models/compare/CompareSpecular.glb");
                    models.add("models/compare/CompareRoughness.glb");
                    models.add("models/compare/CompareMetallic.glb");
                    models.add("models/compare/CompareSheen.glb");
                    models.add("models/compare/CompareNormal.glb");
                    models.add("models/compare/CompareIridescence.glb");
                    models.add("models/compare/CompareEmissiveStrength.glb");
                    models.add("models/compare/CompareTransmission.glb");
                    models.add("models/compare/CompareVolume.glb");
                    models.add("models/compare/CompareIor.glb");
                    models.add("models/compare/CompareDispersion.glb");
                    models.add("models/compare/CompareAnisotropy.glb");
                    models.add("models/compare/CompareBaseColor.glb");
                    float x = 2.5f;
                    for(int i = 0; i < models.size; i++) {
                        String modelPath = models.get(i);
                        createModelEntity(es, modelPath, x, 0, 0);
                        x += 3.0f;
                    }
                }
            }
        });

        sceneManager.loadFromFile("scene/default.xscene", Files.FileType.Local);
    }

    public void createCameraEntity(XEntityService es, float x, float y, float z) {
        XEntity e = es.obtain();

        XCameraComponent cameraComponent = new XCameraComponent();
        cameraComponent.camera.setProjectionMode(PROJECTION_MODE.PERSPECTIVE);
        cameraComponent.camera.setNear(0.1f);
        cameraComponent.camera.setFar(1000f);

        e.attachComponent(new XTransformComponent().position(x, y, z));
        e.attachComponent(cameraComponent);
        e.attachComponent(new XGameWorldComponent());
        es.attachEntity(e);
    }

    public void createModelEntity(XEntityService es, String asset, float x, float y, float z) {
        XEntity e = es.obtain();
        FileHandle assetFile = Gdx.files.internal(asset);
        String name = assetFile.nameWithoutExtension();
        e.setName(name);
        e.attachComponent(new XGLTFComponent(asset, Files.FileType.Local));
        e.attachComponent(new XTransformComponent().position(x, y, z));
        e.attachComponent(new XGameWorldComponent());
        es.attachEntity(e);
    }

    public void createGroundEntity(XEntityService es) {
        XEntity e = es.obtain();
        e.setName("Ground");
        e.attachComponent(new XGLTFComponent("models/ground/ground.gltf", Files.FileType.Local));
        e.attachComponent(new XTransformComponent().position(0, -2, 0).scale(5, 1, 5));
        e.attachComponent(new XGameWorldComponent());
        es.attachEntity(e);
    }
}