package ape.flatbonk.powerup.impl;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.component.PlayerStatsComponent;
import ape.flatbonk.powerup.Powerup;

public class SpeedPowerup extends Powerup {

    public SpeedPowerup() {
        super("Speed Up", "+8% movement speed", 10);
    }

    @Override
    public void apply(Entity player) {
        PlayerStatsComponent stats = player.getPlayerStatsComponent();
        if (stats != null) {
            stats.addSpeedModifier(0.08f);
            levelUp();
        }
    }
}
