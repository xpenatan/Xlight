package xlight.engine.core.editor.ui.options;

public class XUIOpStringEditText {

    private static final XUIOpStringEditText op = new XUIOpStringEditText();

    public static XUIOpStringEditText get() {
        op.reset();
        return op;
    }

    public int width;
    public String label;
    public String tooltip;

    public String value;

    public void reset() {
        label = "";
        tooltip = "";
        width = -1;
    }
}