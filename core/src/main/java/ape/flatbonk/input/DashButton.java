package ape.flatbonk.input;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import ape.flatbonk.util.Constants;

public class DashButton {
    private final float centerX;
    private final float centerY;
    private final float radius;

    private float cooldownTimer;
    private boolean pressed;

    public DashButton(float centerX, float centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = 40f;
        this.cooldownTimer = 0;
        this.pressed = false;
    }

    public boolean touchDown(float x, float y) {
        float dx = x - centerX;
        float dy = y - centerY;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist <= radius * 1.2f && cooldownTimer <= 0) {
            pressed = true;
            return true;
        }
        return false;
    }

    public boolean touchUp() {
        if (pressed) {
            pressed = false;
            return true;
        }
        return false;
    }

    public void triggerCooldown() {
        cooldownTimer = Constants.DASH_COOLDOWN;
    }

    public void update(float delta) {
        if (cooldownTimer > 0) {
            cooldownTimer -= delta;
        }
    }

    public void render(ShapeRenderer shapeRenderer, Viewport viewport) {
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        float cooldownProgress = cooldownTimer / Constants.DASH_COOLDOWN;
        boolean onCooldown = cooldownTimer > 0;

        // Background circle
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (onCooldown) {
            shapeRenderer.setColor(0.2f, 0.2f, 0.3f, 0.8f);
        } else if (pressed) {
            shapeRenderer.setColor(0.6f, 0.8f, 1f, 0.9f);
        } else {
            shapeRenderer.setColor(0.3f, 0.5f, 0.7f, 0.8f);
        }
        shapeRenderer.circle(centerX, centerY, radius, 32);
        shapeRenderer.end();

        // Cooldown arc
        if (onCooldown) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0.1f, 0.3f, 0.5f, 0.9f);
            float arcDegrees = 360f * (1f - cooldownProgress);
            drawArc(shapeRenderer, centerX, centerY, radius - 5, 90, arcDegrees);
            shapeRenderer.end();
        }

        // Border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        if (onCooldown) {
            shapeRenderer.setColor(0.4f, 0.4f, 0.5f, 1f);
        } else {
            shapeRenderer.setColor(Color.CYAN);
        }
        drawCircleOutline(shapeRenderer, centerX, centerY, radius, 32);
        shapeRenderer.end();

        // Dash icon (arrow)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (onCooldown) {
            shapeRenderer.setColor(0.4f, 0.4f, 0.5f, 1f);
        } else {
            shapeRenderer.setColor(Color.WHITE);
        }
        float arrowSize = radius * 0.5f;
        shapeRenderer.triangle(
            centerX, centerY + arrowSize,
            centerX - arrowSize * 0.6f, centerY - arrowSize * 0.3f,
            centerX + arrowSize * 0.6f, centerY - arrowSize * 0.3f
        );
        shapeRenderer.end();
    }

    private void drawArc(ShapeRenderer renderer, float x, float y, float radius, float startAngle, float degrees) {
        int segments = (int) (degrees / 5) + 1;
        float angleStep = degrees / segments;

        for (int i = 0; i < segments; i++) {
            float angle1 = startAngle + i * angleStep;
            float angle2 = startAngle + (i + 1) * angleStep;
            renderer.triangle(
                x, y,
                x + radius * MathUtils.cosDeg(angle1), y + radius * MathUtils.sinDeg(angle1),
                x + radius * MathUtils.cosDeg(angle2), y + radius * MathUtils.sinDeg(angle2)
            );
        }
    }

    private void drawCircleOutline(ShapeRenderer renderer, float x, float y, float radius, int segments) {
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

    public boolean isOnCooldown() {
        return cooldownTimer > 0;
    }

    public boolean isPressed() {
        return pressed;
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public float getRadius() {
        return radius;
    }
}
