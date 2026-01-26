package ape.flatbonk.powerup;

import ape.flatbonk.entity.Entity;

public abstract class Powerup {
    protected String name;
    protected String description;
    protected int level;
    protected int maxLevel;

    public Powerup(String name, String description, int maxLevel) {
        this.name = name;
        this.description = description;
        this.level = 0;
        this.maxLevel = maxLevel;
    }

    public abstract void apply(Entity player);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getLevel() {
        return level;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public boolean isMaxed() {
        return level >= maxLevel;
    }

    public void levelUp() {
        if (!isMaxed()) {
            level++;
        }
    }
}
