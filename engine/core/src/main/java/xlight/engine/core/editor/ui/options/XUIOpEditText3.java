package xlight.engine.core.editor.ui.options;

import com.badlogic.gdx.math.Vector3;

public class XUIOpEditText3 {

    private static final XUIOpEditText3 op = new XUIOpEditText3();

    public static XUIOpEditText3 get() {
        op.reset();
        return op;
    }

    public String label1;
    public String label2;
    public String label3;

    public String tooltip1;
    public String tooltip2;
    public String tooltip3;

    public final Vector3 value = new Vector3();

    public void reset() {
        label1 = "X:";
        label2 = "Y:";
        label3 = "Z:";
        tooltip1 = "";
        tooltip2 = "";
        tooltip3 = "";
    }
}