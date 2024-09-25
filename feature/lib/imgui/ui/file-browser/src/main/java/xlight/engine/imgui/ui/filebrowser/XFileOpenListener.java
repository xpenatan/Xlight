package xlight.engine.imgui.ui.filebrowser;

public interface XFileOpenListener {
    boolean allowFile(XFile file);
    void onOpenFile(XFile file);
}