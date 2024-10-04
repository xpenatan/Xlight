package xlight.engine.camera.ecs.component;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import xlight.engine.camera.PROJECTION_MODE;
import xlight.engine.camera.XCamera;
import xlight.engine.core.editor.ui.XUIData;
import xlight.engine.core.editor.ui.XUIDataListener;
import xlight.engine.core.editor.ui.options.XUIOpCheckbox;
import xlight.engine.core.editor.ui.options.XUIOpEditText;
import xlight.engine.core.editor.ui.options.XUIOpTransform;
import xlight.engine.datamap.XDataMap;
import xlight.engine.datamap.XDataMapListener;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.transform.XTransform;

public class XCameraComponent implements XComponent, XDataMapListener, XUIDataListener {

    private static final int DATA_LOCAL_POSITION = 1;
    private static final int DATA_LOCAL_ROTATION = 2;
    private static final int DATA_ACTIVE_CAMERA = 3;
    private static final int DATA_PROJECTION_INDEX = 4;
    private static final int DATA_NEAR = 5;
    private static final int DATA_FAR = 6;
    private static final int DATA_ZOOM = 7;
    private static final int DATA_FOV = 8;


    public final XTransform localTransform;
    public final XCamera camera;

    public XCameraComponent() {
        localTransform = XTransform.newInstance();
        camera = XCamera.newInstance();
        camera.setActiveCamera(true);
        camera.setProjectionMode(PROJECTION_MODE.PERSPECTIVE);
        ScreenViewport screenViewport = new ScreenViewport();
        camera.setViewport(screenViewport);
    }

    @Override
    public void onSave(XDataMap map) {
        Vector3 position = localTransform.getPosition();
        map.put(DATA_LOCAL_POSITION, position);

        Vector3 rotation = localTransform.getRotation();
        map.put(DATA_LOCAL_ROTATION, rotation);

        map.put(DATA_ACTIVE_CAMERA, camera.isActiveCamera());
        int projectionIndex = camera.getProjectionMode() == PROJECTION_MODE.PERSPECTIVE ? 1 : 0;
        map.put(DATA_PROJECTION_INDEX, projectionIndex);
        map.put(DATA_NEAR, camera.getNear());
        map.put(DATA_FAR, camera.getFar());
        map.put(DATA_ZOOM, camera.getZoom());
        map.put(DATA_FOV, camera.getFieldOfView());
    }

    @Override
    public void onLoad(XDataMap map) {
        Vector3 position = localTransform.getPosition();
        map.getVector3(DATA_LOCAL_POSITION, position);
        localTransform.setPosition(position);

        Vector3 rotation = localTransform.getRotation();
        map.getVector3(DATA_LOCAL_ROTATION, rotation);
        localTransform.setRotation(rotation);

        camera.setActiveCamera(map.getBoolean(DATA_ACTIVE_CAMERA, false));
        PROJECTION_MODE mode = map.getInt(DATA_PROJECTION_INDEX, 1) == 1 ? PROJECTION_MODE.PERSPECTIVE : PROJECTION_MODE.ORTHOGONAL;
        camera.setProjectionMode(mode);
        camera.setNear(map.getFloat(DATA_NEAR, camera.getNear()));
        camera.setFar(map.getFloat(DATA_FAR, camera.getFar()));
        camera.setZoom(map.getFloat(DATA_ZOOM, camera.getZoom()));
        camera.setFieldOfView(map.getFloat(DATA_FOV, camera.getFieldOfView()));
    }

    @Override
    public void onUIDraw(XUIData uiData) {

        XUIOpTransform op = XUIOpTransform.get();
        XUIOpCheckbox chkOp = XUIOpCheckbox.get();
        XUIOpEditText edtOp = XUIOpEditText.get();

        if(uiData.beginHeader("Local Transform")) {
            op.drawOffset = false;
            op.drawScale = false;
            op.drawSize = false;
            uiData.transform(localTransform, op);
        }
        uiData.endHeader();

        if(uiData.beginHeader("Camera")) {
            edtOp.reset();
            if(uiData.checkbox("IsActive", camera.isActiveCamera(), chkOp)) {
                camera.setActiveCamera(chkOp.value);
            }
            boolean is3d = camera.getProjectionMode() == PROJECTION_MODE.PERSPECTIVE;
            edtOp.reset();
            if(uiData.checkbox("Is3D", is3d, chkOp)) {
                camera.setProjectionMode(chkOp.value ? PROJECTION_MODE.PERSPECTIVE : PROJECTION_MODE.ORTHOGONAL);
            }

            edtOp.reset();
            if(uiData.editText("Near", camera.getNear(), edtOp)) {
                camera.setNear(edtOp.value);
            }
            edtOp.reset();
            if(uiData.editText("Far", camera.getFar(), edtOp)) {
                camera.setFar(edtOp.value);
            }
            edtOp.reset();
            if(uiData.editText("Zoom", camera.getZoom(), edtOp)) {
                camera.setZoom(edtOp.value);
            }
            edtOp.reset();
            edtOp.tooltip = "Field of view";
            if(uiData.editText("FOV", camera.getFieldOfView(), edtOp)) {
                camera.setFieldOfView(edtOp.value);
            }
        }
        uiData.endHeader();
    }
}
