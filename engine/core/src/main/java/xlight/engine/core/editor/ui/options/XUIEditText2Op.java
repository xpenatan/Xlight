package xlight.engine.core.editor.ui.options;

public class XUIEditText2Op {

    private static final XUIEditText2Op op = new XUIEditText2Op();

    public static XUIEditText2Op get() {
        op.reset();
        return op;
    }

    public String label1;
    public String label2;

    public String tooltip1;
    public String tooltip2;

    public void reset() {
        label1 = "X:";
        label2 = "Y:";
        tooltip1 = "";
        tooltip2 = "";
    }
}