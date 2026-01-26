package ape.flatbonk.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import ape.flatbonk.util.ShapeType;

public class ShapeDefinition {

    public static void drawShape(ShapeRenderer renderer, ShapeType type, float x, float y, float size, Color color) {
        renderer.setColor(color);
        float halfSize = size / 2;

        switch (type) {
            case CIRCLE:
                renderer.circle(x, y, halfSize, 32);
                break;
            case TRIANGLE:
                drawPolygon(renderer, x, y, halfSize, 3, -90);
                break;
            case SQUARE:
                renderer.rect(x - halfSize, y - halfSize, size, size);
                break;
            case PENTAGON:
                drawPolygon(renderer, x, y, halfSize, 5, -90);
                break;
            case HEXAGON:
                drawPolygon(renderer, x, y, halfSize, 6, 0);
                break;
            case STAR:
                drawStar(renderer, x, y, halfSize, 5);
                break;
            case DIAMOND:
                drawDiamond(renderer, x, y, halfSize);
                break;
            case OVAL:
                renderer.ellipse(x - halfSize, y - halfSize * 0.6f, size, size * 0.6f, 32);
                break;
            case RECTANGLE:
                renderer.rect(x - halfSize, y - halfSize * 0.5f, size, size * 0.5f);
                break;
            case CROSS:
                drawCross(renderer, x, y, halfSize);
                break;
            case ARROW:
                drawArrow(renderer, x, y, halfSize);
                break;
            case HEART:
                drawHeart(renderer, x, y, halfSize);
                break;
            case CRESCENT:
                drawCrescent(renderer, x, y, halfSize, color);
                break;
            case SEMICIRCLE:
                drawSemicircle(renderer, x, y, halfSize);
                break;
            case OCTAGON:
                drawPolygon(renderer, x, y, halfSize, 8, 22.5f);
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

    private static void drawStar(ShapeRenderer renderer, float x, float y, float radius, int points) {
        float innerRadius = radius * 0.4f;
        int totalPoints = points * 2;

        float[] vertices = new float[totalPoints * 2];
        for (int i = 0; i < totalPoints; i++) {
            float angle = -90 + (360f / totalPoints) * i;
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

    private static void drawDiamond(ShapeRenderer renderer, float x, float y, float radius) {
        float height = radius * 1.3f;
        renderer.triangle(x, y + height, x - radius, y, x + radius, y);
        renderer.triangle(x, y - height * 0.7f, x - radius, y, x + radius, y);
    }

    private static void drawCross(ShapeRenderer renderer, float x, float y, float radius) {
        float thickness = radius * 0.35f;
        renderer.rect(x - thickness, y - radius, thickness * 2, radius * 2);
        renderer.rect(x - radius, y - thickness, radius * 2, thickness * 2);
    }

    private static void drawArrow(ShapeRenderer renderer, float x, float y, float radius) {
        float bodyWidth = radius * 0.4f;
        float bodyLength = radius * 1.2f;
        float headWidth = radius * 0.8f;
        float headLength = radius * 0.6f;

        // Arrow body
        renderer.rect(x - bodyWidth / 2, y - bodyLength / 2, bodyWidth, bodyLength);
        // Arrow head
        renderer.triangle(
            x, y + bodyLength / 2 + headLength,
            x - headWidth, y + bodyLength / 2,
            x + headWidth, y + bodyLength / 2
        );
    }

    private static void drawHeart(ShapeRenderer renderer, float x, float y, float radius) {
        float scale = radius * 0.7f;
        // Draw heart using two circles and a triangle
        renderer.circle(x - scale * 0.5f, y + scale * 0.3f, scale * 0.5f, 16);
        renderer.circle(x + scale * 0.5f, y + scale * 0.3f, scale * 0.5f, 16);
        renderer.triangle(
            x - scale, y + scale * 0.3f,
            x + scale, y + scale * 0.3f,
            x, y - scale
        );
    }

    private static void drawCrescent(ShapeRenderer renderer, float x, float y, float radius, Color color) {
        // Draw crescent by drawing a circle then overlaying with background
        renderer.circle(x, y, radius, 32);
        // We simulate the crescent by drawing with darker offset circle
        Color darker = new Color(color.r * 0.2f, color.g * 0.2f, color.b * 0.2f, 1f);
        renderer.setColor(darker);
        renderer.circle(x + radius * 0.4f, y, radius * 0.8f, 32);
        renderer.setColor(color);
    }

    private static void drawSemicircle(ShapeRenderer renderer, float x, float y, float radius) {
        renderer.arc(x, y, radius, 0, 180, 32);
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
}
