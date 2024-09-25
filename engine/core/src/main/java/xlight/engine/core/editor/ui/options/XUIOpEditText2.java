package xlight.engine.core.editor.ui.options;

import com.badlogic.gdx.math.Vector2;

public class XUIOpEditText2 {

    private static final XUIOpEditText2 op = new XUIOpEditText2();

    public static XUIOpEditText2 get() {
        op.reset();
        return op;
    }

    public String label1;
    public String label2;

    public String tooltip1;
    public String tooltip2;

    public final Vector2 value = new Vector2();

    public void reset() {
        label1 = "X:";
        label2 = "Y:";
        tooltip1 = "";
        tooltip2 = "";
    }
}