package ape.flatbonk.system;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.component.*;
import ape.flatbonk.entity.factory.PickupFactory;
import ape.flatbonk.state.GameState;

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
                        // Spawn pickups
                        DropComponent drop = monster.getDropComponent();
                        if (drop != null) {
                            if (drop.getXpValue() > 0) {
                                PickupFactory.createXPOrb(entityManager,
                                    monsterTransform.getX(), monsterTransform.getY(),
                                    drop.getXpValue());
                            }
                            if (drop.getMoneyValue() > 0) {
                                PickupFactory.createMoneyPickup(entityManager,
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

        // Update health
        if (playerHealth != null) {
            playerHealth.update(delta);
        }

        entityManager.update();
    }

    @Override
    public int getPriority() {
        return 20;
    }
}
