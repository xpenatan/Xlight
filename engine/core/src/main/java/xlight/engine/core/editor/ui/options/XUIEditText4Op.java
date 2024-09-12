package xlight.engine.core.editor.ui.options;

public class XUIEditText4Op {

    private static final XUIEditText4Op op = new XUIEditText4Op();

    public static XUIEditText4Op get() {
        op.reset();
        return op;
    }

    public String label1;
    public String label2;
    public String label3;
    public String label4;

    public String tooltip1;
    public String tooltip2;
    public String tooltip3;
    public String tooltip4;

    public void reset() {
        label1 = "X:";
        label2 = "Y:";
        label3 = "Z:";
        label4 = "W:";
        tooltip1 = "";
        tooltip2 = "";
        tooltip3 = "";
        tooltip4 = "";
    }
}