package xlight.editor.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import imgui.ImGuiLoader;
import imgui.gdx.ImGuiGdxInputMultiplexer;
import xlight.editor.imgui.ecs.manager.XImGuiManager;
import xlight.editor.imgui.window.XImGuiWindowContext;
import xlight.engine.ecs.XECSWorld;
import xlight.engine.ecs.manager.XManager;
import xlight.engine.ecs.system.XSystemService;
import xlight.engine.init.ecs.service.XInitFeature;
import xlight.engine.init.ecs.service.XInitFeatureService;

public class XImGuiManagerImpl implements XImGuiManager, XManager {
    private InputMultiplexer input;

    public XImGuiWindowContext curWindowContext;

    @Override
    public void onAttach(XECSWorld world) {
        XInitFeatureService featureService = world.getService(XInitFeatureService.class);
        featureService.addFeature(XImGuiManager.FEATURE, feature -> ImGuiLoader.init(() -> {
            Gdx.app.postRunnable(() -> init(world, feature));
        }));
    }

    private void init(XECSWorld world, XInitFeature feature) {
        input = new ImGuiGdxInputMultiplexer();

        XSystemService systemService = world.getSystemService();
        systemService.attachSystem(new XImGuiSystemImpl());


        feature.initFeature();
    }

    @Override
    public InputMultiplexer getImGuiInput() {
        return input;
    }

    @Override
    public XImGuiWindowContext getCurrentWindowContext() {
        return curWindowContext;
    }
}