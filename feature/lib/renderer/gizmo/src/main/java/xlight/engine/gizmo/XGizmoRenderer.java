package xlight.engine.gizmo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CylinderShapeBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import xlight.engine.glutils.XShapeRenderer;
import xlight.engine.g3d.model.XMeshData;
import xlight.engine.g3d.model.XModelInstance;
import xlight.engine.g3d.util.XShapeModelHelper;
import xlight.engine.input.XInputStateController;
import xlight.engine.math.XAngleDirection;
import xlight.engine.math.XMath;
import xlight.engine.math.XRotSeq;
import xlight.engine.transform.XGizmoType;

public class XGizmoRenderer {

    private boolean isDebug = false;

    public final static float AXIS_WIDTH = 0.7f;

    final private Environment environment;
    final private Model positionGizmoModel;
    final private XModelInstance positionGizmoModelInstance;
    final private Model rotationGizmoModel;
    final private XModelInstance rotationGizmoModelInstance;
    final private Model rotationInvisibleGizmoModel;
    final private XModelInstance rotationInvisibleGizmoModelInstance;
    final private Model scaleGizmoModel;
    final private XModelInstance scaleGizmoModelInstance;

    final private Quaternion objectAngle = new Quaternion();
    final private Quaternion gizmoAngle = new Quaternion();
    final private Vector3 objectPosition;
    final private Vector3 objectStartPosition;

    final private Quaternion dragStartRotation;
    final private Vector3 objectVirtualPosition;
    final private Vector3 objectVirtualTotalVectorTargetAngle;

    // A rotated start position
    final private Vector3 dragStartPosition;
    final private Vector3 dragEndPosition;

    private float startAngleDegree;

    private ModelBatch modelBatch;

    private Color lastHighlightColor1 = new Color();

    private XMeshData meshData;

    private boolean isGlobalTransform = true;
    private XGizmoType transformType = XGizmoType.POSITION;

    private XInputStateController onInput = new XInputStateController();

    private XCursor3DRenderer.AXIS_TYPE targetType = null;
    private Vector3 intersection = new Vector3();

    private float scale = 1;

    private Vector3 tmp = new Vector3();
    private Vector3 tmp2 = new Vector3();
    private Quaternion tmp4 = new Quaternion();

    private boolean startDragging = false;

    XRotSeq rotationSequence;

    public XGizmoRenderer() {
        onInput.registerButtons(Input.Buttons.LEFT);
        objectPosition = new Vector3();
        objectStartPosition = new Vector3();
        dragStartPosition = new Vector3();
        dragEndPosition = new Vector3();
        dragStartRotation = new Quaternion();
        objectVirtualPosition = new Vector3();
        objectVirtualTotalVectorTargetAngle = new Vector3();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1, 1, 1, 1f));

        positionGizmoModel =  XShapeModelHelper.createPositionGizmo(AXIS_WIDTH, true, 0.8f);
        positionGizmoModelInstance = new XModelInstance(positionGizmoModel);

        rotationGizmoModel = createRotationGizmo();
        rotationGizmoModelInstance = new XModelInstance(rotationGizmoModel);
        rotationInvisibleGizmoModel = createInvisibleRotationGizmo();
        rotationInvisibleGizmoModelInstance = new XModelInstance(rotationInvisibleGizmoModel);

        scaleGizmoModel = createScaleGizmo();
        scaleGizmoModelInstance = new XModelInstance(scaleGizmoModel);

        positionGizmoModelInstance.initMeshDataCache();
        rotationGizmoModelInstance.initMeshDataCache();
        scaleGizmoModelInstance.initMeshDataCache();

        modelBatch = new ModelBatch();
    }

    public boolean isHighlight() {
        return meshData != null;
    }

    private Model createRotationGizmo() {
        ModelBuilder mb = new ModelBuilder();
        mb.begin();

        float height = 0.03f;
        int divisionsU = 32;
        int divisionsV = 3;

        {
            Node node = mb.node();
            node.id = "rotationX";
            Material material = new Material("rotationX", ColorAttribute.createDiffuse(1, 0, 0, 0.7f));
            BlendingAttribute blending = new BlendingAttribute();
            material.set(blending);
            MeshPartBuilder builder = mb.part("rotationX", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position, material);
            torus(builder, 0.99f, height, divisionsU, divisionsV);
            node.rotation.set(Vector3.Y, -90);
        }
        {
            Node node = mb.node();
            node.id = "rotationY";
            Material material = new Material("rotationY", ColorAttribute.createDiffuse(0, 1, 0, 0.7f));
            BlendingAttribute blending = new BlendingAttribute();
            material.set(blending);
            MeshPartBuilder builder = mb.part("rotationY", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position, material);
            torus(builder, 0.9700f, height, divisionsU, divisionsV);
            node.rotation.set(Vector3.X, -90);
        }
        {
            Node node = mb.node();
            node.id = "rotationZ";
            Material material = new Material("rotationZ", ColorAttribute.createDiffuse(0, 0, 1, 0.7f));

            BlendingAttribute blending = new BlendingAttribute();
            material.set(blending);

            MeshPartBuilder builder = mb.part("rotationZ", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position, material);
            torus(builder, 0.9600f, height, divisionsU, divisionsV);
        }

        return mb.end();
    }

    private Model createScaleGizmo() {
        Model axisXModel = createArrowBoxUpAxis(1f, 0f, 0f);
        Model axisYModel = createArrowBoxUpAxis(0f, 1f, 0f);
        Model axisZModel = createArrowBoxUpAxis(0f, 0f, 1f);

        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        Node axisX = mb.node("axisX", axisXModel);
        Node axisY = mb.node("axisY", axisYModel);
        Node axisZ = mb.node("axisZ", axisZModel);
        axisX.rotation.set(Vector3.Z, -90);
        axisZ.rotation.set(Vector3.X, 90);

        Model modelYZ = createBoxXY(1f, 0f, 0f);
        Node axisYZ = mb.node("axisYZ", modelYZ);
        axisYZ.rotation.set(Vector3.Y, -90);

        Model modelXZ = createBoxXY(0f, 1f, 0f);
        Node axisXZ = mb.node("axisXZ", modelXZ);
        axisXZ.rotation.set(Vector3.X, 90);

        Model modelXY = createBoxXY(0f, 0f, 1f);
        Node axisXY = mb.node("axisXY", modelXY);

        return mb.end();
    }

    private Model createInvisibleRotationGizmo() {
        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        Node node2 = mb.node();
        node2.id = "invisibleRotMesh";
        node2.rotation.set(Vector3.X, -90);
        Material material = new Material("invisibleRotMesh", ColorAttribute.createDiffuse(1, 1, 0, 1f));
        BlendingAttribute blending = new BlendingAttribute();
        blending.sourceFunction = GL20.GL_ZERO;
        blending.destFunction = GL20.GL_ONE;
        material.set(blending);
        MeshPartBuilder meshBuilderInvisible = mb.part("cylinder", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
                material);
        CylinderShapeBuilder.build(meshBuilderInvisible, 1.90f, 0.02f, 1.90f, 22);
        return mb.end();
    }

    private Model createArrowBoxUpAxis(float r, float g, float b) {
        ModelBuilder mb = new ModelBuilder();
        Material material = null;

        float offset = 0.1f;
        // scale model
        float scaleBoxWith = 0.3f;
        XMath.VEC3_1.set(scaleBoxWith / 2f, scaleBoxWith / 2f, scaleBoxWith / 2f);

        mb.begin();
        Node nodeConeAxisX = mb.node();
        nodeConeAxisX.id = "boxAxisX";
        nodeConeAxisX.translation.y = offset + AXIS_WIDTH + 0.069f;

        nodeConeAxisX.calculateLocalTransform();
        nodeConeAxisX.calculateWorldTransform();
        material = new Material("boxAxisX", ColorAttribute.createDiffuse(r, g, b, 0.8f));
        BlendingAttribute blending = new BlendingAttribute();
        blending.blended = false;
        material.set(blending);
        MeshPartBuilder mesBuilderConeX = mb.part("box", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material);
        BoxShapeBuilder.build(mesBuilderConeX, XMath.VEC3_1.x, XMath.VEC3_1.y, XMath.VEC3_1.z);

        Node cylinderX = mb.node();
        cylinderX.id = "cylinderX";
        cylinderX.translation.y = offset + (AXIS_WIDTH / 2f + 0.011f);
        cylinderX.calculateLocalTransform();
        cylinderX.calculateWorldTransform();
        MeshPartBuilder meshBuilderCylinderX = mb.part("cylinder", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
                material);

        CylinderShapeBuilder.build(meshBuilderCylinderX, 0.05f, AXIS_WIDTH - 0.022f, 0.05f, 5);

        return mb.end();
    }

    private Model createBoxXY(float r, float g, float b) {
        ModelBuilder mb = new ModelBuilder();
        Material material = null;

        mb.begin();

        float size = 0.3f;
        float width = 0.02f;
        float offset = 0.1f;

        Node node = mb.node();
        node.id = "axisXY";
        material = new Material("axisXY", ColorAttribute.createDiffuse(r, g, b, 0.8f));
        BlendingAttribute blending = new BlendingAttribute();
        material.set(blending);
        MeshPartBuilder meshBuilderXY = mb.part("axisXY", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material);
        BoxShapeBuilder.build(meshBuilderXY, new Vector3(0, 0, 0), new Vector3(0, size, 0), new Vector3(size, 0, 0),
                new Vector3(size, size, 0), new Vector3(0, 0, width), new Vector3(0, size, width),
                new Vector3(size, 0, width), new Vector3(size, size, width));
        node.translation.x = offset;
        node.translation.y = offset;
        return mb.end();
    }

    public void setPosition(Vector3 objectPosition) {
        this.objectPosition.set(objectPosition);
    }

    public void setPosition(float x, float y, float z) {
        objectPosition.set(x, y, z);
    }

    public Vector3 getPosition() {
        return objectPosition;
    }

    public Vector3 getObjectVirtualPosition() {
        if(!isDragging() || transformType != XGizmoType.POSITION) {
            objectVirtualPosition.set(objectPosition);
        }
        return objectVirtualPosition;
    }

    public Vector3 getObjectVirtualTotalVectorTargetAngle() {
        return objectVirtualTotalVectorTargetAngle;
    }

    public void setGlobalTransform(boolean flag) {
        isGlobalTransform = flag;
    }

    public boolean isGlobalTransform() {
        return isGlobalTransform;
    }

    public void setObjectAngle(Quaternion quaternion, XRotSeq rotationSequence) {
        objectAngle.set(quaternion);
        this.rotationSequence = rotationSequence;
    }

    public XCursor3DRenderer.AXIS_TYPE getAxisType() {
        return targetType;
    }

    public boolean isDragging() {
        return targetType != null && onInput.isButtonDragging();
    }

    public void setTransformType(XGizmoType type) {
        transformType = type;
    }

    public XGizmoType getTransformType() {
        return transformType;
    }

    public void render(Camera camera, boolean isPerspective, XShapeRenderer renderer, XCursor3DRenderer cursor3DController) {
        if(rotationSequence == null) {
            return;
        }

        float distance = XMath.getDistance(objectPosition.x, objectPosition.y, objectPosition.z, camera.position.x, camera.position.y, camera.position.z);
        if(isPerspective) {
            scale = (distance / 5f);
        }

        if(isGlobalTransform) {
            gizmoAngle.idt();
        }
        else {
            gizmoAngle.set(objectAngle);
        }

        if(isDebug && renderer != null) {
            float size = 0.1f;
            float halfSize = size / 2f;

//            renderer.setColor(Color.WHITE);
//            renderer.box(objectPosition.x - halfSize, objectPosition.y - halfSize, objectPosition.z + halfSize, size, size, size);
//            renderer.setColor(Color.RED);
//            renderer.box(dragStartPosition.x - halfSize, dragStartPosition.y - halfSize, dragStartPosition.z + halfSize, size, size, size);
            renderer.setColor(Color.RED);
//            renderer.box(dragEndPosition.x - halfSize, dragEndPosition.y - halfSize, dragEndPosition.z + halfSize, size, size, size);
//            renderer.setColor(Color.PURPLE);
            renderer.box(objectVirtualPosition.x - halfSize, objectVirtualPosition.y - halfSize, objectVirtualPosition.z + halfSize, size, size, size);
//            renderer.setColor(Color.GOLD);
//            renderer.box(intersection.x - halfSize, intersection.y - halfSize, intersection.z + halfSize, size, size, size);
        }

        update(gizmoAngle);

        Vector3 myPosition = tmp2;
        Vector3 target = tmp;
        target.set(camera.position).sub(myPosition.set(objectPosition)).nor(); // we get a realtive normalized Vector
        // representing the direction to look
        tmp4.setFromCross(Vector3.Z, target);
        rotationInvisibleGizmoModelInstance.transform.set(tmp4).setTranslation(myPosition);
        rotationInvisibleGizmoModelInstance.transform.scale(scale, scale, scale);

        updateModelFaceLocation(camera, gizmoAngle);

        onInput.update();

        XMeshData meshData = null;

        if(transformType == XGizmoType.POSITION) {
            meshData = positionGizmoModelInstance.rayCast(null, camera, Gdx.input.getX(), Gdx.input.getY(), intersection);
        }
        else if(transformType == XGizmoType.ROTATE) {
            meshData = rotationGizmoModelInstance.rayCast(null, camera, Gdx.input.getX(), Gdx.input.getY(), intersection);
        }
        else if(transformType == XGizmoType.SCALE) {
            meshData = scaleGizmoModelInstance.rayCast(null, camera, Gdx.input.getX(), Gdx.input.getY(), intersection);
        }
        if(targetType == null) {
            if(!isDragging()) {
                setMeshHighLight(meshData);
            }
        }

        boolean changeTargetType = Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT);
        if(targetType == null && (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) || changeTargetType)) {
            if(meshData != null) {
                XCursor3DRenderer.AXIS_TYPE targetType = getTargetType(meshData);
                cursor3DController.set3DCursorPosition(objectPosition);
                cursor3DController.setGlobalAngle(gizmoAngle);
                cursor3DController.setMoveState(targetType);
                if(!changeTargetType) {
                    this.targetType = targetType;
                    objectStartPosition.set(objectPosition);
                    dragStartPosition.set(objectPosition);
                    dragStartRotation.set(gizmoAngle);
                    XCursor3DRenderer.getLockedAxisPosition(camera, this.targetType, gizmoAngle, dragStartPosition);
                    startDragging = true;
                }
            }
        }
        else if(targetType != null && onInput.isButtonJustPressedUp(Input.Buttons.LEFT)) {
            targetType = null;
            setMeshHighLight(null);
        }

        if(isDragging()) {
            dragEndPosition.set(objectStartPosition);
            XCursor3DRenderer.getLockedAxisPosition(camera, targetType, rotationSequence, gizmoAngle, dragEndPosition, intersection);
            float subX = dragEndPosition.x - dragStartPosition.x;
            float subY = dragEndPosition.y - dragStartPosition.y;
            float subZ = dragEndPosition.z - dragStartPosition.z;
            objectVirtualPosition.set(objectStartPosition).add(subX, subY, subZ);

            objectVirtualTotalVectorTargetAngle.setZero();

            if(transformType == XGizmoType.ROTATE) {
                updateDraggingRotation();
            }
        }

        // Makes model render on top of the game models
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        modelBatch.begin(camera);
        if(transformType == XGizmoType.POSITION) {
            modelBatch.render(positionGizmoModelInstance, environment);
        }
        else if(transformType == XGizmoType.ROTATE) {
            modelBatch.render(rotationInvisibleGizmoModelInstance, environment);
            modelBatch.flush();
            modelBatch.render(rotationGizmoModelInstance, environment);
        }
        else if(transformType == XGizmoType.SCALE) {
            modelBatch.render(scaleGizmoModelInstance, environment);
        }
        modelBatch.end();
    }

    private void update(Quaternion quaternion) {
        positionGizmoModelInstance.transform.idt();
        positionGizmoModelInstance.transform.translate(objectPosition);
        positionGizmoModelInstance.transform.scale(scale, scale, scale);
        positionGizmoModelInstance.transform.rotate(quaternion);

        rotationGizmoModelInstance.transform.idt();
        rotationGizmoModelInstance.transform.translate(objectPosition);
        rotationGizmoModelInstance.transform.scale(scale, scale, scale);
        rotationGizmoModelInstance.transform.rotate(quaternion);

        scaleGizmoModelInstance.transform.idt();
        scaleGizmoModelInstance.transform.translate(objectPosition);
        scaleGizmoModelInstance.transform.scale(scale, scale, scale);
        scaleGizmoModelInstance.transform.rotate(quaternion);
    }

    private XCursor3DRenderer.AXIS_TYPE getTargetType(XMeshData meshData) {
        if(meshData == null)
            return null;
        XCursor3DRenderer.AXIS_TYPE type = null;
        if(transformType == XGizmoType.POSITION) {
            Node parent = meshData.parentNode.getParent();
            String id = parent.id;
            if(id.equals("axisX")) {
                type = XCursor3DRenderer.AXIS_TYPE.X;
            }
            else if(id.equals("axisY")) {
                type = XCursor3DRenderer.AXIS_TYPE.Y;
            }
            else if(id.equals("axisZ")) {
                type = XCursor3DRenderer.AXIS_TYPE.Z;
            }
            else if(id.equals("axisXY")) {
                type = XCursor3DRenderer.AXIS_TYPE.X_Y;
            }
            else if(id.equals("axisXZ")) {
                type = XCursor3DRenderer.AXIS_TYPE.X_Z;
            }
            else if(id.equals("axisYZ")) {
                type = XCursor3DRenderer.AXIS_TYPE.Y_Z;
            }
        }
        else if(transformType == XGizmoType.ROTATE) {
            Node parent = meshData.parentNode;
            String id = parent.id;

            if(id.equals("rotationX")) {
                type = XCursor3DRenderer.AXIS_TYPE.Y_Z;
            }
            else if(id.equals("rotationY")) {
                type = XCursor3DRenderer.AXIS_TYPE.X_Z;
            }
            else if(id.equals("rotationZ")) {
                type = XCursor3DRenderer.AXIS_TYPE.X_Y;
            }
        }
        else if(transformType == XGizmoType.SCALE) {
            Node parent = meshData.parentNode.getParent();
            String id = parent.id;
            if(id.equals("axisX")) {
                type = XCursor3DRenderer.AXIS_TYPE.X;
            }
            else if(id.equals("axisY")) {
                type = XCursor3DRenderer.AXIS_TYPE.Y;
            }
            else if(id.equals("axisZ")) {
                type = XCursor3DRenderer.AXIS_TYPE.Z;
            }
            else if(id.equals("axisXY")) {
                type = XCursor3DRenderer.AXIS_TYPE.X_Y;
            }
            else if(id.equals("axisXZ")) {
                type = XCursor3DRenderer.AXIS_TYPE.X_Z;
            }
            else if(id.equals("axisYZ")) {
                type = XCursor3DRenderer.AXIS_TYPE.Y_Z;
            }
        }
        return type;
    }

    private void updateDraggingRotation() {

        XMath.MAT4_1.idt();
        XMath.MAT4_1.rotate(dragStartRotation);
        XMath.MAT4_1.inv();
        XMath.MAT4_1.translate(tmp2.set(intersection).sub(objectPosition));
        XMath.MAT4_1.getTranslation(tmp);

        boolean startDragging = this.startDragging;
        if(this.startDragging) {
            this.startDragging = false;
        }

        float angleDegree = 0;
        if(targetType == XCursor3DRenderer.AXIS_TYPE.Y_Z) { // Red
            angleDegree = XMath.getAngleBetweenTwoPosition(0, 0, tmp.y, tmp.z, false, false);
            if(startDragging) {
                startAngleDegree = angleDegree;
            }
            float offset = XMath.getAngleOffset(startAngleDegree, angleDegree);
            objectVirtualTotalVectorTargetAngle.set(offset, 0, 0);
        }
        else if(targetType == XCursor3DRenderer.AXIS_TYPE.X_Z) {  // Green
            angleDegree = XMath.getAngleBetweenTwoPosition(0, 0, tmp.x, tmp.z, false, false);
            if(startDragging) {
                startAngleDegree = angleDegree;
            }
            float offset = XMath.getAngleOffset(startAngleDegree, angleDegree);
            objectVirtualTotalVectorTargetAngle.set(0, -offset, 0);
        }
        else if(targetType == XCursor3DRenderer.AXIS_TYPE.X_Y) {  // Green
            angleDegree = XMath.getAngleBetweenTwoPosition(0, 0, tmp.x, tmp.y, false, false);
            if(startDragging) {
                startAngleDegree = angleDegree;
            }
            float offset = XMath.getAngleOffset(startAngleDegree, angleDegree);
            objectVirtualTotalVectorTargetAngle.set(0, 0, offset);
        }

        if(isDebug) {
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println("startAngleDegree: " + startAngleDegree + " angleDegree: " + angleDegree);
            System.out.println("objectVirtualTotalVectorTargetAngle: " + objectVirtualTotalVectorTargetAngle);
        }
    }

    private void setMeshHighLight(XMeshData meshData) {
        if(this.meshData == meshData) {
            return;
        }

        if(this.meshData != null) {
            Material material = this.meshData.nodePart.material;
            ColorAttribute colorAttribute = (ColorAttribute)material.get(ColorAttribute.Diffuse);
            colorAttribute.color.set(lastHighlightColor1);
        }

        this.meshData = meshData;

        if(meshData != null) {
            Material material = meshData.nodePart.material;
            ColorAttribute colorAttribute = (ColorAttribute)material.get(ColorAttribute.Diffuse);
            lastHighlightColor1.set(colorAttribute.color);
            colorAttribute.color.set(Color.YELLOW);
        }
    }

    private void updateModelFaceLocation(Camera camera, Quaternion quaternion) {
        XAngleDirection.XAngleEnumData location = XAngleDirection.getLocation(camera.position.x, camera.position.y, camera.position.z, quaternion,
                objectPosition.x, objectPosition.y, objectPosition.z);

        Node axisX = null;
        Node axisY = null;
        Node axisZ = null;

        Node axisXY = null;
        Node axisXZ = null;
        Node axisYZ = null;

        if(transformType == XGizmoType.POSITION) {
            axisX = positionGizmoModelInstance.getNode("axisX");
            axisY = positionGizmoModelInstance.getNode("axisY");
            axisZ = positionGizmoModelInstance.getNode("axisZ");

            axisXY = positionGizmoModelInstance.getNode("axisXY");
            axisXZ = positionGizmoModelInstance.getNode("axisXZ");
            axisYZ = positionGizmoModelInstance.getNode("axisYZ");
        }
        else if(transformType == XGizmoType.SCALE) {
            axisX = scaleGizmoModelInstance.getNode("axisX");
            axisY = scaleGizmoModelInstance.getNode("axisY");
            axisZ = scaleGizmoModelInstance.getNode("axisZ");

            axisXY = scaleGizmoModelInstance.getNode("axisXY");
            axisXZ = scaleGizmoModelInstance.getNode("axisXZ");
            axisYZ = scaleGizmoModelInstance.getNode("axisYZ");
        }
        else {
            return;
        }

        if(location.axisZ == XAngleDirection.XpeAngleEnum.TOP_RIGHT) {
            axisX.rotation.set(Vector3.Z, -90);
            axisY.rotation.set(Vector3.Z, 0);

            XMath.MAT4_1.idt();
            XMath.MAT4_1.rotate(Vector3.X, 0);
            XMath.MAT4_1.rotate(Vector3.Y, 0);
            axisXY.rotation.setFromMatrix(true, XMath.MAT4_1);
        }
        else if(location.axisZ == XAngleDirection.XpeAngleEnum.TOP_LEFT) {
            axisX.rotation.set(Vector3.Z, 90);
            axisY.rotation.set(Vector3.Z, 0);

            XMath.MAT4_1.idt();
            XMath.MAT4_1.rotate(Vector3.X, 0);
            XMath.MAT4_1.rotate(Vector3.Y, 180);
            axisXY.rotation.setFromMatrix(true, XMath.MAT4_1);
        }
        else if(location.axisZ == XAngleDirection.XpeAngleEnum.BOTTOM_RIGHT) {
            axisX.rotation.set(Vector3.Z, -90);
            axisY.rotation.set(Vector3.Z, 180);

            XMath.MAT4_1.idt();
            XMath.MAT4_1.rotate(Vector3.X, 180);
            XMath.MAT4_1.rotate(Vector3.Y, 0);
            axisXY.rotation.setFromMatrix(true, XMath.MAT4_1);
        }
        else if(location.axisZ == XAngleDirection.XpeAngleEnum.BOTTOM_LEFT) {
            axisX.rotation.set(Vector3.Z, 90);
            axisY.rotation.set(Vector3.Z, 180);

            XMath.MAT4_1.idt();
            XMath.MAT4_1.rotate(Vector3.Y, 180);
            XMath.MAT4_1.rotate(Vector3.X, 180);
            axisXY.rotation.setFromMatrix(true, XMath.MAT4_1);
        }

        if(location.axisX == XAngleDirection.XpeAngleEnum.TOP_RIGHT) {
            axisZ.rotation.set(Vector3.X, 90);

            XMath.MAT4_1.idt();
            XMath.MAT4_1.rotate(Vector3.X, 0);
            XMath.MAT4_1.rotate(Vector3.Y, 270);
            axisYZ.rotation.setFromMatrix(true, XMath.MAT4_1);
        }
        else if(location.axisX == XAngleDirection.XpeAngleEnum.TOP_LEFT) {
            axisZ.rotation.set(Vector3.X, -90);

            XMath.MAT4_1.idt();
            XMath.MAT4_1.rotate(Vector3.X, 0);
            XMath.MAT4_1.rotate(Vector3.Y, 90);
            axisYZ.rotation.setFromMatrix(true, XMath.MAT4_1);
        }
        else if(location.axisX == XAngleDirection.XpeAngleEnum.BOTTOM_RIGHT) {
            axisZ.rotation.set(Vector3.X, 90);

            XMath.MAT4_1.idt();
            XMath.MAT4_1.rotate(Vector3.X, 180);
            XMath.MAT4_1.rotate(Vector3.Y, 90);
            axisYZ.rotation.setFromMatrix(true, XMath.MAT4_1);
        }
        else if(location.axisX == XAngleDirection.XpeAngleEnum.BOTTOM_LEFT) {
            axisZ.rotation.set(Vector3.X, -90);

            XMath.MAT4_1.idt();
            XMath.MAT4_1.rotate(Vector3.X, 180);
            XMath.MAT4_1.rotate(Vector3.Y, 270);
            axisYZ.rotation.setFromMatrix(true, XMath.MAT4_1);
        }

        if(location.axisY == XAngleDirection.XpeAngleEnum.TOP_RIGHT) {
            XMath.MAT4_1.idt();
            XMath.MAT4_1.rotate(Vector3.X, 90);
            XMath.MAT4_1.rotate(Vector3.Y, 180);
            XMath.MAT4_1.rotate(Vector3.Z, 180);
            axisXZ.rotation.setFromMatrix(true, XMath.MAT4_1);
        }
        else if(location.axisY == XAngleDirection.XpeAngleEnum.TOP_LEFT) {
            XMath.MAT4_1.idt();
            XMath.MAT4_1.rotate(Vector3.X, 90);
            XMath.MAT4_1.rotate(Vector3.Y, 0);
            XMath.MAT4_1.rotate(Vector3.Z, 180);
            axisXZ.rotation.setFromMatrix(true, XMath.MAT4_1);
        }
        else if(location.axisY == XAngleDirection.XpeAngleEnum.BOTTOM_RIGHT) {
            XMath.MAT4_1.idt();
            XMath.MAT4_1.rotate(Vector3.X, 90);
            XMath.MAT4_1.rotate(Vector3.Y, 0);
            XMath.MAT4_1.rotate(Vector3.Z, 0);
            axisXZ.rotation.setFromMatrix(true, XMath.MAT4_1);
        }
        else if(location.axisY == XAngleDirection.XpeAngleEnum.BOTTOM_LEFT) {
            XMath.MAT4_1.idt();
            XMath.MAT4_1.rotate(Vector3.X, 90);
            XMath.MAT4_1.rotate(Vector3.Y, 180);
            XMath.MAT4_1.rotate(Vector3.Z, 0);
            axisXZ.rotation.setFromMatrix(true, XMath.MAT4_1);
        }

        axisX.calculateLocalTransform();
        axisX.calculateWorldTransform();
    }

    private static final MeshPartBuilder.VertexInfo v0 = new MeshPartBuilder.VertexInfo();
    private static final MeshPartBuilder.VertexInfo v1 = new MeshPartBuilder.VertexInfo();

    private static void torus(MeshPartBuilder builder, float width, float height, int divisionsU, int divisionsV) {

        // builder.setColor(Color.LIGHT_GRAY);

        MeshPartBuilder.VertexInfo curr1 = v0.set(null, null, null, null);
        curr1.hasUV = curr1.hasNormal = false;
        curr1.hasPosition = true;

        MeshPartBuilder.VertexInfo curr2 = v1.set(null, null, null, null);
        curr2.hasUV = curr2.hasNormal = false;
        curr2.hasPosition = true;
        short i1, i2, i3 = 0, i4 = 0;

        int i, j, k;
        double s, t, twopi;
        twopi = 2 * Math.PI;

        for(i = 0; i < divisionsV; i++) {
            for(j = 0; j <= divisionsU; j++) {
                for(k = 1; k >= 0; k--) {
                    s = (i + k) % divisionsV + 0.5;
                    t = j % divisionsU;

                    curr1.position.set(
                            (float)((width + height * Math.cos(s * twopi / divisionsV))
                                    * Math.cos(t * twopi / divisionsU)),
                            (float)((width + height * Math.cos(s * twopi / divisionsV))
                                    * Math.sin(t * twopi / divisionsU)),
                            (float)(height * Math.sin(s * twopi / divisionsV)));
                    k--;
                    s = (i + k) % divisionsV + 0.5;
                    curr2.position.set(
                            (float)((width + height * Math.cos(s * twopi / divisionsV))
                                    * Math.cos(t * twopi / divisionsU)),
                            (float)((width + height * Math.cos(s * twopi / divisionsV))
                                    * Math.sin(t * twopi / divisionsU)),
                            (float)(height * Math.sin(s * twopi / divisionsV)));
                    // curr2.uv.set((float) s, 0);
                    i1 = builder.vertex(curr1);
                    i2 = builder.vertex(curr2);
                    builder.rect(i4, i2, i1, i3);
                    i4 = i2;
                    i3 = i1;
                }
            }
        }
    }
}