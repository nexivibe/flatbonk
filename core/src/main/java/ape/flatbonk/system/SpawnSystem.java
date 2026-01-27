package ape.flatbonk.system;

import com.badlogic.gdx.math.MathUtils;

import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.factory.MonsterFactory;
import ape.flatbonk.state.GameState;

public class SpawnSystem implements GameSystem {
    private final EntityManager entityManager;
    private final GameState gameState;
    private float spawnTimer;
    private float waveTimer;

    private static final float WAVE_INTERVAL = 8f;
    private static final int MAX_ACTIVE_MONSTERS = 50;

    public SpawnSystem(EntityManager entityManager, GameState gameState) {
        this.entityManager = entityManager;
        this.gameState = gameState;
        this.spawnTimer = 0;
        this.waveTimer = 0;
    }

    @Override
    public void update(float delta) {
        // Don't spawn if too many monsters already
        int activeMonsters = entityManager.getEntitiesWithTag("monster").size();
        if (activeMonsters >= MAX_ACTIVE_MONSTERS) {
            return;
        }

        spawnTimer += delta;
        waveTimer += delta;

        float spawnInterval = gameState.getSpawnInterval();

        // Regular spawning
        if (spawnTimer >= spawnInterval) {
            spawnTimer -= spawnInterval;

            MonsterFactory.MonsterType type = MonsterFactory.getRandomType(gameState.getElapsedTime());

            // Spawn multiple monsters for swarm type
            int count = (type == MonsterFactory.MonsterType.SWARM) ? 6 : 1;

            // Spawn more as time goes on for action feel
            float elapsed = gameState.getElapsedTime();
            if (elapsed > 30f) count += 1;
            if (elapsed > 60f) count += 1;
            if (elapsed > 90f) count += 1;

            for (int i = 0; i < count && activeMonsters + i < MAX_ACTIVE_MONSTERS; i++) {
                MonsterFactory.createMonster(entityManager, gameState, type);
            }
        }

        // Wave spawning - periodic burst of enemies for excitement
        if (waveTimer >= WAVE_INTERVAL) {
            waveTimer = 0;

            int waveSize = 3 + (int)(gameState.getElapsedTime() / 30f);
            waveSize = Math.min(waveSize, 10);

            for (int i = 0; i < waveSize && activeMonsters + i < MAX_ACTIVE_MONSTERS; i++) {
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
