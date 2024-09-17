package xlight.editor.core.ecs.manager;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import xlight.editor.core.ecs.XGameState;
import xlight.engine.core.XEngine;

public interface XEditorManager {

    XEngine getGameEngine();

    Input getDefaultInput();

    InputMultiplexer getDefaultMultiplexer();

    XGameState getGameState();

    boolean shouldOverrideGameCamera();
    boolean shouldOverrideUICamera();

    void setGameEngineError();
}