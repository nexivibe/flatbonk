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
        BASIC(30, 10, 80f, 1, 0, 15f),
        FAST(20, 5, 150f, 1, 0, 12f),
        TANK(100, 20, 50f, 3, 1, 25f),
        SWARM(15, 5, 100f, 1, 0, 10f),
        ELITE(150, 25, 70f, 5, 3, 30f);

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

        // Spawn at random edge of screen
        float x, y;
        int edge = MathUtils.random(3);
        switch (edge) {
            case 0: // Top
                x = MathUtils.random(Constants.WORLD_WIDTH);
                y = Constants.WORLD_HEIGHT + type.size;
                break;
            case 1: // Right
                x = Constants.WORLD_WIDTH + type.size;
                y = MathUtils.random(Constants.CONTROL_BAR_HEIGHT, Constants.WORLD_HEIGHT);
                break;
            case 2: // Bottom
                x = MathUtils.random(Constants.WORLD_WIDTH);
                y = Constants.CONTROL_BAR_HEIGHT - type.size;
                break;
            default: // Left
                x = -type.size;
                y = MathUtils.random(Constants.CONTROL_BAR_HEIGHT, Constants.WORLD_HEIGHT);
                break;
        }

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

        // Collision
        CollisionComponent collision = new CollisionComponent(
            type.size / 2,
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
        // Unlock monster types over time
        if (elapsedTime < 30f) {
            return MonsterType.BASIC;
        } else if (elapsedTime < 60f) {
            return MathUtils.randomBoolean(0.7f) ? MonsterType.BASIC : MonsterType.FAST;
        } else if (elapsedTime < 90f) {
            float roll = MathUtils.random();
            if (roll < 0.5f) return MonsterType.BASIC;
            if (roll < 0.8f) return MonsterType.FAST;
            return MonsterType.TANK;
        } else if (elapsedTime < 120f) {
            float roll = MathUtils.random();
            if (roll < 0.3f) return MonsterType.BASIC;
            if (roll < 0.5f) return MonsterType.FAST;
            if (roll < 0.7f) return MonsterType.TANK;
            return MonsterType.SWARM;
        } else {
            float roll = MathUtils.random();
            if (roll < 0.2f) return MonsterType.BASIC;
            if (roll < 0.35f) return MonsterType.FAST;
            if (roll < 0.55f) return MonsterType.TANK;
            if (roll < 0.75f) return MonsterType.SWARM;
            return MonsterType.ELITE;
        }
    }
}
