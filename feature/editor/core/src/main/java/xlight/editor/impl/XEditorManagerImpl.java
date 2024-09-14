package xlight.editor.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.github.xpenatan.gdx.multiview.EmuFiles;
import xlight.engine.core.XEngine;
import xlight.editor.core.ecs.manager.XEditorManager;
import xlight.engine.ecs.XECSWorld;
import xlight.engine.ecs.manager.XManager;

class XEditorManagerImpl implements XEditorManager, XManager {

    public XEngine gameEngine;
    public Input defaultInput;
    public InputMultiplexer defaultMultiplexer;
    public EmuFiles rootEmuFiles;

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
}