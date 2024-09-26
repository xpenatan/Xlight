package xlight.engine.core.util;

public class XClassHelper {

    public static boolean classExtends(Class<?> parentClass, Class<?> childClass) {
        return parentClass.isAssignableFrom(childClass);
    }
}
