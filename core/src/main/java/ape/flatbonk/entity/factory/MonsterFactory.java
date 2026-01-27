package ape.flatbonk.entity.factory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.component.*;
import ape.flatbonk.state.GameState;
import ape.flatbonk.util.Constants;

public class MonsterFactory {

    public enum MonsterType {
        // Low health for satisfying 1-3 hit kills - scales with difficulty over time
        // (health, damage, speed, xpValue, moneyValue, size)
        BASIC(15, 8, 90f, 5, 1, 14f),     // 1 hit kill
        FAST(12, 6, 180f, 4, 0, 11f),     // 1 hit kill, very fast
        TANK(35, 15, 55f, 12, 2, 22f),    // 2-3 hits
        SWARM(8, 4, 120f, 2, 0, 9f),      // 1 hit, comes in groups
        ELITE(50, 20, 75f, 20, 4, 26f);   // 3-4 hits, rare

        final int baseHealth;
        final int baseDamage;
        final float speed;
        final int xpValue;
        final int moneyValue;
        final float size;

        MonsterType(int baseHealth, int baseDamage, float speed, int xpValue, int moneyValue, float size) {
            this.baseHealth = baseHealth;
            this.baseDamage = baseDamage;
            this.speed = speed;
            this.xpValue = xpValue;
            this.moneyValue = moneyValue;
            this.size = size;
        }
    }

    public static Entity createMonster(EntityManager entityManager, GameState gameState, MonsterType type) {
        Entity monster = entityManager.createEntity();
        monster.setTag("monster");

        // Get player position for spawning around them
        Entity player = entityManager.getPlayerEntity();
        float playerX = Constants.WORLD_WIDTH / 2;
        float playerY = Constants.WORLD_HEIGHT / 2;
        if (player != null && player.getTransformComponent() != null) {
            playerX = player.getTransformComponent().getX();
            playerY = player.getTransformComponent().getY();
        }

        // Spawn at random angle around player at SPAWN_DISTANCE
        float angle = MathUtils.random(360f);
        float spawnDist = Constants.SPAWN_DISTANCE + MathUtils.random(-50f, 50f);
        float x = playerX + MathUtils.cosDeg(angle) * spawnDist;
        float y = playerY + MathUtils.sinDeg(angle) * spawnDist;

        // Clamp to world bounds
        x = MathUtils.clamp(x, type.size, Constants.WORLD_WIDTH - type.size);
        y = MathUtils.clamp(y, Constants.CONTROL_BAR_HEIGHT + type.size, Constants.WORLD_HEIGHT - type.size);

        // Transform
        TransformComponent transform = new TransformComponent(x, y);
        monster.addComponent("transform", transform);

        // Velocity
        VelocityComponent velocity = new VelocityComponent(type.speed);
        monster.addComponent("velocity", velocity);

        // Health with difficulty scaling
        int scaledHealth = (int) (type.baseHealth * gameState.getHealthMultiplier());
        HealthComponent health = new HealthComponent(scaledHealth);
        health.setInvincibilityDuration(0.1f);
        monster.addComponent("health", health);

        // Render - monsters are jagged shapes with red/orange tint
        RenderComponent render = new RenderComponent();
        render.setColor(getMonsterColor(type));
        render.setSize(type.size);
        monster.addComponent("render", render);

        // Collision - use the full visual size for better hit detection
        CollisionComponent collision = new CollisionComponent(
            type.size * 0.75f,
            CollisionComponent.MASK_MONSTER,
            CollisionComponent.MASK_PLAYER | CollisionComponent.MASK_PLAYER_BULLET
        );
        monster.addComponent("collision", collision);

        // AI
        AIComponent ai = new AIComponent(AIComponent.AIBehavior.CHASE, type.speed);
        monster.addComponent("ai", ai);

        // Drops
        DropComponent drop = new DropComponent(type.xpValue, type.moneyValue);
        monster.addComponent("drop", drop);

        return monster;
    }

    private static Color getMonsterColor(MonsterType type) {
        switch (type) {
            case BASIC:
                return new Color(0.8f, 0.3f, 0.3f, 1f);
            case FAST:
                return new Color(0.9f, 0.6f, 0.2f, 1f);
            case TANK:
                return new Color(0.6f, 0.2f, 0.2f, 1f);
            case SWARM:
                return new Color(0.7f, 0.4f, 0.4f, 1f);
            case ELITE:
                return new Color(0.9f, 0.1f, 0.1f, 1f);
            default:
                return Color.RED;
        }
    }

    public static MonsterType getRandomType(float elapsedTime) {
        // Fast unlock of monster types for action gameplay
        if (elapsedTime < 15f) {
            // Early game: mostly basic with some fast
            return MathUtils.randomBoolean(0.8f) ? MonsterType.BASIC : MonsterType.FAST;
        } else if (elapsedTime < 30f) {
            // Mix it up early
            float roll = MathUtils.random();
            if (roll < 0.4f) return MonsterType.BASIC;
            if (roll < 0.7f) return MonsterType.FAST;
            return MonsterType.SWARM;
        } else if (elapsedTime < 50f) {
            // Add tanks
            float roll = MathUtils.random();
            if (roll < 0.25f) return MonsterType.BASIC;
            if (roll < 0.45f) return MonsterType.FAST;
            if (roll < 0.65f) return MonsterType.SWARM;
            return MonsterType.TANK;
        } else if (elapsedTime < 80f) {
            // Full variety, elites start appearing
            float roll = MathUtils.random();
            if (roll < 0.15f) return MonsterType.BASIC;
            if (roll < 0.30f) return MonsterType.FAST;
            if (roll < 0.50f) return MonsterType.SWARM;
            if (roll < 0.75f) return MonsterType.TANK;
            return MonsterType.ELITE;
        } else {
            // Late game: heavy enemies, more elites
            float roll = MathUtils.random();
            if (roll < 0.10f) return MonsterType.BASIC;
            if (roll < 0.25f) return MonsterType.FAST;
            if (roll < 0.45f) return MonsterType.SWARM;
            if (roll < 0.70f) return MonsterType.TANK;
            return MonsterType.ELITE;
        }
    }
}
