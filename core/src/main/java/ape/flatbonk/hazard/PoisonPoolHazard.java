package ape.flatbonk.hazard;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.component.*;
import ape.flatbonk.state.GameState;
import ape.flatbonk.util.Constants;

import java.util.List;

public class PoisonPoolHazard extends Hazard {
    private float poisonTickTimer;
    private static final float POISON_TICK_INTERVAL = 0.5f;

    public PoisonPoolHazard() {
        super(5.0f);
        this.poisonTickTimer = 0;
    }

    @Override
    protected void spawnHazard(EntityManager entityManager, GameState gameState) {
        Entity poisonPool = entityManager.createEntity();
        poisonPool.setTag("hazard_poison");

        float x = MathUtils.random(50f, Constants.WORLD_WIDTH - 50f);
        float y = MathUtils.random(Constants.CONTROL_BAR_HEIGHT + 50f, Constants.WORLD_HEIGHT - 50f);

        TransformComponent transform = new TransformComponent(x, y);
        poisonPool.addComponent("transform", transform);

        RenderComponent render = new RenderComponent();
        render.setColor(new Color(0.2f, 0.8f, 0.2f, 0.6f));
        render.setSize(70f);
        poisonPool.addComponent("render", render);

        CollisionComponent collision = new CollisionComponent(
            55f,
            CollisionComponent.MASK_HAZARD,
            CollisionComponent.MASK_PLAYER
        );
        poisonPool.addComponent("collision", collision);

        LifetimeComponent lifetime = new LifetimeComponent(10f);
        poisonPool.addComponent("lifetime", lifetime);
    }

    @Override
    protected void updateHazards(float delta, EntityManager entityManager, GameState gameState) {
        poisonTickTimer += delta;

        Entity player = entityManager.getPlayerEntity();
        if (player == null || !player.isActive()) return;

        TransformComponent playerTransform = player.getTransformComponent();
        CollisionComponent playerCollision = player.getCollisionComponent();
        HealthComponent playerHealth = player.getHealthComponent();

        List<Entity> poisonPools = entityManager.getEntitiesWithTag("hazard_poison");
        boolean inPoison = false;

        for (Entity poison : poisonPools) {
            if (!poison.isActive()) continue;

            TransformComponent poisonTransform = poison.getTransformComponent();
            CollisionComponent poisonCollision = poison.getCollisionComponent();

            if (poisonTransform != null && poisonCollision != null &&
                playerCollision != null && playerTransform != null) {

                if (playerCollision.overlaps(playerTransform, poisonTransform, poisonCollision)) {
                    inPoison = true;
                    break;
                }
            }
        }

        // Apply poison damage over time
        if (inPoison && playerHealth != null && poisonTickTimer >= POISON_TICK_INTERVAL) {
            poisonTickTimer = 0;
            int damage = (int) (5 * gameState.getDamageMultiplier());
            // Poison ignores invincibility frames
            playerHealth.setCurrentHealth(playerHealth.getCurrentHealth() - damage);

            if (playerHealth.isDead()) {
                gameState.setGameOver(true);
            }
        }
    }
}
