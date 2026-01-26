package ape.flatbonk.entity.component;

public class HealthComponent {
    private int currentHealth;
    private int maxHealth;
    private float invincibilityTimer;
    private float invincibilityDuration;

    public HealthComponent(int maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.invincibilityTimer = 0;
        this.invincibilityDuration = 0.5f;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = Math.min(currentHealth, maxHealth);
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public float getInvincibilityTimer() {
        return invincibilityTimer;
    }

    public void setInvincibilityTimer(float invincibilityTimer) {
        this.invincibilityTimer = invincibilityTimer;
    }

    public float getInvincibilityDuration() {
        return invincibilityDuration;
    }

    public void setInvincibilityDuration(float invincibilityDuration) {
        this.invincibilityDuration = invincibilityDuration;
    }

    public boolean isInvincible() {
        return invincibilityTimer > 0;
    }

    public void update(float delta) {
        if (invincibilityTimer > 0) {
            invincibilityTimer -= delta;
        }
    }

    public void damage(int amount) {
        if (!isInvincible()) {
            currentHealth -= amount;
            invincibilityTimer = invincibilityDuration;
        }
    }

    public void heal(int amount) {
        currentHealth = Math.min(currentHealth + amount, maxHealth);
    }

    public boolean isDead() {
        return currentHealth <= 0;
    }

    public float getHealthPercentage() {
        return (float) currentHealth / maxHealth;
    }
}
