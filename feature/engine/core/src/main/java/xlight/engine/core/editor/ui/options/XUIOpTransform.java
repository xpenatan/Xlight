package xlight.engine.core.editor.ui.options;

public class XUIOpTransform {

    private static final XUIOpTransform op = new XUIOpTransform();

    public static XUIOpTransform get() {
        op.reset();
        return op;
    }

    public boolean drawPosition;
    public boolean drawRotation;
    public boolean drawQuaternion;
    public boolean drawScale;
    public boolean drawSize;
    public boolean drawOffset;

    public String posLine;
    public String posLabel1;
    public String posLabel2;
    public String posLabel3;
    public String posTooltip1;
    public String posTooltip2;
    public String posTooltip3;

    public String rotLine;
    public String rotLabel1;
    public String rotLabel2;
    public String rotLabel3;
    public String rotTooltip1;
    public String rotTooltip2;
    public String rotTooltip3;

    public String quatLine;
    public String quatLabel1;
    public String quatLabel2;
    public String quatLabel3;
    public String quatLabel4;
    public String quatTooltip1;
    public String quatTooltip2;
    public String quatTooltip3;
    public String quatTooltip4;


    public String sclLine;
    public String sclLabel1;
    public String sclLabel2;
    public String sclLabel3;
    public String sclTooltip1;
    public String sclTooltip2;
    public String sclTooltip3;

    public String sizeLine;
    public String sizeLabel1;
    public String sizeLabel2;
    public String sizeLabel3;
    public String sizeTooltip1;
    public String sizeTooltip2;
    public String sizeTooltip3;

    public String offsetLine;
    public String offsetLabel1;
    public String offsetLabel2;
    public String offsetLabel3;
    public String offsetTooltip1;
    public String offsetTooltip2;
    public String offsetTooltip3;

    public void reset() {
        drawPosition = true;
        drawRotation = true;
        drawQuaternion = true;
        drawScale = true;
        drawSize = true;
        drawOffset = true;

        posLine = "Position";
        posLabel1 = "X:";
        posLabel2 = "Y:";
        posLabel3 = "Z:";
        posTooltip1 = "Position X";
        posTooltip2 = "Position Y";
        posTooltip3 = "Position Z";

        rotLine = "Rotation";
        rotLabel1 = "X:";
        rotLabel2 = "Y:";
        rotLabel3 = "Z:";
        rotTooltip1 = "Rotation X";
        rotTooltip2 = "Rotation Y";
        rotTooltip3 = "Rotation Z";

        quatLine = "Quaternion";
        quatLabel1 = "X:";
        quatLabel2 = "Y:";
        quatLabel3 = "Z:";
        quatLabel4 = "W:";
        quatTooltip1 = "Quaternion X";
        quatTooltip2 = "Quaternion Y";
        quatTooltip3 = "Quaternion Z";
        quatTooltip4 = "Quaternion W";

        sclLine = "Scale";
        sclLabel1 = "X:";
        sclLabel2 = "Y:";
        sclLabel3 = "Z:";
        sclTooltip1 = "Scale X";
        sclTooltip2 = "Scale Y";
        sclTooltip3 = "Scale Z";

        sizeLine = "Size";
        sizeLabel1 = "X:";
        sizeLabel2 = "Y:";
        sizeLabel3 = "Z:";
        sizeTooltip1 = "Size X";
        sizeTooltip2 = "Size Y";
        sizeTooltip3 = "Size Z";

        offsetLine = "Offset";
        offsetLabel1 = "X:";
        offsetLabel2 = "Y:";
        offsetLabel3 = "Z:";
        offsetTooltip1 = "Offset X";
        offsetTooltip2 = "Offset Y";
        offsetTooltip3 = "Offset Z";
    }
}