package xlight.engine.ecs.component;

public interface XComponentMatcherBuilder {
    XComponentMatcherBuilder all(Class<?> ... components);
    XComponentMatcherBuilder one(Class<?> ... components);
    XComponentMatcherBuilder exclude(Class<?> ... components);
    XComponentMatcher build();
    /** With id makes it possible to have more than 1 matcher with the same configuration.
     * if listener is not null, entity list won't be created, and it will be your responsibility to add and remove entities.
     * Exception will occur if you try to obtain the same matcher multiple times, sharing is not allowed.
     */
    XComponentMatcher build(int id, XComponentMatcher.XComponentMatcherListener listener);
    void reset();
}