package xlight.editor.core.project;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.utils.Array;
import java.lang.reflect.InvocationTargetException;

public class XAppLoader {

    private XClassLoader classLoader;

    public XAppLoader(Array<String> binaryPaths) {
        classLoader = new XClassLoader(binaryPaths);
    }

    public ApplicationListener create(String applicationClassPath) {
        Class<ApplicationListener> gamePackageClass = null;
        try {
            gamePackageClass = (Class<ApplicationListener>)classLoader.loadClass(applicationClassPath.trim());
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
