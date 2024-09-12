package xlight.engine.camera;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Vector3;
import xpeengine.engine.core.util.XMultiShapeRenderer;
import xpeengine.engine.core.util.XShapeRenderer;

public class XCameraUtils {

    static Color NEAR = new Color(0.4f, 0.0f, 1, 1);
    static Color FAR = new Color(1, 1, 0.1f, 1);
    static Color PLANE = new Color(0, 1, 0, 1);

    public static void renderFrustum(XMultiShapeRenderer renderer, Frustum frustum) {
        renderFrustum(renderer.getLineRenderer(), frustum, NEAR, FAR, PLANE, true);
        renderFrustum(renderer.getFilledRenderer(), frustum, NEAR, FAR, PLANE, false);
    }

    public static void renderFrustum(XShapeRenderer renderer, Frustum frustum, boolean glLine) {
        renderFrustum(renderer, frustum, NEAR, FAR, PLANE, glLine);
    }

    public static void renderFrustum(XShapeRenderer renderer, Frustum frustum, Color near, Color far, Color plane, boolean glLine) {
        Vector3[] planePoints = frustum.planePoints;

        float alpha = 1;

        if(!glLine) {
            alpha = 0.1f;
        }

        float nearR = near.r;
        float nearG = near.g;
        float nearB = near.b;

        float farR = far.r;
        float farG = far.g;
        float farB = far.b;

        float lrtbR = plane.r;
        float lrtbG = plane.g;
        float lrtbB = plane.b;

        // far plane
        renderer.setColor(farR, farG, farB, alpha);

        if(glLine) {
            renderer.line(planePoints[4].x, planePoints[4].y, planePoints[4].z, planePoints[5].x, planePoints[5].y, planePoints[5].z);
            renderer.line(planePoints[5].x, planePoints[5].y, planePoints[5].z, planePoints[6].x, planePoints[6].y, planePoints[6].z);
            renderer.line(planePoints[6].x, planePoints[6].y, planePoints[6].z, planePoints[7].x, planePoints[7].y, planePoints[7].z);
            renderer.line(planePoints[7].x, planePoints[7].y, planePoints[7].z, planePoints[4].x, planePoints[4].y, planePoints[4].z);
        }
        else {
//            renderer.triangle(ShapeRenderer.ShapeType.Filled,
//                    planePoints[4].x, planePoints[4].y, planePoints[4].z,
//                    planePoints[5].x, planePoints[5].y, planePoints[5].z,
//                    planePoints[6].x, planePoints[6].y, planePoints[6].z
//            );
//            renderer.triangle(ShapeRenderer.ShapeType.Filled,
//                    planePoints[6].x, planePoints[6].y, planePoints[6].z,
//                    planePoints[7].x, planePoints[7].y, planePoints[7].z,
//                    planePoints[4].x, planePoints[4].y, planePoints[4].z
//            );
        }

        // near plane
        renderer.setColor(nearR, nearG, nearB, alpha);
        if(glLine) {
            renderer.line(planePoints[0].x, planePoints[0].y, planePoints[0].z, planePoints[1].x, planePoints[1].y, planePoints[1].z);
            renderer.line(planePoints[1].x, planePoints[1].y, planePoints[1].z, planePoints[2].x, planePoints[2].y, planePoints[2].z);
            renderer.line(planePoints[2].x, planePoints[2].y, planePoints[2].z, planePoints[3].x, planePoints[3].y, planePoints[3].z);
            renderer.line(planePoints[3].x, planePoints[3].y, planePoints[3].z, planePoints[0].x, planePoints[0].y, planePoints[0].z);
        }
        else {
//            renderer.triangle(ShapeRenderer.ShapeType.Filled,
//                    planePoints[0].x, planePoints[0].y, planePoints[0].z,
//                    planePoints[1].x, planePoints[1].y, planePoints[1].z,
//                    planePoints[2].x, planePoints[2].y, planePoints[2].z
//            );
//            renderer.triangle(ShapeRenderer.ShapeType.Filled,
//                    planePoints[2].x, planePoints[2].y, planePoints[2].z,
//                    planePoints[3].x, planePoints[3].y, planePoints[3].z,
//                    planePoints[0].x, planePoints[0].y, planePoints[0].z
//            );
        }

        // left, right, top bottom (sort of :p)
        renderer.setColor(lrtbR, lrtbG, lrtbB, alpha);
        if(glLine) {
            // Top Left
            renderer.line(planePoints[3].x, planePoints[3].y, planePoints[3].z, planePoints[7].x, planePoints[7].y, planePoints[7].z);
            //Top right
            renderer.line(planePoints[2].x, planePoints[2].y, planePoints[2].z, planePoints[6].x, planePoints[6].y, planePoints[6].z);
            // Bottom left
            renderer.line(planePoints[0].x, planePoints[0].y, planePoints[0].z, planePoints[4].x, planePoints[4].y, planePoints[4].z);
            // Bottom right
            renderer.line(planePoints[1].x, planePoints[1].y, planePoints[1].z, planePoints[5].x, planePoints[5].y, planePoints[5].z);
        }
        else {
            // Left
            renderer.triangle(ShapeRenderer.ShapeType.Filled,
                    planePoints[3].x, planePoints[3].y, planePoints[3].z,
                    planePoints[7].x, planePoints[7].y, planePoints[7].z,
                    planePoints[0].x, planePoints[0].y, planePoints[0].z
            );
            renderer.triangle(ShapeRenderer.ShapeType.Filled,
                    planePoints[0].x, planePoints[0].y, planePoints[0].z,
                    planePoints[4].x, planePoints[4].y, planePoints[4].z,
                    planePoints[7].x, planePoints[7].y, planePoints[7].z
            );

            // Right
            renderer.triangle(ShapeRenderer.ShapeType.Filled,
                    planePoints[2].x, planePoints[2].y, planePoints[2].z,
                    planePoints[6].x, planePoints[6].y, planePoints[6].z,
                    planePoints[5].x, planePoints[5].y, planePoints[5].z
            );
            renderer.triangle(ShapeRenderer.ShapeType.Filled,
                    planePoints[5].x, planePoints[5].y, planePoints[5].z,
                    planePoints[2].x, planePoints[2].y, planePoints[2].z,
                    planePoints[1].x, planePoints[1].y, planePoints[1].z
            );

            // Top
            renderer.triangle(ShapeRenderer.ShapeType.Filled,
                    planePoints[3].x, planePoints[3].y, planePoints[3].z,
                    planePoints[7].x, planePoints[7].y, planePoints[7].z,
                    planePoints[6].x, planePoints[6].y, planePoints[6].z
            );
            renderer.triangle(ShapeRenderer.ShapeType.Filled,
                    planePoints[3].x, planePoints[3].y, planePoints[3].z,
                    planePoints[2].x, planePoints[2].y, planePoints[2].z,
                    planePoints[6].x, planePoints[6].y, planePoints[6].z
            );

            // Bottom
            renderer.triangle(ShapeRenderer.ShapeType.Filled,
                    planePoints[0].x, planePoints[0].y, planePoints[0].z,
                    planePoints[1].x, planePoints[1].y, planePoints[1].z,
                    planePoints[4].x, planePoints[4].y, planePoints[4].z
            );
            renderer.triangle(ShapeRenderer.ShapeType.Filled,
                    planePoints[1].x, planePoints[1].y, planePoints[1].z,
                    planePoints[5].x, planePoints[5].y, planePoints[5].z,
                    planePoints[4].x, planePoints[4].y, planePoints[4].z
            );
        }
    }
}