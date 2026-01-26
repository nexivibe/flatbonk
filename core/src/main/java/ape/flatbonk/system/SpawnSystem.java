package ape.flatbonk.system;

import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.factory.MonsterFactory;
import ape.flatbonk.state.GameState;

public class SpawnSystem implements GameSystem {
    private final EntityManager entityManager;
    private final GameState gameState;
    private float spawnTimer;

    public SpawnSystem(EntityManager entityManager, GameState gameState) {
        this.entityManager = entityManager;
        this.gameState = gameState;
        this.spawnTimer = 0;
    }

    @Override
    public void update(float delta) {
        spawnTimer += delta;

        float spawnInterval = gameState.getSpawnInterval();

        if (spawnTimer >= spawnInterval) {
            spawnTimer -= spawnInterval;

            MonsterFactory.MonsterType type = MonsterFactory.getRandomType(gameState.getElapsedTime());

            // Spawn multiple monsters for swarm type
            int count = (type == MonsterFactory.MonsterType.SWARM) ? 5 : 1;

            for (int i = 0; i < count; i++) {
                MonsterFactory.createMonster(entityManager, gameState, type);
            }

            // Occasionally spawn extra monsters based on elapsed time
            if (gameState.getElapsedTime() > 60f && Math.random() < 0.3) {
                MonsterFactory.createMonster(entityManager, gameState,
                    MonsterFactory.getRandomType(gameState.getElapsedTime()));
            }
        }
    }

    @Override
    public int getPriority() {
        return 30;
    }
}
