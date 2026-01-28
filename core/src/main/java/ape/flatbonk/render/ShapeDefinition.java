package ape.flatbonk.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import ape.flatbonk.util.ShapeType;

public class ShapeDefinition {

    public static void drawShape(ShapeRenderer renderer, ShapeType type, float x, float y, float size, Color color) {
        drawShape(renderer, type, x, y, size, color, 0);
    }

    public static void drawShape(ShapeRenderer renderer, ShapeType type, float x, float y, float size, Color color, float rotation) {
        renderer.setColor(color);
        float halfSize = size / 2;

        switch (type) {
            case CIRCLE:
                renderer.circle(x, y, halfSize, 32);
                break;
            case TRIANGLE:
                drawPolygon(renderer, x, y, halfSize, 3, rotation - 90);
                break;
            case SQUARE:
                drawPolygon(renderer, x, y, halfSize * 1.2f, 4, rotation + 45);
                break;
            case PENTAGON:
                drawPolygon(renderer, x, y, halfSize, 5, rotation - 90);
                break;
            case HEXAGON:
                drawPolygon(renderer, x, y, halfSize, 6, rotation);
                break;
            case STAR:
                drawStar(renderer, x, y, halfSize, 5, rotation);
                break;
            case DIAMOND:
                drawDiamond(renderer, x, y, halfSize, rotation);
                break;
            case OVAL:
                drawRotatedEllipse(renderer, x, y, halfSize, halfSize * 0.6f, rotation);
                break;
            case RECTANGLE:
                drawRotatedRect(renderer, x, y, halfSize, halfSize * 0.5f, rotation);
                break;
            case CROSS:
                drawCross(renderer, x, y, halfSize, rotation);
                break;
            case ARROW:
                drawArrow(renderer, x, y, halfSize, rotation);
                break;
            case HEART:
                drawHeart(renderer, x, y, halfSize, rotation);
                break;
            case CRESCENT:
                drawCrescent(renderer, x, y, halfSize, color, rotation);
                break;
            case SEMICIRCLE:
                drawSemicircle(renderer, x, y, halfSize, rotation);
                break;
            case OCTAGON:
                drawPolygon(renderer, x, y, halfSize, 8, rotation + 22.5f);
                break;
        }
    }

    private static void drawPolygon(ShapeRenderer renderer, float x, float y, float radius, int sides, float startAngle) {
        float[] vertices = new float[sides * 2];
        for (int i = 0; i < sides; i++) {
            float angle = startAngle + (360f / sides) * i;
            vertices[i * 2] = x + radius * MathUtils.cosDeg(angle);
            vertices[i * 2 + 1] = y + radius * MathUtils.sinDeg(angle);
        }

        // Draw as triangles from center
        for (int i = 0; i < sides; i++) {
            int next = (i + 1) % sides;
            renderer.triangle(x, y,
                vertices[i * 2], vertices[i * 2 + 1],
                vertices[next * 2], vertices[next * 2 + 1]);
        }
    }

    private static void drawStar(ShapeRenderer renderer, float x, float y, float radius, int points, float rotation) {
        float innerRadius = radius * 0.4f;
        int totalPoints = points * 2;

        float[] vertices = new float[totalPoints * 2];
        for (int i = 0; i < totalPoints; i++) {
            float angle = rotation - 90 + (360f / totalPoints) * i;
            float r = (i % 2 == 0) ? radius : innerRadius;
            vertices[i * 2] = x + r * MathUtils.cosDeg(angle);
            vertices[i * 2 + 1] = y + r * MathUtils.sinDeg(angle);
        }

        for (int i = 0; i < totalPoints; i++) {
            int next = (i + 1) % totalPoints;
            renderer.triangle(x, y,
                vertices[i * 2], vertices[i * 2 + 1],
                vertices[next * 2], vertices[next * 2 + 1]);
        }
    }

    private static void drawDiamond(ShapeRenderer renderer, float x, float y, float radius, float rotation) {
        float height = radius * 1.3f;
        float[] p1 = rotatePoint(0, height, rotation);
        float[] p2 = rotatePoint(-radius, 0, rotation);
        float[] p3 = rotatePoint(radius, 0, rotation);
        float[] p4 = rotatePoint(0, -height * 0.7f, rotation);
        renderer.triangle(x + p1[0], y + p1[1], x + p2[0], y + p2[1], x + p3[0], y + p3[1]);
        renderer.triangle(x + p4[0], y + p4[1], x + p2[0], y + p2[1], x + p3[0], y + p3[1]);
    }

    private static void drawCross(ShapeRenderer renderer, float x, float y, float radius, float rotation) {
        float thickness = radius * 0.35f;
        // Draw rotated cross using triangles
        float cos = MathUtils.cosDeg(rotation);
        float sin = MathUtils.sinDeg(rotation);

        // Vertical bar
        float[] v1 = rotatePoint(-thickness, -radius, rotation);
        float[] v2 = rotatePoint(thickness, -radius, rotation);
        float[] v3 = rotatePoint(thickness, radius, rotation);
        float[] v4 = rotatePoint(-thickness, radius, rotation);
        renderer.triangle(x + v1[0], y + v1[1], x + v2[0], y + v2[1], x + v3[0], y + v3[1]);
        renderer.triangle(x + v1[0], y + v1[1], x + v3[0], y + v3[1], x + v4[0], y + v4[1]);

        // Horizontal bar
        float[] h1 = rotatePoint(-radius, -thickness, rotation);
        float[] h2 = rotatePoint(radius, -thickness, rotation);
        float[] h3 = rotatePoint(radius, thickness, rotation);
        float[] h4 = rotatePoint(-radius, thickness, rotation);
        renderer.triangle(x + h1[0], y + h1[1], x + h2[0], y + h2[1], x + h3[0], y + h3[1]);
        renderer.triangle(x + h1[0], y + h1[1], x + h3[0], y + h3[1], x + h4[0], y + h4[1]);
    }

    private static void drawArrow(ShapeRenderer renderer, float x, float y, float radius, float rotation) {
        float bodyWidth = radius * 0.4f;
        float bodyLength = radius * 1.2f;
        float headWidth = radius * 0.8f;
        float headLength = radius * 0.6f;

        // Arrow pointing in rotation direction
        float[] b1 = rotatePoint(-bodyWidth / 2, -bodyLength / 2, rotation);
        float[] b2 = rotatePoint(bodyWidth / 2, -bodyLength / 2, rotation);
        float[] b3 = rotatePoint(bodyWidth / 2, bodyLength / 2, rotation);
        float[] b4 = rotatePoint(-bodyWidth / 2, bodyLength / 2, rotation);
        renderer.triangle(x + b1[0], y + b1[1], x + b2[0], y + b2[1], x + b3[0], y + b3[1]);
        renderer.triangle(x + b1[0], y + b1[1], x + b3[0], y + b3[1], x + b4[0], y + b4[1]);

        // Arrow head
        float[] tip = rotatePoint(0, bodyLength / 2 + headLength, rotation);
        float[] left = rotatePoint(-headWidth, bodyLength / 2, rotation);
        float[] right = rotatePoint(headWidth, bodyLength / 2, rotation);
        renderer.triangle(x + tip[0], y + tip[1], x + left[0], y + left[1], x + right[0], y + right[1]);
    }

    private static void drawHeart(ShapeRenderer renderer, float x, float y, float radius, float rotation) {
        float scale = radius * 0.7f;
        // For simplicity, hearts don't rotate as much - just offset
        float[] c1 = rotatePoint(-scale * 0.5f, scale * 0.3f, rotation);
        float[] c2 = rotatePoint(scale * 0.5f, scale * 0.3f, rotation);
        float[] t1 = rotatePoint(-scale, scale * 0.3f, rotation);
        float[] t2 = rotatePoint(scale, scale * 0.3f, rotation);
        float[] t3 = rotatePoint(0, -scale, rotation);

        renderer.circle(x + c1[0], y + c1[1], scale * 0.5f, 16);
        renderer.circle(x + c2[0], y + c2[1], scale * 0.5f, 16);
        renderer.triangle(x + t1[0], y + t1[1], x + t2[0], y + t2[1], x + t3[0], y + t3[1]);
    }

    private static void drawCrescent(ShapeRenderer renderer, float x, float y, float radius, Color color, float rotation) {
        float[] offset = rotatePoint(radius * 0.4f, 0, rotation);
        renderer.circle(x, y, radius, 32);
        Color darker = new Color(color.r * 0.2f, color.g * 0.2f, color.b * 0.2f, 1f);
        renderer.setColor(darker);
        renderer.circle(x + offset[0], y + offset[1], radius * 0.8f, 32);
        renderer.setColor(color);
    }

    private static void drawSemicircle(ShapeRenderer renderer, float x, float y, float radius, float rotation) {
        renderer.arc(x, y, radius, rotation, 180, 32);
    }

    private static void drawRotatedEllipse(ShapeRenderer renderer, float x, float y, float radiusX, float radiusY, float rotation) {
        int segments = 32;
        for (int i = 0; i < segments; i++) {
            float angle1 = (360f / segments) * i;
            float angle2 = (360f / segments) * (i + 1);
            float[] p1 = rotatePoint(radiusX * MathUtils.cosDeg(angle1), radiusY * MathUtils.sinDeg(angle1), rotation);
            float[] p2 = rotatePoint(radiusX * MathUtils.cosDeg(angle2), radiusY * MathUtils.sinDeg(angle2), rotation);
            renderer.triangle(x, y, x + p1[0], y + p1[1], x + p2[0], y + p2[1]);
        }
    }

    private static void drawRotatedRect(ShapeRenderer renderer, float x, float y, float halfWidth, float halfHeight, float rotation) {
        float[] p1 = rotatePoint(-halfWidth, -halfHeight, rotation);
        float[] p2 = rotatePoint(halfWidth, -halfHeight, rotation);
        float[] p3 = rotatePoint(halfWidth, halfHeight, rotation);
        float[] p4 = rotatePoint(-halfWidth, halfHeight, rotation);
        renderer.triangle(x + p1[0], y + p1[1], x + p2[0], y + p2[1], x + p3[0], y + p3[1]);
        renderer.triangle(x + p1[0], y + p1[1], x + p3[0], y + p3[1], x + p4[0], y + p4[1]);
    }

    private static float[] rotatePoint(float px, float py, float rotation) {
        float cos = MathUtils.cosDeg(rotation);
        float sin = MathUtils.sinDeg(rotation);
        return new float[] { px * cos - py * sin, px * sin + py * cos };
    }

    public static void drawShapeOutline(ShapeRenderer renderer, ShapeType type, float x, float y, float size, Color color) {
        renderer.setColor(color);
        float halfSize = size / 2;

        switch (type) {
            case CIRCLE:
                drawCircleOutline(renderer, x, y, halfSize, 32);
                break;
            case SQUARE:
                renderer.rect(x - halfSize, y - halfSize, size, size);
                break;
            case OVAL:
                drawEllipseOutline(renderer, x, y, halfSize, halfSize * 0.6f, 32);
                break;
            case RECTANGLE:
                renderer.rect(x - halfSize, y - halfSize * 0.5f, size, size * 0.5f);
                break;
            default:
                // For other shapes, draw filled at reduced opacity
                drawShape(renderer, type, x, y, size, color);
        }
    }

    private static void drawCircleOutline(ShapeRenderer renderer, float x, float y, float radius, int segments) {
        for (int i = 0; i < segments; i++) {
            float angle1 = (360f / segments) * i;
            float angle2 = (360f / segments) * (i + 1);
            renderer.line(
                x + radius * MathUtils.cosDeg(angle1),
                y + radius * MathUtils.sinDeg(angle1),
                x + radius * MathUtils.cosDeg(angle2),
                y + radius * MathUtils.sinDeg(angle2)
            );
        }
    }

    private static void drawEllipseOutline(ShapeRenderer renderer, float x, float y, float radiusX, float radiusY, int segments) {
        for (int i = 0; i < segments; i++) {
            float angle1 = (360f / segments) * i;
            float angle2 = (360f / segments) * (i + 1);
            renderer.line(
                x + radiusX * MathUtils.cosDeg(angle1),
                y + radiusY * MathUtils.sinDeg(angle1),
                x + radiusX * MathUtils.cosDeg(angle2),
                y + radiusY * MathUtils.sinDeg(angle2)
            );
        }
    }

    /**
     * Draw an irregular polygon with jagged edges for monster rendering
     */
    public static void drawIrregularPolygon(ShapeRenderer renderer, float x, float y, float radius,
            int sides, float[] vertexOffsets, Color color, float rotation) {
        renderer.setColor(color);

        float[] vertices = new float[sides * 2];
        for (int i = 0; i < sides; i++) {
            float angle = rotation + (360f / sides) * i;
            float r = radius;
            if (vertexOffsets != null && i < vertexOffsets.length) {
                r = radius * (0.7f + vertexOffsets[i] * 0.5f);  // Vary radius 70%-120%
            }
            vertices[i * 2] = x + r * MathUtils.cosDeg(angle);
            vertices[i * 2 + 1] = y + r * MathUtils.sinDeg(angle);
        }

        // Draw as triangles from center
        for (int i = 0; i < sides; i++) {
            int next = (i + 1) % sides;
            renderer.triangle(x, y,
                vertices[i * 2], vertices[i * 2 + 1],
                vertices[next * 2], vertices[next * 2 + 1]);
        }
    }

    /**
     * Generate random vertex offsets for irregular polygon
     */
    public static float[] generateVertexOffsets(int sides) {
        float[] offsets = new float[sides];
        for (int i = 0; i < sides; i++) {
            offsets[i] = MathUtils.random(0.4f, 1.0f);
        }
        return offsets;
    }

    /**
     * Generate organ positions for Flatlandia-style internal details
     * Returns array of [x1, y1, size1, x2, y2, size2, ...] relative to center
     */
    public static float[] generateOrganOffsets(int organCount, float maxRadius) {
        float[] organs = new float[organCount * 3];
        for (int i = 0; i < organCount; i++) {
            // Random position inside the polygon (keep organs toward center)
            float angle = MathUtils.random(360f);
            float dist = MathUtils.random(0.1f, 0.5f) * maxRadius;
            organs[i * 3] = MathUtils.cosDeg(angle) * dist;      // x offset
            organs[i * 3 + 1] = MathUtils.sinDeg(angle) * dist;  // y offset
            organs[i * 3 + 2] = MathUtils.random(0.08f, 0.2f);   // size as fraction of monster size
        }
        return organs;
    }

    /**
     * Draw organs inside a monster (Flatlandia style - visible internals)
     */
    public static void drawOrgans(ShapeRenderer renderer, float x, float y, float size,
            float[] organOffsets, int organCount, Color baseColor) {
        if (organOffsets == null || organCount <= 0) return;

        // Darker color for organs
        Color organColor = new Color(baseColor.r * 0.4f, baseColor.g * 0.4f, baseColor.b * 0.4f, 0.8f);

        for (int i = 0; i < organCount && i * 3 + 2 < organOffsets.length; i++) {
            float ox = x + organOffsets[i * 3] * size;
            float oy = y + organOffsets[i * 3 + 1] * size;
            float osize = organOffsets[i * 3 + 2] * size;

            renderer.setColor(organColor);
            renderer.circle(ox, oy, osize);
        }
    }

    /**
     * Draw outline for Flatlandia style
     */
    public static void drawIrregularPolygonOutline(ShapeRenderer renderer, float x, float y, float radius,
            int sides, float[] vertexOffsets, Color color, float rotation) {
        renderer.setColor(color);

        for (int i = 0; i < sides; i++) {
            float angle1 = rotation + (360f / sides) * i;
            float angle2 = rotation + (360f / sides) * ((i + 1) % sides);

            float r1 = radius;
            float r2 = radius;
            if (vertexOffsets != null) {
                if (i < vertexOffsets.length) r1 = radius * (0.7f + vertexOffsets[i] * 0.5f);
                if ((i + 1) % sides < vertexOffsets.length) r2 = radius * (0.7f + vertexOffsets[(i + 1) % sides] * 0.5f);
            }

            float x1 = x + r1 * MathUtils.cosDeg(angle1);
            float y1 = y + r1 * MathUtils.sinDeg(angle1);
            float x2 = x + r2 * MathUtils.cosDeg(angle2);
            float y2 = y + r2 * MathUtils.sinDeg(angle2);

            renderer.line(x1, y1, x2, y2);
        }
    }
}
