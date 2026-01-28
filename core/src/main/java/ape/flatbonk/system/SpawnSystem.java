package ape.flatbonk.system;

import com.badlogic.gdx.math.MathUtils;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.component.TransformComponent;
import ape.flatbonk.entity.factory.MonsterFactory;
import ape.flatbonk.entity.factory.PickupFactory;
import ape.flatbonk.state.GameState;
import ape.flatbonk.util.Constants;

public class SpawnSystem implements GameSystem {
    private final EntityManager entityManager;
    private final GameState gameState;
    private float spawnTimer;
    private float waveTimer;
    private float diamondTimer;
    private int lastBossLevel;

    private static final float WAVE_INTERVAL = 8f;
    private static final float DIAMOND_INTERVAL = 12f;  // Spawn diamond every 12 seconds
    private static final int MAX_ACTIVE_MONSTERS = 40;  // Reduced for better performance and pacing
    private static final int MAX_DIAMONDS = 3;

    public SpawnSystem(EntityManager entityManager, GameState gameState) {
        this.entityManager = entityManager;
        this.gameState = gameState;
        this.spawnTimer = 0;
        this.waveTimer = 0;
        this.diamondTimer = 5f;  // First diamond spawns after 7 seconds
        this.lastBossLevel = 0;
    }

    @Override
    public void update(float delta) {
        // Check for boss spawn every 3 levels
        int currentLevel = gameState.getPlayerLevel();
        if (currentLevel >= 3 && currentLevel % 3 == 0 && currentLevel > lastBossLevel) {
            lastBossLevel = currentLevel;
            MonsterFactory.createBossMonster(entityManager, gameState);
        }

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

            // Spawn count increases with time
            int count = 1;
            float elapsed = gameState.getElapsedTime();
            if (elapsed > 30f) count += 1;
            if (elapsed > 60f) count += 1;
            if (elapsed > 90f) count += 1;

            for (int i = 0; i < count && activeMonsters + i < MAX_ACTIVE_MONSTERS; i++) {
                MonsterFactory.createMonster(entityManager, gameState);
            }
        }

        // Wave spawning - periodic burst of enemies for excitement
        if (waveTimer >= WAVE_INTERVAL) {
            waveTimer = 0;

            int waveSize = 3 + (int)(gameState.getElapsedTime() / 30f);
            waveSize = Math.min(waveSize, 8);  // Smaller waves

            for (int i = 0; i < waveSize && activeMonsters + i < MAX_ACTIVE_MONSTERS; i++) {
                MonsterFactory.createMonster(entityManager, gameState);
            }
        }

        // Diamond spawning
        diamondTimer += delta;
        if (diamondTimer >= DIAMOND_INTERVAL) {
            diamondTimer = 0;

            // Only spawn if not too many diamonds
            int activeDiamonds = entityManager.getEntitiesWithTag("diamond").size();
            if (activeDiamonds < MAX_DIAMONDS) {
                // Spawn diamond near player but not too close
                Entity player = entityManager.getPlayerEntity();
                float px = Constants.WORLD_WIDTH / 2;
                float py = Constants.WORLD_HEIGHT / 2;
                if (player != null) {
                    TransformComponent pt = player.getTransformComponent();
                    if (pt != null) {
                        px = pt.getX();
                        py = pt.getY();
                    }
                }

                // Random position within visible range of player
                float angle = MathUtils.random(360f);
                float dist = MathUtils.random(150f, 300f);
                float x = px + MathUtils.cosDeg(angle) * dist;
                float y = py + MathUtils.sinDeg(angle) * dist;

                // Clamp to world bounds
                x = MathUtils.clamp(x, 50f, Constants.WORLD_WIDTH - 50f);
                y = MathUtils.clamp(y, Constants.CONTROL_BAR_HEIGHT + 50f, Constants.WORLD_HEIGHT - 50f);

                PickupFactory.createDiamond(entityManager, x, y);
            }
        }
    }

    @Override
    public int getPriority() {
        return 30;
    }
}
