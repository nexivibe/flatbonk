package ape.flatbonk.input;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import ape.flatbonk.util.Constants;

public class VirtualJoystick {
    private final float centerX;
    private final float centerY;
    private final float outerRadius;
    private final float innerRadius;
    private final float deadzone;

    private float knobX;
    private float knobY;
    private boolean active;
    private int touchPointer;

    private final Vector2 direction;

    public VirtualJoystick(float centerX, float centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.outerRadius = Constants.JOYSTICK_RADIUS;
        this.innerRadius = outerRadius * 0.4f;
        this.deadzone = Constants.JOYSTICK_DEADZONE;

        this.knobX = centerX;
        this.knobY = centerY;
        this.active = false;
        this.touchPointer = -1;

        this.direction = new Vector2();
    }

    public boolean touchDown(float x, float y, int pointer) {
        float dx = x - centerX;
        float dy = y - centerY;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist <= outerRadius * 1.5f) {
            active = true;
            touchPointer = pointer;
            updateKnob(x, y);
            return true;
        }
        return false;
    }

    public boolean touchDragged(float x, float y, int pointer) {
        if (active && pointer == touchPointer) {
            updateKnob(x, y);
            return true;
        }
        return false;
    }

    public boolean touchUp(int pointer) {
        if (active && pointer == touchPointer) {
            active = false;
            touchPointer = -1;
            knobX = centerX;
            knobY = centerY;
            direction.set(0, 0);
            return true;
        }
        return false;
    }

    private void updateKnob(float x, float y) {
        float dx = x - centerX;
        float dy = y - centerY;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist > outerRadius) {
            dx = dx / dist * outerRadius;
            dy = dy / dist * outerRadius;
            dist = outerRadius;
        }

        knobX = centerX + dx;
        knobY = centerY + dy;

        // Calculate normalized direction with deadzone
        float normalizedDist = dist / outerRadius;
        if (normalizedDist < deadzone) {
            direction.set(0, 0);
        } else {
            float adjustedDist = (normalizedDist - deadzone) / (1 - deadzone);
            direction.set(dx / dist * adjustedDist, dy / dist * adjustedDist);
        }
    }

    public void render(ShapeRenderer shapeRenderer, Viewport viewport) {
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        // Outer ring
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.3f, 0.4f, 0.5f, 1f);
        for (int i = 0; i < 32; i++) {
            float angle1 = (float) (i * 2 * Math.PI / 32);
            float angle2 = (float) ((i + 1) * 2 * Math.PI / 32);
            shapeRenderer.line(
                centerX + outerRadius * (float) Math.cos(angle1),
                centerY + outerRadius * (float) Math.sin(angle1),
                centerX + outerRadius * (float) Math.cos(angle2),
                centerY + outerRadius * (float) Math.sin(angle2)
            );
        }
        shapeRenderer.end();

        // Inner knob
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (active) {
            shapeRenderer.setColor(0.4f, 0.6f, 0.8f, 0.9f);
        } else {
            shapeRenderer.setColor(0.3f, 0.4f, 0.5f, 0.7f);
        }
        shapeRenderer.circle(knobX, knobY, innerRadius, 24);
        shapeRenderer.end();
    }

    public Vector2 getDirection() {
        return direction;
    }

    public boolean isActive() {
        return active;
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public float getOuterRadius() {
        return outerRadius;
    }
}
