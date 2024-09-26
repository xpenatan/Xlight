package xlight.engine.core.editor.ui;

/**
 * UI listener to be registered for component, manager or system
 */
public interface XUIDataTypeListener<T> {
    void onUIDraw(T value, XUIData uiData);
}