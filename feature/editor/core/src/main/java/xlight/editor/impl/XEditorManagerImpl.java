package xlight.editor.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.github.xpenatan.gdx.multiview.EmuFiles;
import xlight.editor.core.ecs.XGameState;
import xlight.engine.core.XEngine;
import xlight.editor.core.ecs.manager.XEditorManager;
import xlight.engine.ecs.XECSWorld;
import xlight.engine.ecs.manager.XManager;

class XEditorManagerImpl implements XEditorManager, XManager {

    public XEngine gameEngine;
    public Input defaultInput;
    public InputMultiplexer defaultMultiplexer;
    public EmuFiles rootEmuFiles;

    boolean overrideGameCamera = true;
    boolean overrideUICamera = true;

    public XGameState gameState = XGameState.PLAY;

    @Override
    public void onAttach(XECSWorld world) {


        defaultInput = Gdx.input; // cache the default input
        defaultMultiplexer = new InputMultiplexer();
        defaultInput.setInputProcessor(defaultMultiplexer);

    }

    @Override
    public void onDetach(XECSWorld world) {

    }

    @Override
    public XEngine getGameEngine() {
        return gameEngine;
    }

    @Override
    public Input getDefaultInput() {
        return defaultInput;
    }

    @Override
    public InputMultiplexer getDefaultMultiplexer() {
        return defaultMultiplexer;
    }

    @Override
    public XGameState getGameState() {
        return gameState;
    }

    @Override
    public boolean shouldOverrideGameCamera() {
        return overrideGameCamera;
    }

    @Override
    public boolean shouldOverrideUICamera() {
        return overrideUICamera;
    }
}