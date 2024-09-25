package xlight.engine.core.editor.ui;

/**
 * Listener to be implemented inside component, manager or system.
 * Used to add UI elements when selecting them inside the editor
 */
public interface XUIDataListener {
    void onUIDraw(XUIData uiData);
}