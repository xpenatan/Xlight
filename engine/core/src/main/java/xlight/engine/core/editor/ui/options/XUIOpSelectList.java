package xlight.engine.core.editor.ui.options;

public class XUIOpSelectList {

    private static final XUIOpSelectList op = new XUIOpSelectList();

    public static XUIOpSelectList get() {
        op.reset();
        return op;
    }

    public String tooltip;

    public int value;

    public void reset() {
        tooltip = "";
    }
}