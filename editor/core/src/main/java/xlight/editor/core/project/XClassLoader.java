package xlight.editor.core.project;

import com.badlogic.gdx.utils.Array;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;

public class XClassLoader extends ClassLoader {
    private Set<String> loadedClasses = new HashSet<>();
    private Set<String> unavaiClasses = new HashSet<>();
    private ClassLoader parent;

    private Set<String> ignorePath = new HashSet<>();

    boolean debug = false;

    private Array<File> classPathBin = new Array<File>();

    private XClassLoader(Array<String> paths, ClassLoader parent) {
        this.parent = parent;

        for(int i = 0; i < paths.size; i++) {
            String path = paths.get(i);
            File file = new File(path);
            classPathBin.add(file);
        }
    }

    public XClassLoader(Array<String> paths) {
        this(paths, XClassLoader.class.getClassLoader());
    }

    public void addIgnorPathOrClassses(String... classes) {
        for(String classe : classes) {
            ignorePath.add(classe);
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if(loadedClasses.contains(name)) {
            if(debug)
                System.out.println("Class already loaded: " + name);
            return super.loadClass(name); // Use default CL cache
        }
        else if(unavaiClasses.contains(name)) {
            if(debug)
                System.out.println("Class is not from this ClassLoader: " + name);
            return super.loadClass(name); // Use default CL cache
        }

        ClassNotFoundException error = null;
        for(int i = 0; i < classPathBin.size; i++) {
            File targetDir = classPathBin.get(i);
            try {
                return loadclasss(name, targetDir);
            }
            catch(ClassNotFoundException e) {
                error = e;
            }
        }
        throw error;
    }

    private Class<?> loadclasss(String name, File target) throws ClassNotFoundException {
        if(debug)
            System.out.println("Loading new class:" + name);

        byte[] newClassData = loadNewClass(name, target);

        if(newClassData != null) {

            if(debug) {
                System.out.println("Succesfuly load class: " + name);
                System.out.println("At target: " + target.getAbsolutePath());
            }

            loadedClasses.add(name);
            return loadClass(newClassData, name);
        }
        else {
            if(debug)
                System.out.println("Trying to load class from parent");

            Class<?> loadClass = null;
            try {

                loadClass = parent.loadClass(name);
                if(debug)
                    System.out.println("Class loaded from parent: " + name);
                unavaiClasses.add(name);
            }
            catch(ClassNotFoundException e) {
                throw new ClassNotFoundException("", e);
            }
            return loadClass;
        }
    }

    private byte[] loadNewClass(String name, File target) {
        if(ignorePath.size() > 0) {
            for(String path : ignorePath) {
                boolean flag = name.contains(path);

                if(flag)
                    return null;
            }
        }
        byte[] classLoad = classLoad(target, toFilePath(name));
        return classLoad;
    }

    // classPath file: D:\\Dev\\Projects\\ProjectEditor\\bin
    // filePath: com/editor/MyObject.class
    private static byte[] classLoad(final File classpath, String filePath) {
        File file = findFile(filePath, classpath);
        if(file == null) {
            return null;
        }
        byte[] mybyte = null;
        try {
            URI uri = file.toURI();
            URL myUrl = uri.toURL();
            URLConnection connection = myUrl.openConnection();
            InputStream input = connection.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int data = input.read();

            while(data != -1) {
                buffer.write(data);
                data = input.read();
            }
            input.close();
            byte[] classData = buffer.toByteArray();

            mybyte = classData;
        }
        catch(MalformedURLException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        return mybyte;
    }

    private static File findFile(String filePath, File classPath) {
        File file = new File(classPath, filePath);
        return file.exists() ? file : null;
    }

    private Class<?> loadClass(byte[] classData, String name) {
        Class<?> clazz = defineClass(name, classData, 0, classData.length);
        if(clazz != null) {
            if(clazz.getPackage() == null) {
                definePackage(name.replaceAll("\\.\\w+$", ""), null, null, null, null, null, null, null);
            }
            resolveClass(clazz);
        }
        return clazz;
    }

    private static String toFilePath(String name) {
        return name.replaceAll("\\.", "/") + ".class";
    }

    @SuppressWarnings("rawtypes")
    public static Object newInstance(Class clazz) {
        try {
            return clazz.newInstance();
        }
        catch(InstantiationException e) {
            Throwable cause = e.getCause();
            if(cause == null) {
                cause = e;
            }
            throw new RuntimeException(cause);
        }
        catch(IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    private static Method getMethod(String methodName, Class clazz) {
        for(Method method : clazz.getMethods()) {
            if(method.getName().equals(methodName)) {
                return method;
            }
        }
        if(!clazz.equals(Object.class)) {
            Class superclass = clazz.getSuperclass();
            if(superclass != null) {
                return getMethod(methodName, superclass);
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T invoke(Method method, Object o, Object... params) {
        try {
            return (T)method.invoke(o, params);
        }
        catch(IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        catch(InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    /**
     * Invoke a method by passing name, object and parameter.
     */
    public static Object invoke(String methodName, Object o, Object... params) {
        return invoke(getMethod(methodName, o.getClass()), o, params);
    }
}
