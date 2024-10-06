package xlight.engine.pool;

public interface XClassOrInterface {
    default Class<?> getClassOrInterfaceType() { return getClass(); }
}