package xlight.editor.imgui.ecs.system.inspector;

import com.badlogic.gdx.utils.Array;
import imgui.ImGui;
import xlight.engine.core.register.XMetaClass;
import xlight.engine.core.register.XRegisterManager;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.component.XComponent;
import xlight.engine.ecs.entity.XEntity;
import xlight.engine.pool.XPool;
import xlight.engine.pool.XPoolController;

public class XUIComponentsWidget {

    private final XPoolController poolController;
    public Array<XUIComponentGroup> componentGroups;

    private boolean toInit;

    public XUIComponentsWidget(XPoolController poolController) {
        this.poolController = poolController;
        componentGroups = new Array<>();

        poolController.registerPool(XUIComponentGroup.class, new XPool<XUIComponentGroup>() {
            @Override
            protected XUIComponentGroup newObject() {
                return new XUIComponentGroup(poolController);
            }
        });

        clear();
    }

    public void clear() {
        for(int i = 0; i < componentGroups.size; i++) {
            XUIComponentGroup componentGroup = componentGroups.get(i);
            poolController.releaseObject(XUIComponentGroup.class, componentGroup);
        }
        componentGroups.clear();
        toInit = true;
    }

    public boolean render(XWorld world, XEntity entity) {
        if(toInit) {
            toInit = false;
            createModel(world);
        }

        for(XUIComponentGroup componentGroup : componentGroups) {
            XMetaClass metaClass = renderMenuRecursive(componentGroup);
            if(metaClass != null) {
                Class<?> parentType = metaClass.getParentType(); // Return parent or the real type
                if(!entity.containsComponent(parentType)) {
                    Class<?> type = metaClass.getType();
                    Object o = poolController.obtainObject(type);
                    if(o instanceof XComponent) {
                        entity.attachComponent((XComponent)o);
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    private XMetaClass renderMenuRecursive(XUIComponentGroup componentGroup) {
        XMetaClass metaClass = null;
        String name = componentGroup.getName();
        int size = componentGroup.children.size;
        boolean component = componentGroup.isComponent();
        if(component) {
            if(ImGui.MenuItem(name)) {
                metaClass = componentGroup.metaClass;
            }
        }
        else {
            if(ImGui.BeginMenu(name)) {
                for(XUIComponentGroup child : componentGroup.children) {
                    metaClass = renderMenuRecursive(child);
                    if(metaClass != null) {
                        break;
                    }
                }
                ImGui.EndMenu();
            }
        }
        return metaClass;
    }

    private void createModel(XWorld world) {
        XRegisterManager registerService = world.getManager(XRegisterManager.class);
        for(XMetaClass metaClass : registerService.getRegisteredClasses()) {
            Array<XUIComponentGroup> curGroups = componentGroups;
            Array<String> groups = metaClass.getGroups();
            if(groups.size == 0) {
                XUIComponentGroup componentGroup = findOrCreate(curGroups, "Others");
                componentGroup.addMetaClass(metaClass);
            }
            else {
                XUIComponentGroup lastGroup = null;
                for(int i = 0; i < groups.size; i++) {
                    String groupName = groups.get(i);
                    lastGroup = findOrCreate(curGroups, groupName);
                    curGroups = lastGroup.children;
                }
                lastGroup.addMetaClass(metaClass);
            }
        }

        sortArray(componentGroups);
    }

    private void sortArray(Array<XUIComponentGroup> componentGroups) {
        if(componentGroups.size == 0) {
            return;
        }

        for(XUIComponentGroup componentGroup : componentGroups) {
            sortArray(componentGroup.children);
        }
        componentGroups.sort();
    }

    private XUIComponentGroup findOrCreate(Array<XUIComponentGroup> componentGroups, String groupName) {
        XUIComponentGroup componentGroup = XUIComponentGroup.get(componentGroups, groupName);
        if(componentGroup == null) {
            componentGroup = poolController.obtainObject(XUIComponentGroup.class);
            componentGroup.name = groupName;
            componentGroups.add(componentGroup);
        }
        return componentGroup;
    }
}