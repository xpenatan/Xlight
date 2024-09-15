package xlight.engine.esc.component;

import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.ecs.entity.XEntity;

public class ComponentA implements XComponent {

    public boolean onAttach = false;
    public boolean onDetach = false;

    @Override
    public void onAttach(XWorld world, XEntity entity) {
        onAttach = true;
    }

    @Override
    public void onDetach(XWorld world, XEntity entity) {
        onDetach = false;
    }
}