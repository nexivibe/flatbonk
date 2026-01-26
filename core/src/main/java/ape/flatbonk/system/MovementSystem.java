package ape.flatbonk.system;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.component.*;
import ape.flatbonk.util.Constants;

import java.util.List;

public class MovementSystem implements GameSystem {
    private final EntityManager entityManager;

    public MovementSystem(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void update(float delta) {
        List<Entity> entities = entityManager.getEntitiesWithComponents("transform", "velocity");

        for (Entity entity : entities) {
            TransformComponent transform = entity.getTransformComponent();
            VelocityComponent velocity = entity.getVelocityComponent();

            transform.translate(velocity.getVx() * delta, velocity.getVy() * delta);

            // Update lifetime for bullets
            if (entity.hasComponent("lifetime")) {
                LifetimeComponent lifetime = entity.getLifetimeComponent();
                lifetime.update(delta);
            }
        }
    }

    @Override
    public int getPriority() {
        return 10;
    }
}
