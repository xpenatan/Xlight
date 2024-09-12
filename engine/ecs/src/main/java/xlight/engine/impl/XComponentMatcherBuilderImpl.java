package xlight.engine.impl;

import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.GdxRuntimeException;
import xlight.engine.ecs.component.XComponentMatcher;
import xlight.engine.ecs.component.XComponentMatcherBuilder;

public class XComponentMatcherBuilderImpl implements XComponentMatcherBuilder {

    public String debugAllClasses = "";
    public String debugOneClasses = "";
    public String debugExcludeClasses = "";
    public final Bits all = new Bits();
    public final Bits one = new Bits();
    public final Bits exclude = new Bits();

    private final XComponentServiceImpl componentService;
    private final XEntityServiceImpl entityService;

    public XComponentMatcherBuilderImpl(XComponentServiceImpl componentService, XEntityServiceImpl entityService) {
        this.componentService = componentService;
        this.entityService = entityService;
    }

    @Override
    public void reset() {
        debugAllClasses = "";
        debugOneClasses = "";
        debugExcludeClasses = "";
        all.clear();
        one.clear();
        exclude.clear();
    }

    @Override
    public XComponentMatcherBuilder all(Class<?> ... components) {
        int typesLength = components.length;
        for(int i = 0; i < typesLength; i++) {
            Class<?> componentType = components[i];
            int index = componentService.getComponentIndex(componentType);
            if(index >= 0) {
                if(i > 0) {
                    debugAllClasses += ", ";
                }
                debugAllClasses += componentType.getSimpleName();
                all.set(index);
            }
        }
        return this;
    }

    @Override
    public XComponentMatcherBuilder one(Class<?> ... components) {
        int typesLength = components.length;
        for(int i = 0; i < typesLength; i++) {
            Class<?> componentType = components[i];
            int index = componentService.getComponentIndex(componentType);
            if(index >= 0) {
                if(i > 0) {
                    debugOneClasses += ", ";
                }
                debugOneClasses += componentType.getSimpleName();
                one.set(index);
            }
        }
        return this;
    }

    @Override
    public XComponentMatcherBuilder exclude(Class<?> ... components) {
        int typesLength = components.length;
        for(int i = 0; i < typesLength; i++) {
            Class<?> componentType = components[i];
            int index = componentService.getComponentIndex(componentType);
            if(index >= 0) {
                if(i > 0) {
                    debugExcludeClasses += ", ";
                }
                debugExcludeClasses += componentType.getSimpleName();
                exclude.set(index);
            }
        }
        return this;
    }

    @Override
    public XComponentMatcher build() {
        return build(0, null);
    }

    @Override
    public XComponentMatcher build(int id, XComponentMatcher.XComponentMatcherListener listener) {
        XComponentMatcher matcher = entityService.getOrCreate(id, this, listener);
        if(matcher == null) {
            throw new GdxRuntimeException("Matcher was not found. Make sure the components are registered.");
        }
        reset();
        return matcher;
    }
}