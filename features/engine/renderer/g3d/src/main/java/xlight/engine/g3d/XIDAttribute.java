package xlight.engine.g3d;

import com.badlogic.gdx.graphics.g3d.Attribute;

public class XIDAttribute extends Attribute {

    public final static String SelectAttribute = "SelectAttribute";
    public final static long SelectAttributeType = register(SelectAttribute);

    public int id;

    public XIDAttribute() {
        super(SelectAttributeType);
    }

    public XIDAttribute(final XIDAttribute copyFrom) {
        super(copyFrom.type);
    }

    @Override
    public Attribute copy() {
        return new XIDAttribute(this);
    }

    public int compareTo(Attribute o) {
        if(type != o.type) return type < o.type ? -1 : 1;
        return 0; // FIXME implement comparing
    }
}
