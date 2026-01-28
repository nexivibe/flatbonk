package ape.flatbonk.state;

import com.badlogic.gdx.math.MathUtils;

import ape.flatbonk.util.Constants;
import ape.flatbonk.util.PlayerColor;
import ape.flatbonk.util.ShapeType;

public class GameState {
    private final PlayerConfig playerConfig;
    private long gameSeed;

    private int playerLevel;
    private int currentXP;
    private int xpForNextLevel;
    private int money;
    private int killCount;
    private int score;
    private int totalDamageDealt;
    private float elapsedTime;
    private boolean gameOver;
    private float xpBonusModifier;

    private float difficultyMultiplier;
    private float healthMultiplier;
    private float damageMultiplier;
    private float spawnInterval;

    // Base kills needed: 3 for first, then +1 per level (3, 4, 5, 6...)
    private static final int BASE_KILLS_FOR_LEVEL = 3;
    private static final int XP_PER_KILL = 5;

    public GameState(PlayerConfig playerConfig) {
        this.playerConfig = playerConfig;
        this.gameSeed = System.currentTimeMillis();
        reset();
    }

    public GameState(PlayerConfig playerConfig, long seed) {
        this.playerConfig = playerConfig;
        this.gameSeed = seed;
        reset();
    }

    public void reset() {
        this.playerLevel = 1;
        this.currentXP = 0;
        this.xpForNextLevel = calculateXpForLevel(1);
        this.money = 0;
        this.killCount = 0;
        this.score = 0;
        this.totalDamageDealt = 0;
        this.elapsedTime = 0;
        this.gameOver = false;
        this.xpBonusModifier = 1f;

        this.difficultyMultiplier = 1f;
        this.healthMultiplier = 1f;
        this.damageMultiplier = 1f;
        this.spawnInterval = Constants.INITIAL_SPAWN_INTERVAL;

        // Initialize random with seed
        MathUtils.random.setSeed(gameSeed);
    }

    private int calculateXpForLevel(int level) {
        // Level 1: 3 kills, Level 2: 4 kills, Level 3: 5 kills, etc.
        int killsNeeded = BASE_KILLS_FOR_LEVEL + (level - 1);
        return killsNeeded * XP_PER_KILL;
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
        currentXP += (int)(xp * xpBonusModifier);
    }

    public boolean shouldLevelUp() {
        return currentXP >= xpForNextLevel;
    }

    public void levelUp() {
        currentXP -= xpForNextLevel;
        playerLevel++;
        xpForNextLevel = calculateXpForLevel(playerLevel);
    }

    public void setXpBonusModifier(float modifier) {
        this.xpBonusModifier = modifier;
    }

    public void addXpBonusModifier(float amount) {
        this.xpBonusModifier += amount;
    }

    public long getGameSeed() {
        return gameSeed;
    }

    public int getXpForNextLevel() {
        return xpForNextLevel;
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
        return (float) currentXP / xpForNextLevel;
    }
}
