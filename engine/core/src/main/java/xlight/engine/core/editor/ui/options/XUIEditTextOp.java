package xlight.engine.core.editor.ui.options;

public class XUIEditTextOp {

    private static final XUIEditTextOp op = new XUIEditTextOp();

    public static XUIEditTextOp get() {
        op.reset();
        return op;
    }

    public int width;
    public String label;
    public String tooltip;

    /** Only for int and float */
    public boolean enableStep;
    public float step;

    public void reset() {
        label = "";
        tooltip = "";
        width = -1;
        enableStep = false;
        step = 0.01f;
    }
}