package ape.flatbonk.entity.component;

public class TransformComponent {
    private float x;
    private float y;
    private float rotation;
    private float scale;

    public TransformComponent() {
        this(0, 0);
    }

    public TransformComponent(float x, float y) {
        this.x = x;
        this.y = y;
        this.rotation = 0;
        this.scale = 1f;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void translate(float dx, float dy) {
        this.x += dx;
        this.y += dy;
    }

    public float distanceTo(TransformComponent other) {
        float dx = other.x - this.x;
        float dy = other.y - this.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
