package xlight.engine.core.editor.ui.options;

public class XUIOpEditText {

    private static final XUIOpEditText op = new XUIOpEditText();

    public static XUIOpEditText get() {
        op.reset();
        return op;
    }

    public int width;
    public String label;
    public String tooltip;

    /** Only for int and float */
    public boolean enableStep;
    public float step;

    public float value;

    public void reset() {
        label = "";
        tooltip = "";
        width = -1;
        enableStep = false;
        step = 0.01f;
    }
}