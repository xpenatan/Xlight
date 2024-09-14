package xlight.engine.esc.component;

import xlight.engine.ecs.XECSWorld;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.ecs.entity.XEntity;

public class PlayerComponent implements XComponent {

    public int attach = 0;
    public int detach = 0;

    @Override
    public void onAttach(XECSWorld world, XEntity Entity) {
        attach++;
    }

    @Override
    public void onDetach(XECSWorld world, XEntity Entity) {
        detach++;
    }
}