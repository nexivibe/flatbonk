package ape.flatbonk.entity.factory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.component.*;
import ape.flatbonk.util.Constants;

public class PickupFactory {

    private static final float SCATTER_SPEED = 80f;
    private static final float SCATTER_DISTANCE_MIN = 8f;
    private static final float SCATTER_DISTANCE_MAX = 20f;

    /**
     * Create multiple XP orbs that scatter from the drop point
     */
    public static void createXPOrbBurst(EntityManager entityManager, float x, float y, int totalXpValue) {
        // Split XP into multiple orbs (3-6 orbs depending on value)
        int orbCount = Math.min(6, Math.max(3, totalXpValue / 3));
        int xpPerOrb = totalXpValue / orbCount;
        int remainder = totalXpValue % orbCount;

        for (int i = 0; i < orbCount; i++) {
            int xp = xpPerOrb + (i < remainder ? 1 : 0);
            createScatteringXPOrb(entityManager, x, y, xp);
        }
    }

    private static Entity createScatteringXPOrb(EntityManager entityManager, float x, float y, int xpValue) {
        Entity pickup = entityManager.createEntity();
        pickup.setTag("xpOrb");

        // Random scatter direction
        float angle = MathUtils.random(360f);
        float scatterDist = MathUtils.random(SCATTER_DISTANCE_MIN, SCATTER_DISTANCE_MAX);
        float targetX = x + MathUtils.cosDeg(angle) * scatterDist;
        float targetY = y + MathUtils.sinDeg(angle) * scatterDist;

        // Transform - start at drop point
        TransformComponent transform = new TransformComponent(x, y);
        pickup.addComponent("transform", transform);

        // Velocity - scatter outward then stop
        float vx = MathUtils.cosDeg(angle) * SCATTER_SPEED;
        float vy = MathUtils.sinDeg(angle) * SCATTER_SPEED;
        VelocityComponent velocity = new VelocityComponent(vx, vy, SCATTER_SPEED);
        pickup.addComponent("velocity", velocity);

        // Lifetime component to track scatter phase
        LifetimeComponent lifetime = new LifetimeComponent(0.3f); // Scatter for 0.3s
        lifetime.setDestroyOnExpire(false); // Don't destroy, just stop scattering
        pickup.addComponent("lifetime", lifetime);

        // Render
        RenderComponent render = new RenderComponent();
        render.setColor(new Color(0.3f, 0.8f, 1f, 1f));
        render.setSize(6f + Math.min(4f, xpValue / 3f)); // Size based on XP value
        pickup.addComponent("render", render);

        // Collision
        CollisionComponent collision = new CollisionComponent(
            5f,
            CollisionComponent.MASK_PICKUP,
            CollisionComponent.MASK_PLAYER
        );
        pickup.addComponent("collision", collision);

        // Drop (stores XP value)
        DropComponent drop = new DropComponent(xpValue, 0);
        pickup.addComponent("drop", drop);

        return pickup;
    }

    /**
     * Create multiple money pickups that scatter from the drop point
     */
    public static void createMoneyBurst(EntityManager entityManager, float x, float y, int totalMoneyValue) {
        // Split money into multiple coins (2-4 coins)
        int coinCount = Math.min(4, Math.max(2, totalMoneyValue / 2));
        int moneyPerCoin = totalMoneyValue / coinCount;
        int remainder = totalMoneyValue % coinCount;

        for (int i = 0; i < coinCount; i++) {
            int money = moneyPerCoin + (i < remainder ? 1 : 0);
            createScatteringMoney(entityManager, x, y, money);
        }
    }

    private static Entity createScatteringMoney(EntityManager entityManager, float x, float y, int moneyValue) {
        Entity pickup = entityManager.createEntity();
        pickup.setTag("money");

        // Random scatter direction
        float angle = MathUtils.random(360f);
        float scatterDist = MathUtils.random(SCATTER_DISTANCE_MIN, SCATTER_DISTANCE_MAX);

        // Transform
        TransformComponent transform = new TransformComponent(x, y);
        pickup.addComponent("transform", transform);

        // Velocity - scatter outward
        float vx = MathUtils.cosDeg(angle) * SCATTER_SPEED;
        float vy = MathUtils.sinDeg(angle) * SCATTER_SPEED;
        VelocityComponent velocity = new VelocityComponent(vx, vy, SCATTER_SPEED);
        pickup.addComponent("velocity", velocity);

        // Lifetime for scatter phase
        LifetimeComponent lifetime = new LifetimeComponent(0.3f);
        lifetime.setDestroyOnExpire(false);
        pickup.addComponent("lifetime", lifetime);

        // Render
        RenderComponent render = new RenderComponent();
        render.setColor(new Color(1f, 0.85f, 0.2f, 1f));
        render.setSize(8f + Math.min(4f, moneyValue));
        pickup.addComponent("render", render);

        // Collision
        CollisionComponent collision = new CollisionComponent(
            7f,
            CollisionComponent.MASK_PICKUP,
            CollisionComponent.MASK_PLAYER
        );
        pickup.addComponent("collision", collision);

        // Drop (stores money value)
        DropComponent drop = new DropComponent(0, moneyValue);
        pickup.addComponent("drop", drop);

        return pickup;
    }

    // Keep simple versions for compatibility
    public static Entity createXPOrb(EntityManager entityManager, float x, float y, int xpValue) {
        return createScatteringXPOrb(entityManager, x, y, xpValue);
    }

    public static Entity createMoneyPickup(EntityManager entityManager, float x, float y, int moneyValue) {
        return createScatteringMoney(entityManager, x, y, moneyValue);
    }

    public static Entity createHealthPickup(EntityManager entityManager, float x, float y, int healAmount) {
        Entity pickup = entityManager.createEntity();
        pickup.setTag("health");

        // Random scatter
        float angle = MathUtils.random(360f);

        // Transform
        TransformComponent transform = new TransformComponent(x, y);
        pickup.addComponent("transform", transform);

        // Velocity
        float vx = MathUtils.cosDeg(angle) * SCATTER_SPEED * 0.5f;
        float vy = MathUtils.sinDeg(angle) * SCATTER_SPEED * 0.5f;
        VelocityComponent velocity = new VelocityComponent(vx, vy, SCATTER_SPEED);
        pickup.addComponent("velocity", velocity);

        // Lifetime for scatter
        LifetimeComponent lifetime = new LifetimeComponent(0.3f);
        lifetime.setDestroyOnExpire(false);
        pickup.addComponent("lifetime", lifetime);

        // Render
        RenderComponent render = new RenderComponent();
        render.setColor(new Color(0.2f, 1f, 0.3f, 1f));
        render.setSize(12f);
        pickup.addComponent("render", render);

        // Collision
        CollisionComponent collision = new CollisionComponent(
            10f,
            CollisionComponent.MASK_PICKUP,
            CollisionComponent.MASK_PLAYER
        );
        pickup.addComponent("collision", collision);

        // Use drop to store heal amount in xp field
        DropComponent drop = new DropComponent(healAmount, 0);
        pickup.addComponent("drop", drop);

        return pickup;
    }

    /**
     * Create a diamond pickup that gives a random bonus
     */
    public static Entity createDiamond(EntityManager entityManager, float x, float y) {
        Entity pickup = entityManager.createEntity();
        pickup.setTag("diamond");

        // Transform
        TransformComponent transform = new TransformComponent(x, y);
        pickup.addComponent("transform", transform);

        // No velocity - diamonds stay in place
        VelocityComponent velocity = new VelocityComponent(0);
        pickup.addComponent("velocity", velocity);

        // Render - bright cyan diamond shape
        RenderComponent render = new RenderComponent();
        render.setColor(new Color(0.4f, 1f, 1f, 1f));  // Bright cyan
        render.setSize(12f);
        pickup.addComponent("render", render);

        // Collision
        CollisionComponent collision = new CollisionComponent(
            10f,
            CollisionComponent.MASK_PICKUP,
            CollisionComponent.MASK_PLAYER
        );
        pickup.addComponent("collision", collision);

        // Lifetime - diamonds disappear after a while
        LifetimeComponent lifetime = new LifetimeComponent(15f);  // 15 seconds
        pickup.addComponent("lifetime", lifetime);

        return pickup;
    }

    /**
     * Create floating text that rises and fades
     */
    public static Entity createFloatingText(EntityManager entityManager, float x, float y,
            String text, Color color) {
        Entity entity = entityManager.createEntity();
        entity.setTag("floatingText");

        TransformComponent transform = new TransformComponent(x, y);
        entity.addComponent("transform", transform);

        VelocityComponent velocity = new VelocityComponent(50f);
        velocity.set(0, 40f);  // Float upward
        entity.addComponent("velocity", velocity);

        FloatingTextComponent floatingText = new FloatingTextComponent(text, color, 1.5f);
        entity.addComponent("floatingText", floatingText);

        LifetimeComponent lifetime = new LifetimeComponent(1.5f);
        entity.addComponent("lifetime", lifetime);

        return entity;
    }
}
