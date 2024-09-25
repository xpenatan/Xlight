package xlight.editor.window.gameeditor.ecs.system.content.selection;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Array;
import xlight.editor.core.ecs.manager.XEntitySelectionManager;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.ecs.component.XComponentService;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.g2d.XRender2D;
import xlight.engine.g2d.ecs.component.XRender2DComponent;
import xlight.engine.g3d.XRender3D;
import xlight.engine.g3d.ecs.component.XRender3DComponent;
import xlight.engine.list.XList;
import xlight.engine.outline.XOutLine2DBatch;
import xlight.engine.outline.XOutLine3DBatch;
import xlight.engine.transform.ecs.component.XTransformComponent;

public class XSelectionRenderer {

    private XOutLine2DBatch outLine2DBatch;
    private XOutLine3DBatch outLine3DBatch;

    private final Array<XRender2D> render2d = new Array<>();
    private final Array<XRender3D> render3d = new Array<>();

    private final Class<? extends XComponent> worldType;

    public XSelectionRenderer(Class<? extends XComponent> worldType) {
        outLine2DBatch = new XOutLine2DBatch();
        outLine3DBatch = new XOutLine3DBatch();
        this.worldType = worldType;
    }

    public void render(int engineType, Camera camera, XEntitySelectionManager selectionManager, XComponentService cs) {
        XList<XEntity> selectedTargets = selectionManager.getSelectedTargets();
        for(XEntity entity : selectedTargets) {
            if(entity.isVisible()) {
                XTransformComponent transformComponent = entity.getComponent(XTransformComponent.class);
                XComponent worldTypeComponent = entity.getComponent(worldType);
                if(transformComponent != null && worldTypeComponent != null) {
                    XRender2DComponent spriteComponent = cs.getComponent(entity, XRender2DComponent.class);
                    if(spriteComponent != null) {
                        render2d.add(spriteComponent);
                    }
                    else {
                        XRender3DComponent modelComponent = cs.getComponent(entity, XRender3DComponent.class);
                        if(modelComponent != null) {
                            render3d.add(modelComponent);
                        }
                    }
                }
            }
        }

        outLine2DBatch.render(engineType, camera.combined, render2d);
        outLine3DBatch.render(engineType, camera, render3d);
        render2d.clear();
        render3d.clear();
    }
}