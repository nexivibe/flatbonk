package ape.flatbonk.state;

import ape.flatbonk.util.Constants;
import ape.flatbonk.util.PlayerColor;
import ape.flatbonk.util.ShapeType;

public class GameState {
    private final PlayerConfig playerConfig;

    private int playerLevel;
    private int currentXP;
    private int money;
    private int killCount;
    private int score;
    private int totalDamageDealt;
    private float elapsedTime;
    private boolean gameOver;

    private float difficultyMultiplier;
    private float healthMultiplier;
    private float damageMultiplier;
    private float spawnInterval;

    public GameState(PlayerConfig playerConfig) {
        this.playerConfig = playerConfig;
        reset();
    }

    public void reset() {
        this.playerLevel = 1;
        this.currentXP = 0;
        this.money = 0;
        this.killCount = 0;
        this.score = 0;
        this.totalDamageDealt = 0;
        this.elapsedTime = 0;
        this.gameOver = false;

        this.difficultyMultiplier = 1f;
        this.healthMultiplier = 1f;
        this.damageMultiplier = 1f;
        this.spawnInterval = Constants.INITIAL_SPAWN_INTERVAL;
    }

    public void update(float delta) {
        elapsedTime += delta;
        updateDifficulty();
    }

    private void updateDifficulty() {
        int intervals = (int) (elapsedTime / Constants.DIFFICULTY_INTERVAL);
        healthMultiplier = 1f + intervals * Constants.HEALTH_SCALE_PER_INTERVAL;
        damageMultiplier = 1f + intervals * Constants.DAMAGE_SCALE_PER_INTERVAL;

        // Faster spawn rate reduction for more action
        float spawnReduction = intervals * 0.12f;
        spawnInterval = Math.max(Constants.MIN_SPAWN_INTERVAL,
            Constants.INITIAL_SPAWN_INTERVAL - spawnReduction);
    }

    public void addXP(int xp) {
        currentXP += xp;
    }

    public boolean shouldLevelUp() {
        return currentXP >= Constants.XP_PER_LEVEL;
    }

    public void levelUp() {
        currentXP -= Constants.XP_PER_LEVEL;
        playerLevel++;
    }

    public void addMoney(int amount) {
        money += amount;
    }

    public void addKill() {
        killCount++;
        score += 100; // Bonus for killing
    }

    public void addDamageDealt(int damage) {
        totalDamageDealt += damage;
        score += damage;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    // Getters
    public PlayerConfig getPlayerConfig() {
        return playerConfig;
    }

    public ShapeType getPlayerShape() {
        return playerConfig.getSelectedShape();
    }

    public PlayerColor getPlayerColor() {
        return playerConfig.getSelectedColor();
    }

    public int getPlayerLevel() {
        return playerLevel;
    }

    public int getCurrentXP() {
        return currentXP;
    }

    public int getMoney() {
        return money;
    }

    public int getKillCount() {
        return killCount;
    }

    public int getScore() {
        return score;
    }

    public int getTotalDamageDealt() {
        return totalDamageDealt;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public float getHealthMultiplier() {
        return healthMultiplier;
    }

    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    public float getSpawnInterval() {
        return spawnInterval;
    }

    public float getXPProgress() {
        return (float) currentXP / Constants.XP_PER_LEVEL;
    }
}
