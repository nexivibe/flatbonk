package ape.flatbonk.hazard;

import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.state.GameState;

public abstract class Hazard {
    protected float spawnTimer;
    protected float spawnInterval;

    public Hazard(float spawnInterval) {
        this.spawnInterval = spawnInterval;
        this.spawnTimer = 0;
    }

    public void update(float delta, EntityManager entityManager, GameState gameState) {
        spawnTimer += delta;

        if (spawnTimer >= spawnInterval) {
            spawnTimer -= spawnInterval;
            spawnHazard(entityManager, gameState);
        }

        updateHazards(delta, entityManager, gameState);
    }

    protected abstract void spawnHazard(EntityManager entityManager, GameState gameState);

    protected abstract void updateHazards(float delta, EntityManager entityManager, GameState gameState);
}
