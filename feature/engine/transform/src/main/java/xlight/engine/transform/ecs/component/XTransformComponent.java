package xlight.engine.transform.ecs.component;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import xlight.engine.core.editor.ui.XUIData;
import xlight.engine.core.editor.ui.options.XUIOpTransform;
import xlight.engine.datamap.XDataMap;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.list.XIntSetNode;
import xlight.engine.list.XList;
import xlight.engine.math.XMath;
import xlight.engine.transform.XTransform;
import xlight.engine.transform.XTransformListener;

public class XTransformComponent extends XLocalTransformComponent {
    private static final int DATA_SIZE = 4;
    private static final int DATA_OFFSET = 5;

    public static final int LISTENER_CODE_UPDATE_CHILD_TRANSFORM = -1;
    public static final int LISTENER_CODE_LOCAL_TRANSFORM_CHANGED = -2;
    public static final int LISTENER_CODE_LOCAL_TRANSFORM_UPDATE_OFFSET = -3;

    private XEntityService entityService;
    private XEntity entity;

    private XTransformListener listener = new XTransformListener() {
        @Override
        public void onChange(XTransform transform, int code) {
            if(entity != null) {
                XLocalTransformComponent localTransformComponent = entity.getComponent(XLocalTransformComponent.class);
                XEntity parent = entity.getParent();
                if(code != LISTENER_CODE_UPDATE_CHILD_TRANSFORM &&
                        code != LISTENER_CODE_LOCAL_TRANSFORM_CHANGED
                        && parent != null && localTransformComponent != null) {
                    // code != LISTENER_CODE_UPDATE_CHILD_TRANSFORM prevents a scaling bug from happening
                    XTransformComponent parentTransformComponent = parent.getComponent(XTransformComponent.class);
                    if(parentTransformComponent != null) {
                        updateTransformOffset(parentTransformComponent.transform, transform, localTransformComponent.transform);
                    }
                }
                XList<XIntSetNode> list = entity.getChildList();
                for(XIntSetNode node : list) {
                    int entityId = node.getKey();
                    XEntity childEntity = entityService.getEntity(entityId);
                    if(childEntity.isAttached()) {
                        updateTransformChildEntity(transform, childEntity, true);
                    }
                }
            }
        }
    };

    @Override
    public void onAttach(XWorld world, XEntity entity) {
        this.entity = entity;
        entityService = world.getWorldService().getEntityService();
        transform.addTransformListener(listener);
    }

    @Override
    public void onDetach(XWorld world, XEntity entity) {
        this.entity = null;
        entityService = null;
        transform.removeTransformListener(listener);
    }

    @Override
    public void onSave(XDataMap map) {
        if(entity != null) {
            // If local transform exist there is no need to save this component
            if(entity.containsComponent(XLocalTransformComponent.class)) {
                return;
            }
        }

        super.onSave(map);

        Vector3 size = transform.getSize();
        map.put(DATA_SIZE, size);

        Vector3 offset = transform.getOffset();
        map.put(DATA_OFFSET, offset);
    }

    @Override
    public void onLoad(XDataMap map) {
        super.onLoad(map);

        Vector3 size = transform.getSize();
        map.getVector3(DATA_SIZE, size);
        transform.setSize(size);

        Vector3 offset = transform.getOffset();
        map.getVector3(DATA_OFFSET, offset);
        transform.setOffset(offset);
    }

    @Override
    public void onUIDraw(XUIData uiData) {
        XUIOpTransform op = XUIOpTransform.get();
        uiData.transform(transform, op);
    }

    private static boolean updateTransformChildEntity(XTransform parentTransform, XEntity childEntity, boolean callListener) {
        XLocalTransformComponent childLocalTransformComponent = childEntity.getComponent(XLocalTransformComponent.class);
        XTransformComponent childTransformComponent = childEntity.getComponent(XTransformComponent.class);
        if (childLocalTransformComponent != null && childTransformComponent != null) {
            XTransform childLocalTransform = childLocalTransformComponent.transform;
            XTransform childTransform = childTransformComponent.transform;
            calculateWorldTransform(parentTransform, childLocalTransform, childTransform, callListener);
            return true;
        }
        return false;
    }

    private static void updateTransformOffset(XTransform parentTransform, XTransform transform, XTransform localTransform) {
        Vector3 parentPosition = parentTransform.getPosition();
        Quaternion parentRotation = parentTransform.getQuaternion();
        Vector3 parentScale = parentTransform.getScale();

        Vector3 worldPosition = transform.getPosition();
        Quaternion worldRotation = transform.getQuaternion();
        Vector3 worldScale = transform.getScale();

        // 1. Calculate Local Position
        Vector3 localPosition = XMath.VEC3_1.set(worldPosition).sub(parentPosition); // Relative to parent
        localPosition = XMath.QUAT_1.set(parentRotation).conjugate().transform(localPosition); // Unrotate by parent
        localPosition.scl(1f / parentScale.x, 1f / parentScale.y, 1f / parentScale.z); // Unscale by parent
        float localPosX = localPosition.x;
        float localPosY = localPosition.y;
        float localPosZ = localPosition.z;

        // 2. Calculate Local Rotation
        Quaternion localRotation = XMath.QUAT_1.set(worldRotation).mulLeft(XMath.QUAT_2.set(parentRotation).conjugate());
        float localRotationX = localRotation.x;
        float localRotationY = localRotation.y;
        float localRotationZ = localRotation.z;
        float localRotationW = localRotation.w;

        // 3. Calculate Local Scale
        Vector3 localScale = XMath.VEC3_1.set(worldScale).scl(1f / parentScale.x, 1f / parentScale.y, 1f / parentScale.z);
        float localScaleX = localScale.x;
        float localScaleY = localScale.y;
        float localScaleZ = localScale.z;

        // Apply calculated local transformations
        localTransform.setPosition(localPosX, localPosY, localPosZ, false);
        localTransform.setRotation(localRotationX, localRotationY, localRotationZ, localRotationW, false);
        localTransform.setScale(localScaleX, localScaleY, localScaleZ, false);
    }

    private static void calculateWorldTransform(XTransform parentTransform, XTransform childLocalTransform, XTransform childTransform, boolean callListener) {
        Vector3 parentPosition = parentTransform.getPosition();
        Quaternion parentRotation = parentTransform.getQuaternion();
        Vector3 parentScale = parentTransform.getScale();

        Vector3 localPosition = childLocalTransform.getPosition();
        Quaternion localRotation = childLocalTransform.getQuaternion();
        Vector3 localScale = childLocalTransform.getScale();

        // 1. Scale Local Position by Parent Scale
        Vector3 scaledLocalPosition = XMath.VEC3_1.set(localPosition).scl(parentScale);

        // 2. Rotate Scaled Position by Parent Rotation
        Vector3 worldPosition = XMath.QUAT_1.set(parentRotation).transform(scaledLocalPosition);

        // 3. Add Parent Position
        worldPosition.add(parentPosition);
        float worldX = worldPosition.x;
        float worldY = worldPosition.y;
        float worldZ = worldPosition.z;

        // 4. Calculate World Rotation
        Quaternion worldRotation = XMath.QUAT_1.set(parentRotation).mul(localRotation);
        float worldRotationX = worldRotation.x;
        float worldRotationY = worldRotation.y;
        float worldRotationZ = worldRotation.z;
        float worldRotationW = worldRotation.w;

        // 5. Calculate World Scale
        Vector3 worldScale = XMath.VEC3_1.set(localScale).scl(parentScale);
        float worldScaleX = worldScale.x;
        float worldScaleY = worldScale.y;
        float worldScaleZ = worldScale.z;

        // Apply transformations
        childTransform.setPosition(worldX, worldY, worldZ, false);
        childTransform.setRotation(worldRotationX, worldRotationY, worldRotationZ, worldRotationW, false);
        childTransform.setScale(worldScaleX, worldScaleY, worldScaleZ, false);

        // Notify listeners if required
        if (callListener) {
            childTransform.callOnChangeListeners(LISTENER_CODE_UPDATE_CHILD_TRANSFORM);
        }
    }
}