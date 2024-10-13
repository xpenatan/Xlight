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
    public boolean enabled;

    public int lineColor;

    public String value;

    public void reset() {
        lineColor = 0; // 0 = don't change color
        label = "";
        tooltip = "";
        width = -1;
        enabled = true;
    }
}