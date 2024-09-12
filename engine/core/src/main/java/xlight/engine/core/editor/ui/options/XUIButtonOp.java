package xlight.engine.core.editor.ui.options;

public class XUIButtonOp {

    private static final XUIButtonOp op = new XUIButtonOp();

    public static XUIButtonOp get() {
        op.reset();
        return op;
    }

    public String tooltip;

    public void reset() {
        tooltip = "";
    }
}