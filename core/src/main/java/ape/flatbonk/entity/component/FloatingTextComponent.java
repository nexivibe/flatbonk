package ape.flatbonk.entity.component;

import com.badlogic.gdx.graphics.Color;

public class FloatingTextComponent {
    private String text;
    private Color color;
    private float lifetime;
    private float maxLifetime;
    private float floatSpeed;
    private float scale;

    public FloatingTextComponent(String text, Color color, float lifetime) {
        this.text = text;
        this.color = color.cpy();
        this.lifetime = lifetime;
        this.maxLifetime = lifetime;
        this.floatSpeed = 30f;  // pixels per second upward
        this.scale = 1f;
    }

    public void update(float delta) {
        lifetime -= delta;
    }

    public boolean isExpired() {
        return lifetime <= 0;
    }

    public float getAlpha() {
        // Fade out over last 50% of lifetime
        if (lifetime < maxLifetime * 0.5f) {
            return lifetime / (maxLifetime * 0.5f);
        }
        return 1f;
    }

    public String getText() {
        return text;
    }

    public Color getColor() {
        return color;
    }

    public float getFloatSpeed() {
        return floatSpeed;
    }

    public void setFloatSpeed(float floatSpeed) {
        this.floatSpeed = floatSpeed;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
