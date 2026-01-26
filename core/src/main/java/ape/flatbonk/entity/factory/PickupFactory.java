package ape.flatbonk.entity.factory;

import com.badlogic.gdx.graphics.Color;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.component.*;

public class PickupFactory {

    public static Entity createXPOrb(EntityManager entityManager, float x, float y, int xpValue) {
        Entity pickup = entityManager.createEntity();
        pickup.setTag("xpOrb");

        // Transform
        TransformComponent transform = new TransformComponent(x, y);
        pickup.addComponent("transform", transform);

        // Velocity (for magnet pull)
        VelocityComponent velocity = new VelocityComponent(0);
        pickup.addComponent("velocity", velocity);

        // Render
        RenderComponent render = new RenderComponent();
        render.setColor(new Color(0.3f, 0.8f, 1f, 1f));
        render.setSize(8f);
        pickup.addComponent("render", render);

        // Collision
        CollisionComponent collision = new CollisionComponent(
            6f,
            CollisionComponent.MASK_PICKUP,
            CollisionComponent.MASK_PLAYER
        );
        pickup.addComponent("collision", collision);

        // Drop (stores XP value)
        DropComponent drop = new DropComponent(xpValue, 0);
        pickup.addComponent("drop", drop);

        return pickup;
    }

    public static Entity createMoneyPickup(EntityManager entityManager, float x, float y, int moneyValue) {
        Entity pickup = entityManager.createEntity();
        pickup.setTag("money");

        // Transform
        TransformComponent transform = new TransformComponent(x, y);
        pickup.addComponent("transform", transform);

        // Velocity (for magnet pull)
        VelocityComponent velocity = new VelocityComponent(0);
        pickup.addComponent("velocity", velocity);

        // Render
        RenderComponent render = new RenderComponent();
        render.setColor(new Color(1f, 0.85f, 0.2f, 1f));
        render.setSize(10f);
        pickup.addComponent("render", render);

        // Collision
        CollisionComponent collision = new CollisionComponent(
            8f,
            CollisionComponent.MASK_PICKUP,
            CollisionComponent.MASK_PLAYER
        );
        pickup.addComponent("collision", collision);

        // Drop (stores money value)
        DropComponent drop = new DropComponent(0, moneyValue);
        pickup.addComponent("drop", drop);

        return pickup;
    }

    public static Entity createHealthPickup(EntityManager entityManager, float x, float y, int healAmount) {
        Entity pickup = entityManager.createEntity();
        pickup.setTag("health");

        // Transform
        TransformComponent transform = new TransformComponent(x, y);
        pickup.addComponent("transform", transform);

        // Velocity
        VelocityComponent velocity = new VelocityComponent(0);
        pickup.addComponent("velocity", velocity);

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
}
