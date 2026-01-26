package ape.flatbonk.system;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.component.*;
import ape.flatbonk.util.Constants;

import java.util.List;

public class CleanupSystem implements GameSystem {
    private final EntityManager entityManager;

    public CleanupSystem(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void update(float delta) {
        List<Entity> entities = entityManager.getEntitiesWithComponents("transform");

        for (Entity entity : entities) {
            if (!entity.isActive()) continue;

            // Remove expired entities
            if (entity.hasComponent("lifetime")) {
                LifetimeComponent lifetime = entity.getLifetimeComponent();
                if (lifetime.isExpired()) {
                    entityManager.removeEntity(entity);
                    continue;
                }
            }

            // Remove entities that are too far off-screen
            TransformComponent transform = entity.getTransformComponent();
            float margin = 100f;

            if (transform.getX() < -margin ||
                transform.getX() > Constants.WORLD_WIDTH + margin ||
                transform.getY() < -margin ||
                transform.getY() > Constants.WORLD_HEIGHT + margin) {

                // Don't remove player
                if (!entity.getTag().equals("player")) {
                    entityManager.removeEntity(entity);
                }
            }
        }

        entityManager.update();
    }

    @Override
    public int getPriority() {
        return 100;
    }
}
