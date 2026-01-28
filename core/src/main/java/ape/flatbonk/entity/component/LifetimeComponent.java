package ape.flatbonk.entity.component;

public class LifetimeComponent {
    private float remainingTime;
    private float maxLifetime;
    private boolean destroyOnExpire;

    public LifetimeComponent(float lifetime) {
        this.remainingTime = lifetime;
        this.maxLifetime = lifetime;
        this.destroyOnExpire = true;
    }

    public boolean shouldDestroyOnExpire() {
        return destroyOnExpire;
    }

    public void setDestroyOnExpire(boolean destroy) {
        this.destroyOnExpire = destroy;
    }

    public float getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(float remainingTime) {
        this.remainingTime = remainingTime;
    }

    public float getMaxLifetime() {
        return maxLifetime;
    }

    public void update(float delta) {
        remainingTime -= delta;
    }

    public boolean isExpired() {
        return remainingTime <= 0;
    }

    public float getLifetimePercentage() {
        return remainingTime / maxLifetime;
    }
}
