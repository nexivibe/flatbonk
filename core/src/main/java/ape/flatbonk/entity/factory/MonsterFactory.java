package ape.flatbonk.entity.factory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.component.*;
import ape.flatbonk.render.ShapeDefinition;
import ape.flatbonk.state.GameState;
import ape.flatbonk.util.Constants;

public class MonsterFactory {

    // Base health per polygon side, scaled by player level - balanced for fun pacing
    private static final int BASE_HEALTH_PER_SIDE = 2;
    private static final float LEVEL_HEALTH_SCALE = 0.06f;  // +6% health per player level (gentler scaling)

    public static Entity createMonster(EntityManager entityManager, GameState gameState) {
        Entity monster = entityManager.createEntity();
        monster.setTag("monster");

        // Random number of sides (4-14, since above 3 and below 15)
        // Bias toward lower sides for easier early game
        int sides = MathUtils.random(4, 10);
        if (gameState.getElapsedTime() > 30f) {
            sides = MathUtils.random(4, 12);
        }
        if (gameState.getElapsedTime() > 60f) {
            sides = MathUtils.random(5, 14);
        }

        // Get player level for health scaling
        int playerLevel = gameState.getPlayerLevel();

        // Calculate health: proportional to sides and player level
        int baseHealth = sides * BASE_HEALTH_PER_SIDE;
        float levelMultiplier = 1f + (playerLevel - 1) * LEVEL_HEALTH_SCALE;
        int scaledHealth = (int) (baseHealth * levelMultiplier * gameState.getHealthMultiplier());

        // Size based on sides (more sides = slightly bigger)
        float size = 10f + sides * 0.7f;

        // Speed - slower overall for better dodging, scales with sides
        float speed = 90f - sides * 3f;
        speed = Math.max(45f, speed);

        // XP and money scale with difficulty (sides^1.5 for satisfying rewards)
        int xpValue = (int) (sides * 1.5f);
        int moneyValue = sides > 6 ? (sides - 5) : 0;

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
        x = MathUtils.clamp(x, size, Constants.WORLD_WIDTH - size);
        y = MathUtils.clamp(y, Constants.CONTROL_BAR_HEIGHT + size, Constants.WORLD_HEIGHT - size);

        // Transform
        TransformComponent transform = new TransformComponent(x, y);
        transform.setRotation(MathUtils.random(360f));
        monster.addComponent("transform", transform);

        // Velocity
        VelocityComponent velocity = new VelocityComponent(speed);
        monster.addComponent("velocity", velocity);

        // Health
        HealthComponent health = new HealthComponent(scaledHealth);
        health.setInvincibilityDuration(0.1f);
        monster.addComponent("health", health);

        // Render - irregular polygon with random vertex offsets and organs
        RenderComponent render = new RenderComponent();
        render.setColor(getMonsterColor(sides));
        render.setSize(size);
        render.setPolygonSides(sides);
        render.setVertexOffsets(ShapeDefinition.generateVertexOffsets(sides));
        // Add Flatlandia-style organs (1-3 based on sides)
        int organCount = Math.max(1, sides / 4);
        render.setOrganCount(organCount);
        render.setOrganOffsets(ShapeDefinition.generateOrganOffsets(organCount, size * 0.4f));
        monster.addComponent("render", render);

        // Collision
        CollisionComponent collision = new CollisionComponent(
            size * 0.7f,
            CollisionComponent.MASK_MONSTER,
            CollisionComponent.MASK_PLAYER | CollisionComponent.MASK_PLAYER_BULLET
        );
        monster.addComponent("collision", collision);

        // AI
        AIComponent ai = new AIComponent(AIComponent.AIBehavior.CHASE, speed);
        monster.addComponent("ai", ai);

        // Drops
        DropComponent drop = new DropComponent(xpValue, moneyValue);
        monster.addComponent("drop", drop);

        return monster;
    }

    /**
     * Create a boss monster - 3x size, 8x health, 10x rewards (generous for achievement feel)
     */
    public static Entity createBossMonster(EntityManager entityManager, GameState gameState) {
        Entity monster = entityManager.createEntity();
        monster.setTag("monster");

        // Boss has many sides (10-14)
        int sides = MathUtils.random(10, 14);

        // Get player level for health scaling
        int playerLevel = gameState.getPlayerLevel();

        // Boss health: 8x normal (reduced from 10x for better pacing)
        int baseHealth = sides * BASE_HEALTH_PER_SIDE * 8;
        float levelMultiplier = 1f + (playerLevel - 1) * LEVEL_HEALTH_SCALE;
        int scaledHealth = (int) (baseHealth * levelMultiplier * gameState.getHealthMultiplier());

        // Boss size: 2.5x normal (slightly smaller for fairer dodging)
        float normalSize = 10f + sides * 0.7f;
        float size = normalSize * 2.5f;

        // Boss is slower
        float speed = 40f;

        // Boss rewards: 10x normal (very rewarding kill!)
        int xpValue = (int) (sides * 1.5f * 10);
        int moneyValue = sides * 3;

        // Get player position for spawning
        Entity player = entityManager.getPlayerEntity();
        float playerX = Constants.WORLD_WIDTH / 2;
        float playerY = Constants.WORLD_HEIGHT / 2;
        if (player != null && player.getTransformComponent() != null) {
            playerX = player.getTransformComponent().getX();
            playerY = player.getTransformComponent().getY();
        }

        // Spawn at edge of screen
        float angle = MathUtils.random(360f);
        float spawnDist = Constants.SPAWN_DISTANCE * 1.2f;
        float x = playerX + MathUtils.cosDeg(angle) * spawnDist;
        float y = playerY + MathUtils.sinDeg(angle) * spawnDist;

        // Clamp to world bounds
        x = MathUtils.clamp(x, size, Constants.WORLD_WIDTH - size);
        y = MathUtils.clamp(y, Constants.CONTROL_BAR_HEIGHT + size, Constants.WORLD_HEIGHT - size);

        // Transform
        TransformComponent transform = new TransformComponent(x, y);
        transform.setRotation(MathUtils.random(360f));
        monster.addComponent("transform", transform);

        // Velocity
        VelocityComponent velocity = new VelocityComponent(speed);
        monster.addComponent("velocity", velocity);

        // Health
        HealthComponent health = new HealthComponent(scaledHealth);
        health.setInvincibilityDuration(0.05f);
        monster.addComponent("health", health);

        // Render - big irregular polygon with boss flag and many organs
        RenderComponent render = new RenderComponent();
        render.setColor(new Color(0.9f, 0.1f, 0.3f, 1f));  // Bright red-pink for boss
        render.setSize(size);
        render.setPolygonSides(sides);
        render.setVertexOffsets(ShapeDefinition.generateVertexOffsets(sides));
        render.setBoss(true);
        // Boss has more visible organs (4-6)
        int organCount = MathUtils.random(4, 6);
        render.setOrganCount(organCount);
        render.setOrganOffsets(ShapeDefinition.generateOrganOffsets(organCount, size * 0.35f));
        monster.addComponent("render", render);

        // Collision
        CollisionComponent collision = new CollisionComponent(
            size * 0.65f,
            CollisionComponent.MASK_MONSTER,
            CollisionComponent.MASK_PLAYER | CollisionComponent.MASK_PLAYER_BULLET
        );
        monster.addComponent("collision", collision);

        // AI
        AIComponent ai = new AIComponent(AIComponent.AIBehavior.CHASE, speed);
        monster.addComponent("ai", ai);

        // Drops - big rewards!
        DropComponent drop = new DropComponent(xpValue, moneyValue);
        monster.addComponent("drop", drop);

        return monster;
    }

    private static Color getMonsterColor(int sides) {
        // Color varies by number of sides - fewer sides = more orange, more sides = more red/purple
        float hue = 0.02f - (sides - 4) * 0.005f;  // Range from orange-red to deep red
        if (hue < 0) hue += 1f;

        // Simple HSV to RGB approximation for red-orange range
        float r = 0.7f + (14 - sides) * 0.02f;
        float g = 0.2f + (sides - 4) * 0.015f;
        float b = 0.1f + (sides - 4) * 0.02f;

        return new Color(
            MathUtils.clamp(r, 0.5f, 1f),
            MathUtils.clamp(g, 0.1f, 0.4f),
            MathUtils.clamp(b, 0.1f, 0.4f),
            1f
        );
    }

}
