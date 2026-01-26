package ape.flatbonk.system;

import ape.flatbonk.state.GameState;

public class LevelUpSystem implements GameSystem {
    private final GameState gameState;
    private final Runnable onLevelUp;

    public LevelUpSystem(GameState gameState, Runnable onLevelUp) {
        this.gameState = gameState;
        this.onLevelUp = onLevelUp;
    }

    @Override
    public void update(float delta) {
        while (gameState.shouldLevelUp()) {
            gameState.levelUp();
            if (onLevelUp != null) {
                onLevelUp.run();
            }
        }
    }

    @Override
    public int getPriority() {
        return 35;
    }
}
