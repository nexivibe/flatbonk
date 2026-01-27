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

        // Get player position
        Entity player = entityManager.getPlayerEntity();
        float playerX = Constants.WORLD_WIDTH / 2;
        float playerY = Constants.WORLD_HEIGHT / 2;
        if (player != null && player.getTransformComponent() != null) {
            playerX = player.getTransformComponent().getX();
            playerY = player.getTransformComponent().getY();
        }

        // Spawn from random angle around player at spawn distance
        float angle = MathUtils.random(360f);
        float spawnDist = Constants.SPAWN_DISTANCE;
        float x = playerX + MathUtils.cosDeg(angle) * spawnDist;
        float y = playerY + MathUtils.sinDeg(angle) * spawnDist;

        // Velocity toward player with some randomness
        float toPlayerX = playerX - x;
        float toPlayerY = playerY - y;
        float len = (float) Math.sqrt(toPlayerX * toPlayerX + toPlayerY * toPlayerY);
        float speed = MathUtils.random(150f, 250f);
        float vx = (toPlayerX / len) * speed + MathUtils.random(-50f, 50f);
        float vy = (toPlayerY / len) * speed + MathUtils.random(-50f, 50f);

        TransformComponent transform = new TransformComponent(x, y);
        fireball.addComponent("transform", transform);

        VelocityComponent velocity = new VelocityComponent(400f);
        velocity.set(vx, vy);
        fireball.addComponent("velocity", velocity);

        RenderComponent render = new RenderComponent();
        render.setColor(new Color(1f, 0.4f, 0.1f, 0.8f));
        render.setSize(Constants.HAZARD_SIZE * 0.5f);
        fireball.addComponent("render", render);

        CollisionComponent collision = new CollisionComponent(
            Constants.HAZARD_SIZE * 0.4f,
            CollisionComponent.MASK_HAZARD,
            CollisionComponent.MASK_PLAYER
        );
        fireball.addComponent("collision", collision);

        LifetimeComponent lifetime = new LifetimeComponent(Constants.HAZARD_LIFETIME);
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
