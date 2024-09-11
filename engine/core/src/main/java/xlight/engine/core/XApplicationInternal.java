package xlight.engine.core;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import xlight.engine.ecs.event.XEvent;

class XApplicationInternal implements ApplicationListener {

    public XEngine engine;
    private XApplication applicationListener;

    public XApplicationInternal(XEngine engine, XApplication applicationListener) {
        this.applicationListener = applicationListener;
        this.engine = engine;
    }

    @Override
    public void create() {
        applicationListener.onSetup(engine);

        engine.getWorld().getEventService().sendEvent(XEvent.EVENT_CREATE);
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        engine.update(deltaTime);
        engine.render();
    }

    @Override
    public void resize(int width, int height) {
        engine.getWorld().getEventService().sendEvent(XEvent.EVENT_RESIZE);
    }

    @Override
    public void pause() {
        engine.getWorld().getEventService().sendEvent(XEvent.EVENT_PAUSE);
    }

    @Override
    public void resume() {
        engine.getWorld().getEventService().sendEvent(XEvent.EVENT_RESUME);
    }

    @Override
    public void dispose() {
        engine.getWorld().getEventService().sendEvent(XEvent.EVENT_DISPOSE, null, null, false);
    }
}