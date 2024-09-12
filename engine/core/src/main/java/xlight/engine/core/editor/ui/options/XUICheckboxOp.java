package xlight.engine.core.editor.ui.options;

public class XUICheckboxOp {

    private static final XUICheckboxOp op = new XUICheckboxOp();

    public static XUICheckboxOp get() {
        op.reset();
        return op;
    }

    public String label;

    public String tooltip;

    public void reset() {
        label = "###input";
        tooltip = "";
    }
}