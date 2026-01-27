package ape.flatbonk.hazard;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.component.*;
import ape.flatbonk.state.GameState;
import ape.flatbonk.util.Constants;

import java.util.List;

public class IcePatchHazard extends Hazard {

    public IcePatchHazard() {
        super(4.0f);
    }

    @Override
    protected void spawnHazard(EntityManager entityManager, GameState gameState) {
        Entity icePatch = entityManager.createEntity();
        icePatch.setTag("hazard_ice");

        // Spawn near player
        Entity player = entityManager.getPlayerEntity();
        float playerX = Constants.WORLD_WIDTH / 2;
        float playerY = Constants.WORLD_HEIGHT / 2;
        if (player != null && player.getTransformComponent() != null) {
            playerX = player.getTransformComponent().getX();
            playerY = player.getTransformComponent().getY();
        }
        float x = playerX + MathUtils.random(-200f, 200f);
        float y = playerY + MathUtils.random(-200f, 200f);
        x = MathUtils.clamp(x, 50f, Constants.WORLD_WIDTH - 50f);
        y = MathUtils.clamp(y, Constants.CONTROL_BAR_HEIGHT + 50f, Constants.WORLD_HEIGHT - 50f);

        TransformComponent transform = new TransformComponent(x, y);
        icePatch.addComponent("transform", transform);

        RenderComponent render = new RenderComponent();
        render.setColor(new Color(0.6f, 0.8f, 1f, 0.4f));
        render.setSize(Constants.HAZARD_SIZE);
        icePatch.addComponent("render", render);

        CollisionComponent collision = new CollisionComponent(
            Constants.HAZARD_SIZE * 0.8f,
            CollisionComponent.MASK_HAZARD,
            CollisionComponent.MASK_PLAYER
        );
        icePatch.addComponent("collision", collision);

        LifetimeComponent lifetime = new LifetimeComponent(Constants.HAZARD_LIFETIME);
        icePatch.addComponent("lifetime", lifetime);
    }

    @Override
    protected void updateHazards(float delta, EntityManager entityManager, GameState gameState) {
        Entity player = entityManager.getPlayerEntity();
        if (player == null || !player.isActive()) return;

        TransformComponent playerTransform = player.getTransformComponent();
        CollisionComponent playerCollision = player.getCollisionComponent();
        VelocityComponent playerVelocity = player.getVelocityComponent();

        List<Entity> icePatches = entityManager.getEntitiesWithTag("hazard_ice");
        boolean onIce = false;

        for (Entity ice : icePatches) {
            if (!ice.isActive()) continue;

            TransformComponent iceTransform = ice.getTransformComponent();
            CollisionComponent iceCollision = ice.getCollisionComponent();

            if (iceTransform != null && iceCollision != null &&
                playerCollision != null && playerTransform != null) {

                if (playerCollision.overlaps(playerTransform, iceTransform, iceCollision)) {
                    onIce = true;
                    break;
                }
            }
        }

        // Apply ice sliding effect
        if (onIce && playerVelocity != null) {
            // Reduce friction - player continues sliding
            float friction = 0.98f;
            playerVelocity.setVx(playerVelocity.getVx() * friction);
            playerVelocity.setVy(playerVelocity.getVy() * friction);
        }
    }
}
