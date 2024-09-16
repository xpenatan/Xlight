package xlight.editor.core.project;

import com.badlogic.gdx.utils.Array;
import java.lang.reflect.InvocationTargetException;
import xlight.engine.core.XApplication;

public class XAppLoader {

    private XClassLoader classLoader;

    public XAppLoader(Array<String> binaryPaths) {
        classLoader = new XClassLoader(binaryPaths);
    }

    public XApplication create(String applicationClassPath) {
        Class<XApplication> gamePackageClass = null;
        try {
            gamePackageClass = (Class<XApplication>)classLoader.loadClass(applicationClassPath.trim());
            return gamePackageClass.getDeclaredConstructor().newInstance();
        } catch(ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch(InstantiationException e) {
            throw new RuntimeException(e);
        } catch(IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch(InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch(NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
