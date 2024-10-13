package xlight.engine.imgui.ui.filebrowser;

import com.badlogic.gdx.utils.Array;

public interface XFileOpenListener {
    Array<String> allowFile(XFile file);
    void onOpenFile(XFile file, int index);
}