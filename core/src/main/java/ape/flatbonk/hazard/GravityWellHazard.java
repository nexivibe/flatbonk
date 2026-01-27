package ape.flatbonk.hazard;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.component.*;
import ape.flatbonk.state.GameState;
import ape.flatbonk.util.Constants;

import java.util.List;

public class GravityWellHazard extends Hazard {
    private static final float PULL_STRENGTH = 150f;
    private static final float PULL_RANGE = 150f;

    public GravityWellHazard() {
        super(6.0f);
    }

    @Override
    protected void spawnHazard(EntityManager entityManager, GameState gameState) {
        Entity gravityWell = entityManager.createEntity();
        gravityWell.setTag("hazard_gravity");

        // Spawn near player
        Entity player = entityManager.getPlayerEntity();
        float playerX = Constants.WORLD_WIDTH / 2;
        float playerY = Constants.WORLD_HEIGHT / 2;
        if (player != null && player.getTransformComponent() != null) {
            playerX = player.getTransformComponent().getX();
            playerY = player.getTransformComponent().getY();
        }
        float x = playerX + MathUtils.random(-250f, 250f);
        float y = playerY + MathUtils.random(-250f, 250f);
        x = MathUtils.clamp(x, 100f, Constants.WORLD_WIDTH - 100f);
        y = MathUtils.clamp(y, Constants.CONTROL_BAR_HEIGHT + 100f, Constants.WORLD_HEIGHT - 100f);

        TransformComponent transform = new TransformComponent(x, y);
        gravityWell.addComponent("transform", transform);

        RenderComponent render = new RenderComponent();
        render.setColor(new Color(0.6f, 0.2f, 0.8f, 0.6f));
        render.setSize(Constants.HAZARD_SIZE);
        gravityWell.addComponent("render", render);

        CollisionComponent collision = new CollisionComponent(
            PULL_RANGE,
            CollisionComponent.MASK_HAZARD,
            CollisionComponent.MASK_PLAYER
        );
        gravityWell.addComponent("collision", collision);

        LifetimeComponent lifetime = new LifetimeComponent(Constants.HAZARD_LIFETIME);
        gravityWell.addComponent("lifetime", lifetime);
    }

    @Override
    protected void updateHazards(float delta, EntityManager entityManager, GameState gameState) {
        Entity player = entityManager.getPlayerEntity();
        if (player == null || !player.isActive()) return;

        TransformComponent playerTransform = player.getTransformComponent();
        VelocityComponent playerVelocity = player.getVelocityComponent();

        List<Entity> gravityWells = entityManager.getEntitiesWithTag("hazard_gravity");

        for (Entity well : gravityWells) {
            if (!well.isActive()) continue;

            TransformComponent wellTransform = well.getTransformComponent();
            if (wellTransform == null || playerTransform == null || playerVelocity == null) continue;

            float dx = wellTransform.getX() - playerTransform.getX();
            float dy = wellTransform.getY() - playerTransform.getY();
            float dist = (float) Math.sqrt(dx * dx + dy * dy);

            if (dist < PULL_RANGE && dist > 0) {
                // Pull strength increases as player gets closer
                float pullFactor = 1f - (dist / PULL_RANGE);
                float pullX = (dx / dist) * PULL_STRENGTH * pullFactor * delta;
                float pullY = (dy / dist) * PULL_STRENGTH * pullFactor * delta;

                playerVelocity.setVx(playerVelocity.getVx() + pullX);
                playerVelocity.setVy(playerVelocity.getVy() + pullY);
            }
        }
    }
}
