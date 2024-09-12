package xlight.engine.core.editor.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Array;
import xlight.engine.core.editor.ui.options.XUIButtonOp;
import xlight.engine.core.editor.ui.options.XUICheckboxOp;
import xlight.engine.core.editor.ui.options.XUIEditText2Op;
import xlight.engine.core.editor.ui.options.XUIEditText3Op;
import xlight.engine.core.editor.ui.options.XUIEditText4Op;
import xlight.engine.core.editor.ui.options.XUIEditTextOp;
import xlight.engine.core.editor.ui.options.XUISelectListOp;
import xlight.engine.core.editor.ui.options.XUITransformOp;
import xlight.engine.datamap.XDataMap;
import xlight.engine.transform.XTransform;

public interface XUIData {

    boolean isUIUpdated();

    /**
     * Begin group add a header before adding the UI elements. Must call endGroup to close it.
     * Grouping does not stack.
     */
    boolean beginHeaderGroup(String groupName);

    void endHeaderGroup();

    /**
     * Custom data, must register it and the data to obtain must be known.
     */
    XDataMap custom(Object value);

    /**
     * Custom data, must register it and the data to obtain must be known.
     */
    XDataMap custom(Object value, Object options);

    // ##################################### Multi Float EditText

    /**
     * When UI is updated, it will return Vector3 value at key 0
     */
    XDataMap editText4(String line, Vector4 value);

    /**
     * When UI is updated, it will return Vector4 value at key 0
     */
    XDataMap editText4(String line, Vector4 value, XUIEditText4Op op);

    /**
     * When UI is updated, it will return Vector4 value at key 0
     */
    XDataMap editText4(String line, float x, float y, float z, float w);

    XDataMap editText4(String line, float x, float y, float z, float w, XUIEditText4Op op);

    /**
     * When UI is updated, it will return Vector3 value at key 0
     */
    XDataMap editText3(String line, Vector3 value);

    /**
     * When UI is updated, it will return Vector3 value at key 0
     */
    XDataMap editText3(String line, Vector3 value, XUIEditText3Op op);

    /**
     * When UI is updated, it will return Vector3 value at key 0
     */
    XDataMap editText3(String line, float x, float y, float z);

    /**
     * When UI is updated, it will return Vector3 value at key 0
     */
    XDataMap editText3(String line, float x, float y, float z, XUIEditText3Op op);

    /**
     * When UI is updated, it will return Vector2 value at key 0
     */
    XDataMap editText2(String line, Vector2 value);

    /**
     * When UI is updated, it will return Vector2 value at key 0
     */
    XDataMap editText2(String line, Vector2 value, XUIEditText2Op op);

    /**
     * When UI is updated, it will return Vector2 value at key 0
     */
    XDataMap editText2(String line, float x, float y);

    /**
     * When UI is updated, it will return Vector2 value at key 0
     */
    XDataMap editText2(String line, float x, float y, XUIEditText2Op op);

    // ##################################### Multi Int EditText

    /**
     * When UI is updated, it will return Vector4 value at key 0
     */
    XDataMap editText4(String line, int x, int y, int z, int w);

    /**
     * When UI is updated, it will return Vector4 value at key 0
     */
    XDataMap editText4(String line, int x, int y, int z, int w, XUIEditText4Op op);

    /**
     * When UI is updated, it will return Vector3 value at key 0
     */
    XDataMap editText3(String line, int x, int y, int z);

    /**
     * When UI is updated, it will return Vector3 value at key 0
     */
    XDataMap editText3(String line, int x, int y, int z, XUIEditText3Op op);

    /**
     * When UI is updated, it will return Vector2 value at key 0
     */
    XDataMap editText2(String line, int x, int y);

    /**
     * When UI is updated, it will return Vector2 value at key 0
     */
    XDataMap editText2(String line, int x, int y, XUIEditText2Op op);

    /**
     * When UI is updated, it will return int value at key 0
     */
    XDataMap editText(String line, int value);

    /**
     * When UI is updated, it will return int value at key 0
     */
    XDataMap editText(String line, int value, XUIEditTextOp op);

    XDataMap editText(String line, float value);

    XDataMap editText(String line, float value, XUIEditTextOp op);

    XDataMap editText(String line, String value);

    XDataMap editText(String line, String value, XUIEditTextOp op);

    /**
     * When UI is updated, it will return Vector3 position at key 0, rotation at key 1 and scale at key 2
     */
    XDataMap transform(XTransform value);

    /**
     * When UI is updated, it will return Vector3 position at key 0, rotation at key 1 and scale at key 2
     */
    XDataMap transform(XTransform value, XUITransformOp op);

    /**
     * When UI is updated, it will return boolean value at key 0
     */
    XDataMap checkbox(String line, boolean value);

    /**
     * When UI is updated, it will return boolean value at key 0
     */
    XDataMap checkbox(String line, boolean value, XUICheckboxOp op);

    /**
     * Add a select list. When UI is updated, it will return int selected value at key 0
     */
    XDataMap selectList(String line, Array<String> items, int selectedIndex);

    /**
     * Add a select list. When UI is updated, it will return int selected value at key 0
     */
    XDataMap selectList(String line, Array<String> items, int selectedIndex, XUISelectListOp op);

    XDataMap button(String line, String buttonName);

    XDataMap button(String line, String buttonName, XUIButtonOp op);

    void beginLine(String name);
    void endLine();

    void beginUIGroup(int totalItems);
    void endUIGroup();

    void textureWindow(String name, Texture texture);
}