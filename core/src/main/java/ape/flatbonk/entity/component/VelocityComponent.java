package ape.flatbonk.entity.component;

public class VelocityComponent {
    private float vx;
    private float vy;
    private float maxSpeed;

    public VelocityComponent() {
        this(0, 0, Float.MAX_VALUE);
    }

    public VelocityComponent(float maxSpeed) {
        this(0, 0, maxSpeed);
    }

    public VelocityComponent(float vx, float vy, float maxSpeed) {
        this.vx = vx;
        this.vy = vy;
        this.maxSpeed = maxSpeed;
    }

    public float getVx() {
        return vx;
    }

    public void setVx(float vx) {
        this.vx = vx;
    }

    public float getVy() {
        return vy;
    }

    public void setVy(float vy) {
        this.vy = vy;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void set(float vx, float vy) {
        this.vx = vx;
        this.vy = vy;
    }

    public void normalize() {
        float length = (float) Math.sqrt(vx * vx + vy * vy);
        if (length > 0) {
            vx /= length;
            vy /= length;
        }
    }

    public void clampToMaxSpeed() {
        float speed = (float) Math.sqrt(vx * vx + vy * vy);
        if (speed > maxSpeed) {
            float ratio = maxSpeed / speed;
            vx *= ratio;
            vy *= ratio;
        }
    }

    public float getSpeed() {
        return (float) Math.sqrt(vx * vx + vy * vy);
    }
}
