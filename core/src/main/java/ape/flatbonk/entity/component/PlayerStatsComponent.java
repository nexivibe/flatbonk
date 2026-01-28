package ape.flatbonk.entity.component;

public class PlayerStatsComponent {
    private float speedModifier;
    private float damageModifier;
    private float cooldownModifier;
    private float pickupRangeModifier;
    private float healthRegenRate;
    private int armor;
    private float dashDistanceModifier;
    private float xpBonusModifier;
    private int projectileBonus;
    private float projectileSizeModifier;
    private float critChance;
    private float critMultiplier;

    public PlayerStatsComponent() {
        this.speedModifier = 1f;
        this.damageModifier = 1f;
        this.cooldownModifier = 1f;
        this.pickupRangeModifier = 1f;
        this.healthRegenRate = 0f;
        this.armor = 0;
        this.dashDistanceModifier = 1f;
        this.xpBonusModifier = 1f;
        this.projectileBonus = 0;
        this.projectileSizeModifier = 1f;
        this.critChance = 0f;
        this.critMultiplier = 2f;
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

    public float getDashDistanceModifier() {
        return dashDistanceModifier;
    }

    public void addDashDistanceModifier(float amount) {
        this.dashDistanceModifier += amount;
    }

    public float getXpBonusModifier() {
        return xpBonusModifier;
    }

    public void addXpBonusModifier(float amount) {
        this.xpBonusModifier += amount;
    }

    public int getProjectileBonus() {
        return projectileBonus;
    }

    public void addProjectileBonus(int amount) {
        this.projectileBonus += amount;
    }

    public float getProjectileSizeModifier() {
        return projectileSizeModifier;
    }

    public void addProjectileSizeModifier(float amount) {
        this.projectileSizeModifier += amount;
    }

    public float getCritChance() {
        return critChance;
    }

    public void addCritChance(float amount) {
        this.critChance = Math.min(0.75f, this.critChance + amount);
    }

    public float getCritMultiplier() {
        return critMultiplier;
    }

    public void addCritMultiplier(float amount) {
        this.critMultiplier += amount;
    }
}
