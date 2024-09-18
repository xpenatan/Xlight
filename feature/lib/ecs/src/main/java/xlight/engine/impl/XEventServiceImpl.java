package xlight.engine.impl;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.event.XEventListener;
import xlight.engine.ecs.event.XEventService;
import xlight.engine.pool.XPool;

class XEventServiceImpl implements XEventService {

    private final XWorld world;
    private final Array<XEventImpl> toAdd;
    private final Array<XEventImpl> processing;
    private final IntMap<Array<XEventListener>> listenersMap;
    private final XPool<XEventImpl> eventPool;

    public XEventServiceImpl(XWorld world) {
        this.world = world;
        toAdd = new Array<>();
        processing = new Array<>();
        listenersMap = new IntMap<>();
        eventPool = new XPool<XEventImpl>(30, 100) {
            @Override
            public XEventImpl newObject() {
                return new XEventImpl();
            }
        };
    }

    @Override
    public void sendEvent(int id) {
        sendEvent(id, null);
    }

    @Override
    public void sendEvent(int id, Object userData) {
        sendEvent(id, userData, null, true);
    }

    @Override
    public void sendEvent(int id, XSendEventListener listener) {
        sendEvent(id, null, listener, true);
    }

    @Override
    public void sendEvent(int id, Object userData, XSendEventListener listener) {
        sendEvent(id, userData, listener, true);
    }

    @Override
    public void sendEvent(int id, Object userData, XSendEventListener listener, boolean isAsync) {
        XEventImpl event = eventPool.obtain();
        event.id = id;
        event.userData = userData;
        event.listener = listener;
        event.world = world;

        if(isAsync) {
            toAdd.add(event);
        }
        else {
            updateEvent(event);
            eventPool.free(event);
        }
    }

    @Override
    public void clear() {
        for(XEventImpl event : processing) {
            eventPool.free(event);
        }
        processing.clear();
        for(XEventImpl event : toAdd) {
            eventPool.free(event);
        }
        toAdd.clear();
    }

    @Override
    public boolean addEventListener(int eventID, XEventListener listener) {
        Array<XEventListener> eventArray = listenersMap.get(eventID);
        if(eventArray == null) {
            eventArray = new Array<>();
            listenersMap.put(eventID, eventArray);
        }
        if(!eventArray.contains(listener, true)) {
            eventArray.add(listener);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeEventListener(int eventID, XEventListener listener) {
        Array<XEventListener> eventArray = listenersMap.get(eventID);
        if(eventArray != null) {
            return eventArray.removeValue(listener, true);
        }
        return false;
    }

    void update() {
        for(int i = 0; i < toAdd.size; i++) {
            XEventImpl event = toAdd.get(i);
            processing.add(event);
        }
        toAdd.clear();

        for(int i = 0; i < processing.size; i++) {
            XEventImpl event = processing.get(i);
            updateEvent(event);
            processing.removeIndex(i);
            eventPool.free(event);
            i--;
        }
    }

    private void updateEvent(XEventImpl event) {
        XSendEventListener eventListener = event.listener;
        if(eventListener != null) {
            eventListener.onBeginEvent(event);
        }
        Array<XEventListener> listeners = listenersMap.get(event.getId());
        if(listeners != null) {
            for(int i = 0; i < listeners.size; i++) {
                XEventListener listener = listeners.get(i);
                if(listener.onEvent(event)) {
                    listeners.removeIndex(i);
                    i--;
                }
            }
        }
        if(eventListener != null) {
            eventListener.onEndEvent(event);
        }
    }
}