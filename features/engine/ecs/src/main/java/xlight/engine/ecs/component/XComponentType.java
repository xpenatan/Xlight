package xlight.engine.ecs.component;

public class XComponentType {
    private int index;
    private Class<? extends XComponent> type;

    public XComponentType(Class<? extends XComponent> type, int index) {
        this.type = type;
        this.index = index;
    }

    public Class<? extends XComponent> getType() {
        return type;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "[index: " + index + ", type: " + type.getSimpleName() + "]";
    }
}