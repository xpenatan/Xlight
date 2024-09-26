package xlight.engine.core.editor.ui.options;

public class XUIOpCheckbox {

    private static final XUIOpCheckbox op = new XUIOpCheckbox();

    public static XUIOpCheckbox get() {
        op.reset();
        return op;
    }

    public String label;

    public String tooltip;

    public boolean value;

    public void reset() {
        label = "###input";
        tooltip = "";
        value = false;
    }
}