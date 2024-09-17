package emu;

import com.badlogic.gdx.utils.Array;
import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;
import xlight.engine.core.XApplication;

@Emulate(valueStr = "xlight.editor.core.project.XAppLoader")
public class XAppLoaderEmu {

    public XAppLoaderEmu(Array<String> binaryPaths) {
    }

    public XApplication create(String applicationClassPath) {
        try {
            Class<?> aClass = Class.forName(applicationClassPath);
            return (XApplication)aClass.newInstance();
        } catch(ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch(InstantiationException e) {
            throw new RuntimeException(e);
        } catch(IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
