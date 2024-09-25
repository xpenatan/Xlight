package xlight.engine.core.editor.ui.options;

public class XUIOpButton {

    private static final XUIOpButton op = new XUIOpButton();

    public static XUIOpButton get() {
        op.reset();
        return op;
    }

    public String tooltip;

    public void reset() {
        tooltip = "";
    }
}