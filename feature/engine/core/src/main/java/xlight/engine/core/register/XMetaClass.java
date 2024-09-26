package xlight.engine.core.register;

import com.badlogic.gdx.utils.Array;

public interface XMetaClass {
    Class<?> getType();
    Class<?> getParentType();
    void setParentType(Class<?> parentType);
    int getKey();
    String getName();
    void setName(String name);

    void setMetaClassGroup(String... name);
    Array<String> getGroups();
}