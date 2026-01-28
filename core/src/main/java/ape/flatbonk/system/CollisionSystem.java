package ape.flatbonk.system;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.component.*;
import ape.flatbonk.entity.factory.PickupFactory;
import ape.flatbonk.state.GameState;
import ape.flatbonk.util.Constants;

import java.util.List;

public class CollisionSystem implements GameSystem {
    private final EntityManager entityManager;
    private final GameState gameState;

    public CollisionSystem(EntityManager entityManager, GameState gameState) {
        this.entityManager = entityManager;
        this.gameState = gameState;
    }

    @Override
    public void update(float delta) {
        Entity player = entityManager.getPlayerEntity();
        if (player == null || !player.isActive()) return;

        TransformComponent playerTransform = player.getTransformComponent();
        CollisionComponent playerCollision = player.getCollisionComponent();
        HealthComponent playerHealth = player.getHealthComponent();

        if (playerTransform == null || playerCollision == null) return;

        // Check player-monster collisions
        List<Entity> monsters = entityManager.getEntitiesWithTag("monster");
        for (Entity monster : monsters) {
            if (!monster.isActive()) continue;

            TransformComponent monsterTransform = monster.getTransformComponent();
            CollisionComponent monsterCollision = monster.getCollisionComponent();

            if (monsterTransform == null || monsterCollision == null) continue;

            if (playerCollision.overlaps(playerTransform, monsterTransform, monsterCollision)) {
                // Knockback monster away from player on any collision
                VelocityComponent monsterVelocity = monster.getVelocityComponent();
                if (monsterVelocity != null) {
                    float dx = monsterTransform.getX() - playerTransform.getX();
                    float dy = monsterTransform.getY() - playerTransform.getY();
                    float len = (float) Math.sqrt(dx * dx + dy * dy);
                    if (len > 0) {
                        dx /= len;
                        dy /= len;
                        monsterVelocity.addKnockback(dx, dy, Constants.KNOCKBACK_FORCE * 1.5f);
                    }
                }

                // Player takes damage from monster
                if (playerHealth != null && !playerHealth.isInvincible()) {
                    int damage = (int) (10 * gameState.getDamageMultiplier());
                    PlayerStatsComponent stats = player.getPlayerStatsComponent();
                    if (stats != null) {
                        damage = stats.calculateDamageReduction(damage);
                    }
                    playerHealth.damage(damage);

                    if (playerHealth.isDead()) {
                        gameState.setGameOver(true);
                    }
                }
            }
        }

        // Check player bullet - monster collisions
        List<Entity> bullets = entityManager.getEntitiesWithTag("playerBullet");
        bullets.addAll(entityManager.getEntitiesWithTag("homingBullet"));
        bullets.addAll(entityManager.getEntitiesWithTag("piercingBullet"));
        bullets.addAll(entityManager.getEntitiesWithTag("lifeDrainBullet"));

        for (Entity bullet : bullets) {
            if (!bullet.isActive()) continue;

            TransformComponent bulletTransform = bullet.getTransformComponent();
            CollisionComponent bulletCollision = bullet.getCollisionComponent();
            DropComponent bulletDamage = bullet.getDropComponent();

            if (bulletTransform == null || bulletCollision == null) continue;

            for (Entity monster : monsters) {
                if (!monster.isActive()) continue;

                TransformComponent monsterTransform = monster.getTransformComponent();
                CollisionComponent monsterCollision = monster.getCollisionComponent();
                HealthComponent monsterHealth = monster.getHealthComponent();

                if (monsterTransform == null || monsterCollision == null || monsterHealth == null) continue;

                if (bulletCollision.overlaps(bulletTransform, monsterTransform, monsterCollision)) {
                    // Apply damage
                    int damage = bulletDamage != null ? bulletDamage.getXpValue() : 10;
                    PlayerStatsComponent playerStats = player.getPlayerStatsComponent();
                    if (playerStats != null) {
                        damage = (int) (damage * playerStats.getDamageModifier());
                    }

                    monsterHealth.damage(damage);

                    // Apply knockback to monster
                    VelocityComponent monsterVelocity = monster.getVelocityComponent();
                    if (monsterVelocity != null) {
                        float dx = monsterTransform.getX() - bulletTransform.getX();
                        float dy = monsterTransform.getY() - bulletTransform.getY();
                        float len = (float) Math.sqrt(dx * dx + dy * dy);
                        if (len > 0) {
                            dx /= len;
                            dy /= len;
                            monsterVelocity.addKnockback(dx, dy, Constants.KNOCKBACK_FORCE);
                        }
                    }

                    // Track damage dealt as score
                    gameState.addDamageDealt(damage);

                    // Handle life drain healing
                    if (bullet.getTag().equals("lifeDrainBullet")) {
                        if (playerHealth != null) {
                            int healAmount = Math.max(1, damage / 4); // Heal 25% of damage dealt
                            playerHealth.heal(healAmount);
                        }
                    }

                    // Handle bullet based on type
                    if (bullet.getTag().equals("piercingBullet")) {
                        HealthComponent pierceCount = bullet.getHealthComponent();
                        if (pierceCount != null) {
                            pierceCount.damage(1);
                            if (pierceCount.isDead()) {
                                entityManager.removeEntity(bullet);
                            }
                        }
                    } else {
                        entityManager.removeEntity(bullet);
                    }

                    // Check if monster died
                    if (monsterHealth.isDead()) {
                        // Spawn burst of pickups that scatter
                        DropComponent drop = monster.getDropComponent();
                        if (drop != null) {
                            if (drop.getXpValue() > 0) {
                                PickupFactory.createXPOrbBurst(entityManager,
                                    monsterTransform.getX(), monsterTransform.getY(),
                                    drop.getXpValue());
                            }
                            if (drop.getMoneyValue() > 0) {
                                PickupFactory.createMoneyBurst(entityManager,
                                    monsterTransform.getX(), monsterTransform.getY(),
                                    drop.getMoneyValue());
                            }
                        }
                        entityManager.removeEntity(monster);
                        gameState.addKill();
                    }

                    break;
                }
            }
        }

        // Check player bullet - fireball hazard collisions (destroy fireballs when shot)
        List<Entity> fireballs = entityManager.getEntitiesWithTag("hazard_fireball");
        for (Entity bullet : bullets) {
            if (!bullet.isActive()) continue;

            TransformComponent bulletTransform = bullet.getTransformComponent();
            CollisionComponent bulletCollision = bullet.getCollisionComponent();

            if (bulletTransform == null || bulletCollision == null) continue;

            for (Entity fireball : fireballs) {
                if (!fireball.isActive()) continue;

                TransformComponent fireballTransform = fireball.getTransformComponent();
                CollisionComponent fireballCollision = fireball.getCollisionComponent();

                if (fireballTransform == null || fireballCollision == null) continue;

                // Check overlap using radius-based collision
                float dx = fireballTransform.getX() - bulletTransform.getX();
                float dy = fireballTransform.getY() - bulletTransform.getY();
                float distSq = dx * dx + dy * dy;
                float radiusSum = bulletCollision.getRadius() + fireballCollision.getRadius();

                if (distSq <= radiusSum * radiusSum) {
                    // Destroy fireball when hit
                    entityManager.removeEntity(fireball);
                    // Destroy bullet too (unless piercing)
                    if (!bullet.getTag().equals("piercingBullet")) {
                        entityManager.removeEntity(bullet);
                    }
                    break;
                }
            }
        }

        // Update health
        if (playerHealth != null) {
            playerHealth.update(delta);
        }
    }

    @Override
    public int getPriority() {
        return 20;
    }
}
