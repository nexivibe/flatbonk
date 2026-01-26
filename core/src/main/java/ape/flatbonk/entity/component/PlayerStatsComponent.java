package ape.flatbonk.entity.component;

public class PlayerStatsComponent {
    private float speedModifier;
    private float damageModifier;
    private float cooldownModifier;
    private float pickupRangeModifier;
    private float healthRegenRate;
    private int armor;

    public PlayerStatsComponent() {
        this.speedModifier = 1f;
        this.damageModifier = 1f;
        this.cooldownModifier = 1f;
        this.pickupRangeModifier = 1f;
        this.healthRegenRate = 0f;
        this.armor = 0;
    }

    public float getSpeedModifier() {
        return speedModifier;
    }

    public void setSpeedModifier(float speedModifier) {
        this.speedModifier = speedModifier;
    }

    public void addSpeedModifier(float amount) {
        this.speedModifier += amount;
    }

    public float getDamageModifier() {
        return damageModifier;
    }

    public void setDamageModifier(float damageModifier) {
        this.damageModifier = damageModifier;
    }

    public void addDamageModifier(float amount) {
        this.damageModifier += amount;
    }

    public float getCooldownModifier() {
        return cooldownModifier;
    }

    public void setCooldownModifier(float cooldownModifier) {
        this.cooldownModifier = cooldownModifier;
    }

    public void addCooldownModifier(float amount) {
        this.cooldownModifier = Math.max(0.1f, this.cooldownModifier - amount);
    }

    public float getPickupRangeModifier() {
        return pickupRangeModifier;
    }

    public void setPickupRangeModifier(float pickupRangeModifier) {
        this.pickupRangeModifier = pickupRangeModifier;
    }

    public void addPickupRangeModifier(float amount) {
        this.pickupRangeModifier += amount;
    }

    public float getHealthRegenRate() {
        return healthRegenRate;
    }

    public void setHealthRegenRate(float healthRegenRate) {
        this.healthRegenRate = healthRegenRate;
    }

    public void addHealthRegenRate(float amount) {
        this.healthRegenRate += amount;
    }

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public void addArmor(int amount) {
        this.armor += amount;
    }

    public int calculateDamageReduction(int rawDamage) {
        return Math.max(1, rawDamage - armor);
    }
}
