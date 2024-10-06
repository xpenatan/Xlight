package xlight.engine.transform.ecs.component;

import com.badlogic.gdx.math.Vector3;
import xlight.engine.core.editor.ui.XUIData;
import xlight.engine.core.editor.ui.options.XUIOpTransform;
import xlight.engine.datamap.XDataMap;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.ecs.entity.XEntityService;
import xlight.engine.list.XIntSetNode;
import xlight.engine.list.XList;
import xlight.engine.transform.XTransform;
import xlight.engine.transform.XTransformListener;
import xlight.engine.transform.XTransformType;

public class XTransformComponent extends XLocalTransformComponent {
    private static final int DATA_SIZE = 4;
    private static final int DATA_OFFSET = 5;

    private XEntityService entityService;
    private XEntity entity;

    private XTransformListener listener = new XTransformListener() {
        @Override
        public void onUpdate(XTransformType type, XTransform transform) {
            if(entity != null) {
                XList<XIntSetNode> list = entity.getChildList();
                for(XIntSetNode node : list) {
                    int entityId = node.getKey();
                    XEntity childEntity = entityService.getEntity(entityId);
                    if(childEntity.isAttached()) {

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
}