package xlight.engine.core.editor.ui.options;

public class XUIEditText3Op {

    private static final XUIEditText3Op op = new XUIEditText3Op();

    public static XUIEditText3Op get() {
        op.reset();
        return op;
    }

    public String label1;
    public String label2;
    public String label3;

    public String tooltip1;
    public String tooltip2;
    public String tooltip3;

    public void reset() {
        label1 = "X:";
        label2 = "Y:";
        label3 = "Z:";
        tooltip1 = "";
        tooltip2 = "";
        tooltip3 = "";
    }
}