package xlight.engine.g3d.model;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.utils.LongMap;

public class XMaterial extends Material {

    private final LongMap<Attribute> disabledAttribute = new LongMap<>();

    public XMaterial(final Material copyFrom) {
        super(copyFrom.id, copyFrom);
    }

    @Override
    public Material copy() {
        return new XMaterial(this);
    }

    public void enableAttribute(Attribute att, boolean flag) {
        if(flag) {
            Attribute attribute = disabledAttribute.remove(att.type);
            if(attribute != null) {
                set(attribute);
            }
        }
        else {
            Attribute attribute = get(att.type);
            if(attribute != null) {
                remove(attribute.type);
                disabledAttribute.put(attribute.type, attribute);
            }
        }
    }

    public boolean isAttributeDisabled(Attribute attribute) {
        return disabledAttribute.containsKey(attribute.type);
    }

    public final <T extends Attribute> T getAttribute (Class<T> clazz, final long type) {
        T t = (T)get(type);
        if(t == null) {
            t = (T)disabledAttribute.get(type);
        }
        return t;
    }
}