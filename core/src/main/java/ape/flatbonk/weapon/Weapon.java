package ape.flatbonk.weapon;

import ape.flatbonk.entity.EntityManager;

public abstract class Weapon {
    protected int level;
    protected float cooldown;
    protected float currentCooldown;
    protected int baseDamage;
    protected float baseSpeed;

    public Weapon(float cooldown, int baseDamage, float baseSpeed) {
        this.level = 1;
        this.cooldown = cooldown;
        this.currentCooldown = 0;
        this.baseDamage = baseDamage;
        this.baseSpeed = baseSpeed;
    }

    public void update(float delta) {
        if (currentCooldown > 0) {
            currentCooldown -= delta;
        }
    }

    public boolean canFire() {
        return currentCooldown <= 0;
    }

    public void fire(EntityManager entityManager, float x, float y, float targetX, float targetY, float cooldownMod) {
        if (canFire()) {
            doFire(entityManager, x, y, targetX, targetY);
            currentCooldown = cooldown * cooldownMod;
        }
    }

    protected abstract void doFire(EntityManager entityManager, float x, float y, float targetX, float targetY);

    public void upgrade() {
        level++;
        onUpgrade();
    }

    protected void onUpgrade() {
        // Override in subclasses for specific upgrade behavior
    }

    public int getLevel() {
        return level;
    }

    public int getDamage() {
        return baseDamage + (level - 1) * 5;
    }

    public float getSpeed() {
        return baseSpeed;
    }

    public abstract String getName();

    public String getDescription() {
        return "A powerful weapon";
    }
}
