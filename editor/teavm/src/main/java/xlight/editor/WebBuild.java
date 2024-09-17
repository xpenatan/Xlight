package xlight.editor;

import com.badlogic.gdx.Files;
import com.github.xpenatan.gdx.backends.teavm.config.AssetFileHandle;
import com.github.xpenatan.gdx.backends.teavm.config.AssetFilter;
import com.github.xpenatan.gdx.backends.teavm.config.plugins.TeaReflectionSupplier;
import com.github.xpenatan.gdx.backends.teavm.gen.SkipClass;
import java.io.File;
import java.io.IOException;
import xlight.engine.core.XWebBuild;
import xlight.engine.core.XWebBuildConfiguration;

@SkipClass
public class WebBuild {

    public static void main(String[] args) throws IOException {

        XWebBuildConfiguration webConfiguration = new XWebBuildConfiguration();

        addDemo(webConfiguration, "demos/g3d/basic", "xlight.demo.basic.MainApp");

        webConfiguration.mainClass = WebMain.class.getName();
        webConfiguration.webappPath = new File("build/dist").getCanonicalPath();
        new XWebBuild(webConfiguration);
    }

    private static void addDemo(XWebBuildConfiguration webConfiguration, String demoPath, String demoMainClass) {
        TeaReflectionSupplier.addReflectionClass(demoMainClass);
        String projectPathStr = null;
        try {
            projectPathStr = new File("../../" + demoPath).getCanonicalPath().replace("\\", "/");
            String demoCompiledClassesPath = projectPathStr + "/core/build/classes/java/main";
            System.out.println("SemoCompiledClassesPath: " + demoCompiledClassesPath);
            AssetFilter filter = (file, isDirectory, op) -> {
                if(file.contains(demoPath + "/core/") || file.contains(demoPath + "/desktop/") || file.contains(demoPath + "/teavm/")) {
                    return false;
                }
                return true;
            };

            // Will copy the demo path folders to assets
            webConfiguration.assetsPath.add(AssetFileHandle.createCopyHandle(projectPathStr, Files.FileType.Internal, demoPath, filter));

            // Add the demo path build classes so teavm know what to compile
            webConfiguration.additionalClasspath.add(new File(demoCompiledClassesPath).toURL());
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}