package xlight.engine.impl;

import com.badlogic.gdx.utils.Array;
import xlight.engine.core.register.XMetaClass;

public class XMetaClassImpl implements XMetaClass {

    private int key;
    private Class<?> type;
    private Class<?> parentType;
    private String name = "";
    public final Array<String> groups;
    public boolean noGroup;

    public XMetaClassImpl(int key, Class<?> classType) {
        this.key = key;
        this.type = classType;
        name = classType.getSimpleName();
        groups = new Array<>();
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public Class<?> getParentType() {
        if(parentType == null) {
            return type;
        }
        return parentType;
    }

    @Override
    public void setParentType(Class<?> parentType) {
        this.parentType = parentType;
    }

    @Override
    public int getKey() {
        return key;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setMetaClassGroup(String... name) {
        groups.clear();
        if(name == null) {
            noGroup = true;
        }
        else {
            groups.addAll(name);
        }
    }

    public Array<String> getGroups() {
        if(noGroup) {
            return null;
        }
        return groups;
    }
}