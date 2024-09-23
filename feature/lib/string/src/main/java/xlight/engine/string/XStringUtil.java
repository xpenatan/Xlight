package xlight.engine.string;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;

public class XStringUtil {
    private XStringUtil() {
    }

    public static boolean isEmptyOrNull(String text) {
        return text == null || text.trim().isEmpty();
    }

    public static XTextBuilder append(XTextBuilder textBuilder, boolean b) {
        textBuilder.append(b);
        return textBuilder;
    }

    public static XTextBuilder append(XTextBuilder textBuilder, double d) {
        if(XStringUtil.USE_SIMPLE_STRINGBUILDER || Gdx.app.getType() == ApplicationType.WebGL)
            textBuilder.append(d);
        else {
            int digitLength = XMathLib.digitLength((int)d);
            if(d < 1f && d > -1f)
                digitLength -= 1;
            textBuilder.append(d, digitLength + 4, false, false);
        }
        return textBuilder;
    }

    public static XTextBuilder append(XTextBuilder textBuilder, double d, int decimals) {
        int digitLength = XMathLib.digitLength((int)d);

        if(d < 1f && d > -1f)
            digitLength -= 1;

        textBuilder.append(d, digitLength + decimals, false, false);
        return textBuilder;
    }

    public static XTextBuilder append(XTextBuilder textBuilder, int i) {
        if(XStringUtil.USE_SIMPLE_STRINGBUILDER || Gdx.app.getType() == ApplicationType.WebGL)
            textBuilder.append(i);
        else {
            textBuilder.append(i);
        }
        return textBuilder;
    }

    public static XTextBuilder append(XTextBuilder textBuilder, short s) {
        if(XStringUtil.USE_SIMPLE_STRINGBUILDER || Gdx.app.getType() == ApplicationType.WebGL)
            textBuilder.append(s);
        else {
            textBuilder.append(s);
        }
        return textBuilder;
    }

    public static XTextBuilder append(XTextBuilder textBuilder, long l) {
        if(XStringUtil.USE_SIMPLE_STRINGBUILDER || Gdx.app.getType() == ApplicationType.WebGL)
            textBuilder.append(l);
        else {
            textBuilder.append(l);
        }
        return textBuilder;
    }

    public static XTextBuilder append(XTextBuilder textBuilder, float f) {
        if(XStringUtil.USE_SIMPLE_STRINGBUILDER || Gdx.app.getType() == ApplicationType.WebGL)
            textBuilder.append(f);
        else {
            int digitLength = XMathLib.digitLength((int)f);
            if(f < 1f && f > -1f)
                digitLength -= 1;
            textBuilder.append(f, digitLength + 4, false, false);
        }
        return textBuilder;
    }

    public static XTextBuilder append(XTextBuilder textBuilder, float f, int decimals) {
        int digitLength = XMathLib.digitLength((int)f);

        if(f < 1f && f > -1f)
            digitLength -= 1;

        textBuilder.append(f, digitLength + decimals, false, false);
        return textBuilder;
    }

    public static boolean USE_SIMPLE_STRINGBUILDER = true;

    private static XTextBuilder stringBuilder = null;

    public static XTextBuilder createImpl() {
        if(XStringUtil.USE_SIMPLE_STRINGBUILDER || Gdx.app.getType() == ApplicationType.WebGL) {
            return new XStringBuilder();
        }
        else {
            return new XTextBuilderDesktop();
        }
    }

    public static XTextBuilder get() {
        if(stringBuilder == null)
            stringBuilder = createImpl();
        return stringBuilder;
    }
}