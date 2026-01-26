package ape.flatbonk.entity.component;

public class AIComponent {
    public enum AIBehavior {
        CHASE,
        WANDER,
        CIRCLE,
        FLEE
    }

    private AIBehavior behavior;
    private float speed;
    private float detectionRange;
    private float attackRange;
    private float attackCooldown;
    private float currentCooldown;

    public AIComponent(AIBehavior behavior, float speed) {
        this.behavior = behavior;
        this.speed = speed;
        this.detectionRange = 500f;
        this.attackRange = 30f;
        this.attackCooldown = 1f;
        this.currentCooldown = 0;
    }

    public AIBehavior getBehavior() {
        return behavior;
    }

    public void setBehavior(AIBehavior behavior) {
        this.behavior = behavior;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getDetectionRange() {
        return detectionRange;
    }

    public void setDetectionRange(float detectionRange) {
        this.detectionRange = detectionRange;
    }

    public float getAttackRange() {
        return attackRange;
    }

    public void setAttackRange(float attackRange) {
        this.attackRange = attackRange;
    }

    public float getAttackCooldown() {
        return attackCooldown;
    }

    public void setAttackCooldown(float attackCooldown) {
        this.attackCooldown = attackCooldown;
    }

    public float getCurrentCooldown() {
        return currentCooldown;
    }

    public void setCurrentCooldown(float currentCooldown) {
        this.currentCooldown = currentCooldown;
    }

    public void update(float delta) {
        if (currentCooldown > 0) {
            currentCooldown -= delta;
        }
    }

    public boolean canAttack() {
        return currentCooldown <= 0;
    }

    public void resetAttackCooldown() {
        currentCooldown = attackCooldown;
    }
}
