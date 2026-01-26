package ape.flatbonk.hazard;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.component.*;
import ape.flatbonk.state.GameState;
import ape.flatbonk.util.Constants;

import java.util.List;

public class FireballHazard extends Hazard {

    public FireballHazard() {
        super(2.0f);
    }

    @Override
    protected void spawnHazard(EntityManager entityManager, GameState gameState) {
        Entity fireball = entityManager.createEntity();
        fireball.setTag("hazard_fireball");

        // Spawn from random edge
        float x, y, vx, vy;
        int edge = MathUtils.random(3);

        switch (edge) {
            case 0: // Top
                x = MathUtils.random(Constants.WORLD_WIDTH);
                y = Constants.WORLD_HEIGHT;
                vx = MathUtils.random(-100f, 100f);
                vy = -MathUtils.random(150f, 300f);
                break;
            case 1: // Right
                x = Constants.WORLD_WIDTH;
                y = MathUtils.random(Constants.CONTROL_BAR_HEIGHT, Constants.WORLD_HEIGHT);
                vx = -MathUtils.random(150f, 300f);
                vy = MathUtils.random(-100f, 100f);
                break;
            case 2: // Bottom
                x = MathUtils.random(Constants.WORLD_WIDTH);
                y = Constants.CONTROL_BAR_HEIGHT;
                vx = MathUtils.random(-100f, 100f);
                vy = MathUtils.random(150f, 300f);
                break;
            default: // Left
                x = 0;
                y = MathUtils.random(Constants.CONTROL_BAR_HEIGHT, Constants.WORLD_HEIGHT);
                vx = MathUtils.random(150f, 300f);
                vy = MathUtils.random(-100f, 100f);
                break;
        }

        TransformComponent transform = new TransformComponent(x, y);
        fireball.addComponent("transform", transform);

        VelocityComponent velocity = new VelocityComponent(400f);
        velocity.set(vx, vy);
        fireball.addComponent("velocity", velocity);

        RenderComponent render = new RenderComponent();
        render.setColor(new Color(1f, 0.4f, 0.1f, 1f));
        render.setSize(15f);
        fireball.addComponent("render", render);

        CollisionComponent collision = new CollisionComponent(
            12f,
            CollisionComponent.MASK_HAZARD,
            CollisionComponent.MASK_PLAYER
        );
        fireball.addComponent("collision", collision);

        LifetimeComponent lifetime = new LifetimeComponent(5f);
        fireball.addComponent("lifetime", lifetime);
    }

    @Override
    protected void updateHazards(float delta, EntityManager entityManager, GameState gameState) {
        Entity player = entityManager.getPlayerEntity();
        if (player == null || !player.isActive()) return;

        TransformComponent playerTransform = player.getTransformComponent();
        CollisionComponent playerCollision = player.getCollisionComponent();
        HealthComponent playerHealth = player.getHealthComponent();

        List<Entity> fireballs = entityManager.getEntitiesWithTag("hazard_fireball");
        for (Entity fireball : fireballs) {
            if (!fireball.isActive()) continue;

            TransformComponent fireballTransform = fireball.getTransformComponent();
            CollisionComponent fireballCollision = fireball.getCollisionComponent();

            if (fireballTransform != null && fireballCollision != null &&
                playerCollision != null && playerTransform != null) {

                if (playerCollision.overlaps(playerTransform, fireballTransform, fireballCollision)) {
                    if (playerHealth != null && !playerHealth.isInvincible()) {
                        int damage = (int) (15 * gameState.getDamageMultiplier());
                        playerHealth.damage(damage);
                        entityManager.removeEntity(fireball);

                        if (playerHealth.isDead()) {
                            gameState.setGameOver(true);
                        }
                    }
                }
            }
        }
    }
}
