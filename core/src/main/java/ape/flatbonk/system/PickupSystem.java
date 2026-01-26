package ape.flatbonk.system;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.component.*;
import ape.flatbonk.state.GameState;
import ape.flatbonk.util.Constants;

import java.util.List;

public class PickupSystem implements GameSystem {
    private final EntityManager entityManager;
    private final GameState gameState;

    public PickupSystem(EntityManager entityManager, GameState gameState) {
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
        PlayerStatsComponent stats = player.getPlayerStatsComponent();

        if (playerTransform == null) return;

        float magnetRange = Constants.XP_MAGNET_RANGE;
        if (stats != null) {
            magnetRange *= stats.getPickupRangeModifier();
        }

        // Process XP orbs
        List<Entity> xpOrbs = entityManager.getEntitiesWithTag("xpOrb");
        for (Entity orb : xpOrbs) {
            if (!orb.isActive()) continue;
            processPickup(orb, playerTransform, playerCollision, magnetRange, delta, "xp");
        }

        // Process money
        List<Entity> money = entityManager.getEntitiesWithTag("money");
        for (Entity coin : money) {
            if (!coin.isActive()) continue;
            processPickup(coin, playerTransform, playerCollision, magnetRange, delta, "money");
        }

        // Process health pickups
        List<Entity> healthPickups = entityManager.getEntitiesWithTag("health");
        for (Entity hp : healthPickups) {
            if (!hp.isActive()) continue;
            processPickup(hp, playerTransform, playerCollision, magnetRange, delta, "health");
        }

        // Health regen
        if (stats != null && playerHealth != null && stats.getHealthRegenRate() > 0) {
            float regen = stats.getHealthRegenRate() * delta;
            playerHealth.heal((int) regen);
        }
    }

    private void processPickup(Entity pickup, TransformComponent playerTransform,
                               CollisionComponent playerCollision, float magnetRange,
                               float delta, String type) {
        TransformComponent pickupTransform = pickup.getTransformComponent();
        VelocityComponent pickupVelocity = pickup.getVelocityComponent();
        CollisionComponent pickupCollision = pickup.getCollisionComponent();
        DropComponent drop = pickup.getDropComponent();

        if (pickupTransform == null) return;

        float dist = playerTransform.distanceTo(pickupTransform);

        // Magnet pull
        if (dist < magnetRange && pickupVelocity != null) {
            float dx = playerTransform.getX() - pickupTransform.getX();
            float dy = playerTransform.getY() - pickupTransform.getY();
            float len = (float) Math.sqrt(dx * dx + dy * dy);

            if (len > 0) {
                float pullSpeed = Constants.PICKUP_SPEED * (1 - dist / magnetRange);
                pickupVelocity.set(dx / len * pullSpeed, dy / len * pullSpeed);
            }
        }

        // Collection
        if (pickupCollision != null && playerCollision != null &&
            playerCollision.overlaps(playerTransform, pickupTransform, pickupCollision)) {

            if (drop != null) {
                switch (type) {
                    case "xp":
                        gameState.addXP(drop.getXpValue());
                        break;
                    case "money":
                        gameState.addMoney(drop.getMoneyValue());
                        break;
                    case "health":
                        Entity player = entityManager.getPlayerEntity();
                        if (player != null) {
                            HealthComponent health = player.getHealthComponent();
                            if (health != null) {
                                health.heal(drop.getXpValue());
                            }
                        }
                        break;
                }
            }

            entityManager.removeEntity(pickup);
        }
    }

    @Override
    public int getPriority() {
        return 25;
    }
}
