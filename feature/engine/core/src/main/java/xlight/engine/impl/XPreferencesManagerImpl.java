package xlight.engine.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import xlight.engine.core.XEngineEvent;
import xlight.engine.core.ecs.XPreferencesManager;
import xlight.engine.ecs.XWorld;
import xlight.engine.ecs.event.XEvent;
import xlight.engine.ecs.event.XEventService;
import xlight.engine.ecs.manager.XManager;
import xlight.engine.pool.XPoolController;
import xlight.engine.pool.ecs.manager.XPoolManager;
import xlight.engine.properties.XProperties;

class XPreferencesManagerImpl implements XPreferencesManager, XManager, XEventService.XSendEventListener {

    private XProperties editorProperties;
    private Preferences preferences;
    private XEventService eventService;

    @Override
    public void onAttach(XWorld world) {
        XPoolController poolController = world.getManager(XPoolManager.class).getPoolController();
        eventService = world.getWorldService().getEventService();
        editorProperties = XProperties.obtain(poolController);
    }

    @Override
    public void setup(String key) {
        if(preferences == null) {
            preferences = Gdx.app.getPreferences(key);
        }
    }

    @Override
    public void putValue(String section, String key, String value) {
        validate();
        XProperties property = editorProperties.putProperties(section);
        property.put(key, value);
    }

    @Override
    public String getValue(String section, String key, String defaultValue) {
        validate();
        XProperties property = editorProperties.getProperties(section);
        if(property != null) {
            return property.get(key, defaultValue);
        }
        return null;
    }

    @Override
    public void putValue(String section, String key, boolean value) {
        validate();
        try {
            String valueStr = String.valueOf(value);
            putValue(section, key, valueStr);
        }
        catch(Throwable ignored) {}
    }

    @Override
    public boolean getValue(String section, String key, boolean defaultValue) {
        validate();
        try {
            String value = getValue(section, key, null);
            if(value != null) {
                return Boolean.parseBoolean(value);
            }
        }
        catch(Throwable ignored) {}
        return defaultValue;
    }

    @Override
    public void putValue(String section, String key, int value) {
        validate();
        try {
            String valueStr = String.valueOf(value);
            putValue(section, key, valueStr);
        }
        catch(Throwable ignored) {}
    }

    @Override
    public int getValue(String section, String key, int defaultValue) {
        validate();
        try {
            String value = getValue(section, key, null);
            if(value != null) {
                return Integer.parseInt(value);
            }
        }
        catch(Throwable ignored) {}
        return defaultValue;
    }

    @Override
    public void putValue(String section, String key, float value) {
        validate();
        try {
            String valueStr = String.valueOf(value);
            putValue(section, key, valueStr);
        }
        catch(Throwable ignored) {}
    }

    @Override
    public float getValue(String section, String key, float defaultValue) {
        validate();
        try {
            String value = getValue(section, key, null);
            if(value != null) {
                return Float.parseFloat(value);
            }
        }
        catch(Throwable ignored) {}
        return defaultValue;
    }

    @Override
    public void save() {
        validate();
        eventService.sendEvent(XEngineEvent.EVENT_SAVE_PREFERENCE, null, this);
    }

    @Override
    public void load() {
        validate();
        eventService.sendEvent(XEngineEvent.EVENT_LOAD_PREFERENCE, null, this);
    }

    @Override
    public void onBeginEvent(XEvent event) {
        int id = event.getId();
        if(id == XEngineEvent.EVENT_LOAD_PREFERENCE) {
            editorProperties.clear();
            String jsonStr = preferences.getString("XEditor");
            editorProperties.loadJson(jsonStr);
        }
    }

    @Override
    public void onEndEvent(XEvent event) {
        int id = event.getId();
        if(id == XEngineEvent.EVENT_SAVE_PREFERENCE) {
            String jsonStr = editorProperties.saveJsonStr();
            preferences.putString("XEditor", jsonStr);
            preferences.flush();
        }
    }

    private void validate() {
        if(preferences == null) {
            throw new RuntimeException("Setup was not called to initialize the manager");
        }
    }
}