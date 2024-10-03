package xlight.editor;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
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
        System.out.println("Add Demo: " + demoPath + " " + demoMainClass);
        TeaReflectionSupplier.addReflectionClass(demoMainClass);
        String projectPathStr = null;
        try {
            projectPathStr = new File("../../" + demoPath).getCanonicalPath().replace("\\", "/");
            String demoCompiledClassesPath = projectPathStr + "/core/build/classes/java/main";
            FileHandle buildPath = new FileHandle(demoCompiledClassesPath);
            System.out.println("Demo build path: " + buildPath);

            FileHandle[] list = buildPath.list();
            for(int i = 0; i < list.length; i++) {
                System.out.println("Dir: " + list[i]);
            }
            AssetFilter filter = (file, isDirectory, op) -> {
                if(file.contains(demoPath + "/core/") || file.contains(demoPath + "/desktop/") || file.contains(demoPath + "/web/")) {
                    return false;
                }
                return true;
            };

            // Will copy the demo path folders to assets
            webConfiguration.assetsPath.add(AssetFileHandle.createCopyHandle(projectPathStr, Files.FileType.Local, demoPath, filter));

            // Add the demo path build classes so teavm know what to compile
            webConfiguration.additionalClasspath.add(new File(demoCompiledClassesPath).toURL());
        } catch(IOException e) {
            throw new RuntimeException("Error adding web demo", e);
        }
    }
}