package ape.flatbonk.system;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.component.*;
import ape.flatbonk.entity.factory.PickupFactory;
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

        // Process diamonds (special bonus pickups)
        List<Entity> diamonds = entityManager.getEntitiesWithTag("diamond");
        for (Entity diamond : diamonds) {
            if (!diamond.isActive()) continue;
            processDiamond(diamond, playerTransform, playerCollision, player);
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
        LifetimeComponent lifetime = pickup.getLifetimeComponent();

        if (pickupTransform == null) return;

        // Check if scatter phase is over (lifetime expired but not destroyed)
        boolean scatterComplete = (lifetime != null && lifetime.isExpired());

        // If still scattering, update lifetime and let velocity carry it
        if (lifetime != null && !lifetime.isExpired()) {
            lifetime.update(delta);
            if (lifetime.isExpired() && pickupVelocity != null) {
                // Stop scatter velocity
                pickupVelocity.set(0, 0);
            }
            return; // Don't process magnet during scatter
        }

        float dist = playerTransform.distanceTo(pickupTransform);

        // Magnet pull (only after scatter is complete)
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
                Entity player = entityManager.getPlayerEntity();
                PlayerStatsComponent playerStats = player != null ? player.getPlayerStatsComponent() : null;

                switch (type) {
                    case "xp":
                        int xpAmount = drop.getXpValue();
                        if (playerStats != null) {
                            xpAmount = (int)(xpAmount * playerStats.getXpBonusModifier());
                        }
                        gameState.addXP(xpAmount);
                        break;
                    case "money":
                        gameState.addMoney(drop.getMoneyValue());
                        break;
                    case "health":
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

    private void processDiamond(Entity diamond, TransformComponent playerTransform,
                                 CollisionComponent playerCollision, Entity player) {
        TransformComponent diamondTransform = diamond.getTransformComponent();
        CollisionComponent diamondCollision = diamond.getCollisionComponent();

        if (diamondTransform == null || diamondCollision == null || playerCollision == null) return;

        // Check collection
        if (playerCollision.overlaps(playerTransform, diamondTransform, diamondCollision)) {
            // Random bonus!
            int bonusType = MathUtils.random(4);
            String bonusText = "";
            Color textColor = new Color(0.4f, 1f, 1f, 1f);

            switch (bonusType) {
                case 0:
                    // XP bonus
                    int xpBonus = 20 + gameState.getPlayerLevel() * 5;
                    gameState.addXP(xpBonus);
                    bonusText = "+" + xpBonus + " XP!";
                    textColor = new Color(0.3f, 0.8f, 1f, 1f);
                    break;
                case 1:
                    // Money bonus
                    int moneyBonus = 5 + gameState.getPlayerLevel() * 2;
                    gameState.addMoney(moneyBonus);
                    bonusText = "+" + moneyBonus + " Gold!";
                    textColor = new Color(1f, 0.85f, 0.2f, 1f);
                    break;
                case 2:
                    // Health bonus
                    HealthComponent health = player.getHealthComponent();
                    if (health != null) {
                        int healAmount = 20 + gameState.getPlayerLevel() * 3;
                        health.heal(healAmount);
                        bonusText = "+" + healAmount + " HP!";
                        textColor = new Color(0.2f, 1f, 0.3f, 1f);
                    }
                    break;
                case 3:
                    // Score bonus
                    int scoreBonus = 500 + gameState.getPlayerLevel() * 100;
                    gameState.addDamageDealt(scoreBonus);  // Adds to score
                    bonusText = "+" + scoreBonus + " Score!";
                    textColor = new Color(1f, 1f, 1f, 1f);
                    break;
                case 4:
                    // Everything bonus!
                    gameState.addXP(10);
                    gameState.addMoney(3);
                    gameState.addDamageDealt(200);
                    bonusText = "JACKPOT!";
                    textColor = new Color(1f, 0.5f, 1f, 1f);
                    break;
            }

            // Create "Shiny!" text plus bonus text
            PickupFactory.createFloatingText(entityManager,
                diamondTransform.getX(), diamondTransform.getY() + 15,
                "Shiny!", new Color(0.4f, 1f, 1f, 1f));

            PickupFactory.createFloatingText(entityManager,
                diamondTransform.getX(), diamondTransform.getY(),
                bonusText, textColor);

            entityManager.removeEntity(diamond);
        }
    }

    @Override
    public int getPriority() {
        return 25;
    }
}
