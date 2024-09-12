package xlight.engine.core.editor.ui.options;

public class XUISelectListOp {

    private static final XUISelectListOp op = new XUISelectListOp();

    public static XUISelectListOp get() {
        op.reset();
        return op;
    }

    public String tooltip;

    public void reset() {
        tooltip = "";
    }
}