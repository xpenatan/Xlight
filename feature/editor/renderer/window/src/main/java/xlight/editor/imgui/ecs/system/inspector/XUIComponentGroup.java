package xlight.editor.imgui.ecs.system.inspector;

import com.badlogic.gdx.utils.Array;
import xlight.engine.core.register.XMetaClass;
import xlight.engine.pool.XPoolController;
import xlight.engine.pool.XPoolable;

public class XUIComponentGroup implements XPoolable, Comparable {
    public String name;
    private final XPoolController poolController;
    public Array<XUIComponentGroup> children;
    public XMetaClass metaClass;

    public XUIComponentGroup(XPoolController poolController) {
        this.poolController = poolController;
        children = new Array<>();
        onReset();
    }

    public String getName() {
        if(metaClass != null)
            return metaClass.getName();
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    public boolean isComponent() {
        return metaClass != null;
    }

    public void addMetaClass(XMetaClass metaClasse) {
        XUIComponentGroup metaClassGroup = poolController.obtainObject(XUIComponentGroup.class);
        metaClassGroup.metaClass = metaClasse;
        children.add(metaClassGroup);
    }

    public void addGroup(String name) {
        XUIComponentGroup componentGroup = get(children, name);
        if(componentGroup == null) {
            componentGroup = poolController.obtainObject(XUIComponentGroup.class);
            componentGroup.name = name;
            children.add(componentGroup);
        }
    }

    @Override
    public void onReset() {
        for(int i = 0; i < children.size; i++) {
            XUIComponentGroup componentGroup = children.get(i);
            poolController.releaseObject(XUIComponentGroup.class, componentGroup);
        }
        children.clear();
        metaClass = null;
        name = "";
    }

    public static XUIComponentGroup get(Array<XUIComponentGroup> componentGroups, String groupName) {
        for(XUIComponentGroup componentGroup : componentGroups) {
            if(componentGroup.getName().equals(groupName)) {
                return componentGroup;
            }
        }
        return null;
    }

    @Override
    public int compareTo(Object o) {
        return toString().compareTo(o.toString());
    }
}