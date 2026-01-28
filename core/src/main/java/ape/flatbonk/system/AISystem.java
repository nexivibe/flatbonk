package ape.flatbonk.system;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.component.*;

import java.util.List;

public class AISystem implements GameSystem {
    private final EntityManager entityManager;

    public AISystem(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void update(float delta) {
        Entity player = entityManager.getPlayerEntity();
        if (player == null || !player.isActive()) return;

        TransformComponent playerTransform = player.getTransformComponent();
        if (playerTransform == null) return;

        List<Entity> aiEntities = entityManager.getEntitiesWithComponents("ai", "transform", "velocity");

        for (Entity entity : aiEntities) {
            AIComponent ai = entity.getAIComponent();
            TransformComponent transform = entity.getTransformComponent();
            VelocityComponent velocity = entity.getVelocityComponent();

            ai.update(delta);

            switch (ai.getBehavior()) {
                case CHASE:
                    updateChase(transform, velocity, playerTransform, ai);
                    break;
                case WANDER:
                    updateWander(transform, velocity, ai, delta);
                    break;
                case CIRCLE:
                    updateCircle(transform, velocity, playerTransform, ai, delta);
                    break;
                case FLEE:
                    updateFlee(transform, velocity, playerTransform, ai);
                    break;
            }
        }

        // Monster separation - prevent overlapping
        separateMonsters(aiEntities);

        // Update homing bullets
        updateHomingBullets(playerTransform);
    }

    private void separateMonsters(List<Entity> monsters) {
        float separationForce = 80f;  // How strongly they push apart

        for (int i = 0; i < monsters.size(); i++) {
            Entity a = monsters.get(i);
            if (!a.isActive() || !a.getTag().equals("monster")) continue;

            TransformComponent transformA = a.getTransformComponent();
            VelocityComponent velocityA = a.getVelocityComponent();
            CollisionComponent collisionA = a.getCollisionComponent();

            if (transformA == null || velocityA == null || collisionA == null) continue;

            float radiusA = collisionA.getRadius();

            for (int j = i + 1; j < monsters.size(); j++) {
                Entity b = monsters.get(j);
                if (!b.isActive() || !b.getTag().equals("monster")) continue;

                TransformComponent transformB = b.getTransformComponent();
                VelocityComponent velocityB = b.getVelocityComponent();
                CollisionComponent collisionB = b.getCollisionComponent();

                if (transformB == null || velocityB == null || collisionB == null) continue;

                float radiusB = collisionB.getRadius();
                float minDist = radiusA + radiusB;

                float dx = transformB.getX() - transformA.getX();
                float dy = transformB.getY() - transformA.getY();
                float dist = (float) Math.sqrt(dx * dx + dy * dy);

                if (dist < minDist && dist > 0.1f) {
                    // Overlap detected - push apart
                    float overlap = minDist - dist;
                    float nx = dx / dist;
                    float ny = dy / dist;

                    // Apply separation force as velocity adjustment
                    float pushX = nx * separationForce * (overlap / minDist);
                    float pushY = ny * separationForce * (overlap / minDist);

                    velocityA.setVx(velocityA.getVx() - pushX);
                    velocityA.setVy(velocityA.getVy() - pushY);
                    velocityB.setVx(velocityB.getVx() + pushX);
                    velocityB.setVy(velocityB.getVy() + pushY);
                }
            }
        }
    }

    private void updateChase(TransformComponent transform, VelocityComponent velocity,
                             TransformComponent target, AIComponent ai) {
        float dx = target.getX() - transform.getX();
        float dy = target.getY() - transform.getY();
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist > 0) {
            velocity.set(dx / dist * ai.getSpeed(), dy / dist * ai.getSpeed());
        }
    }

    private void updateWander(TransformComponent transform, VelocityComponent velocity,
                              AIComponent ai, float delta) {
        // Simple wandering behavior
        float angle = (float) (Math.random() * Math.PI * 2);
        velocity.set(
            (float) Math.cos(angle) * ai.getSpeed() * 0.5f,
            (float) Math.sin(angle) * ai.getSpeed() * 0.5f
        );
    }

    private void updateCircle(TransformComponent transform, VelocityComponent velocity,
                              TransformComponent target, AIComponent ai, float delta) {
        float dx = target.getX() - transform.getX();
        float dy = target.getY() - transform.getY();
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist > ai.getDetectionRange()) {
            // Move toward player
            updateChase(transform, velocity, target, ai);
        } else {
            // Circle around player
            float perpX = -dy / dist;
            float perpY = dx / dist;
            velocity.set(perpX * ai.getSpeed(), perpY * ai.getSpeed());
        }
    }

    private void updateFlee(TransformComponent transform, VelocityComponent velocity,
                            TransformComponent target, AIComponent ai) {
        float dx = transform.getX() - target.getX();
        float dy = transform.getY() - target.getY();
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist > 0) {
            velocity.set(dx / dist * ai.getSpeed(), dy / dist * ai.getSpeed());
        }
    }

    private void updateHomingBullets(TransformComponent playerTransform) {
        List<Entity> homingBullets = entityManager.getEntitiesWithTag("homingBullet");
        List<Entity> monsters = entityManager.getEntitiesWithTag("monster");

        for (Entity bullet : homingBullets) {
            if (!bullet.isActive()) continue;

            TransformComponent bulletTransform = bullet.getTransformComponent();
            VelocityComponent velocity = bullet.getVelocityComponent();

            // Find nearest monster
            Entity nearestMonster = null;
            float nearestDist = Float.MAX_VALUE;

            for (Entity monster : monsters) {
                if (!monster.isActive()) continue;
                TransformComponent monsterTransform = monster.getTransformComponent();
                float dist = bulletTransform.distanceTo(monsterTransform);
                if (dist < nearestDist) {
                    nearestDist = dist;
                    nearestMonster = monster;
                }
            }

            if (nearestMonster != null && nearestDist < 300f) {
                TransformComponent target = nearestMonster.getTransformComponent();
                float dx = target.getX() - bulletTransform.getX();
                float dy = target.getY() - bulletTransform.getY();
                float dist = (float) Math.sqrt(dx * dx + dy * dy);

                if (dist > 0) {
                    float speed = velocity.getSpeed();
                    float currentVx = velocity.getVx();
                    float currentVy = velocity.getVy();

                    // Gradually turn toward target
                    float turnRate = 5f;
                    float targetVx = dx / dist * speed;
                    float targetVy = dy / dist * speed;

                    velocity.set(
                        currentVx + (targetVx - currentVx) * turnRate * 0.016f,
                        currentVy + (targetVy - currentVy) * turnRate * 0.016f
                    );
                }
            }
        }
    }

    @Override
    public int getPriority() {
        return 5;
    }
}
