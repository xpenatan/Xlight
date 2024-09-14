package xlight.editor;

import com.badlogic.gdx.Files;
import com.github.xpenatan.gdx.backends.teavm.config.AssetFileHandle;
import com.github.xpenatan.gdx.backends.teavm.gen.SkipClass;
import java.io.File;
import java.io.IOException;
import xlight.engine.core.XWebBuild;
import xlight.engine.core.XWebBuildConfiguration;

@SkipClass
public class WebBuild {

    public static void main(String[] args) throws IOException {
        XWebBuildConfiguration webConfiguration = new XWebBuildConfiguration();
        webConfiguration.mainClass = WebMain.class.getName();
        webConfiguration.webappPath = new File("build/dist").getCanonicalPath();
//        AssetFileHandle assets = AssetFileHandle.createHandle("../assets", Files.FileType.Internal);
//        webConfiguration.assetsPath.add(assets);
        new XWebBuild(webConfiguration);
    }
}