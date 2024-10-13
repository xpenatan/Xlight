package xlight.engine.core.editor.ui;

import com.badlogic.gdx.utils.Array;
import xlight.engine.core.editor.ui.options.XUIOpButton;
import xlight.engine.core.editor.ui.options.XUIOpEditText;
import xlight.engine.core.editor.ui.options.XUIOpEditText2;
import xlight.engine.core.editor.ui.options.XUIOpEditText3;
import xlight.engine.core.editor.ui.options.XUIOpStringEditText;
import xlight.engine.core.editor.ui.options.XUIOpTransform;
import xlight.engine.core.editor.ui.options.XUIOpCheckbox;
import xlight.engine.core.editor.ui.options.XUIOpSelectList;
import xlight.engine.transform.XTransform;

public interface XUIData {
    boolean beginTable();

    /**
     * Call when beginTable returns true
     */
    void endTable();
    void beginLine(String name);
    void beginLine(String name, int lineColor);
    void endLine();

    boolean collapsingHeader(String name);

    void text(String line, String text);
    boolean button(String line, String buttonName);
    boolean button(String line, String buttonName, XUIOpButton op);

    boolean editText(String line, float value, XUIOpEditText op);
    boolean editText2(String line, float x, float y, XUIOpEditText2 op);
    boolean editText3(String line, float x, float y, float z, XUIOpEditText3 op);

    boolean editText(String line, int value, XUIOpEditText op);
    boolean editText2(String line, int x, int y, XUIOpEditText2 op);
    boolean editText3(String line, int x, int y, int z, XUIOpEditText3 op);

    boolean editText(String line, String value, XUIOpStringEditText op);

    boolean selectList(String line, Array<String> items, int selectedIndex, XUIOpSelectList op);

    boolean checkbox(String line, boolean value, XUIOpCheckbox op);

    /**
     * Return true when one of transform values is updated.
     */
    boolean transform(XTransform transform, XUIOpTransform op);
}