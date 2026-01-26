package ape.flatbonk.powerup.impl;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.component.PlayerStatsComponent;
import ape.flatbonk.powerup.Powerup;

public class RegenPowerup extends Powerup {

    public RegenPowerup() {
        super("Regeneration", "+1 HP/sec", 5);
    }

    @Override
    public void apply(Entity player) {
        PlayerStatsComponent stats = player.getPlayerStatsComponent();
        if (stats != null) {
            stats.addHealthRegenRate(1f);
            levelUp();
        }
    }
}
