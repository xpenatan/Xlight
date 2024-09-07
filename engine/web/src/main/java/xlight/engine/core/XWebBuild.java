package xlight.engine.core;

import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder;
import org.teavm.tooling.TeaVMTool;
import org.teavm.vm.TeaVMOptimizationLevel;

public class XWebBuild {

    public XWebBuild(XWebBuildConfiguration webConfiguration) {
        TeaVMTool tool = TeaBuilder.config(webConfiguration);
        tool.setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE);
        tool.setMainClass(webConfiguration.mainClass);
        tool.setObfuscated(false);
        TeaBuilder.build(tool);
    }
}