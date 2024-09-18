package xlight.editor.window.gameeditor.ecs.system;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import xlight.engine.camera.PROJECTION_MODE;
import xlight.engine.camera.XCamera;
import xlight.engine.camera.ecs.manager.XCameraManager;
import xlight.engine.ecs.XWorld;
import xlight.engine.g3d.model.XModelInstance;
import xlight.engine.g3d.util.XShapeModelHelper;
import xlight.engine.math.XMath;

public class XGizmoApplication extends ApplicationAdapter {
    private ModelBatch modelBatch;
    private Environment environment;

    private Model positionGizmoModel;
    private XModelInstance positionGizmoModelInstance;
    private XWorld world;
    private XCamera modelCamera;

    public XGizmoApplication(XWorld world) {
        this.world = world;
        modelBatch = new ModelBatch();

        positionGizmoModel = XShapeModelHelper.createPositionGizmo(0.4f, false, 0.9f);
        positionGizmoModelInstance = new XModelInstance(positionGizmoModel);
        positionGizmoModelInstance.initMeshDataCache();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1, 1, 1, 1f));

        modelCamera = XCamera.newInstance();
        modelCamera.setViewport(new ScreenViewport());
        modelCamera.setType(1);
        modelCamera.setPosition(0, 0, 1.8f);
        modelCamera.setProjectionMode(PROJECTION_MODE.PERSPECTIVE);
    }

    @Override
    public void render() {
        tick(world);
    }

    private void tick(XWorld world) {
        ScreenUtils.clear(0, 0, 0, 0, true);

        XCameraManager cameraManager = world.getManager(XCameraManager.class);
        XCamera gameCamera = cameraManager.getRenderingGameCamera();
        XCamera camera = cameraManager.getRenderingUICamera();

        if(camera != null) {
            Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT); // Makes model render on top of the game models

            modelCamera.updateCamera();
            modelBatch.begin(modelCamera.asGDXCamera());

            XMath.MAT4_1.idt();
            XMath.MAT4_1.rotateTowardDirection(gameCamera.getDirection(), gameCamera.getUp());
            XMath.MAT4_1.inv();

            XMath.QUAT_1.setFromMatrix(XMath.MAT4_1);

            positionGizmoModelInstance.transform.idt();
            positionGizmoModelInstance.transform.rotate(XMath.QUAT_1);
            modelBatch.render(positionGizmoModelInstance, environment);
            modelBatch.end();
        }
    }
}
