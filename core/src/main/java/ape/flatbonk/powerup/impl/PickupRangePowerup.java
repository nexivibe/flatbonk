package ape.flatbonk.powerup.impl;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.component.PlayerStatsComponent;
import ape.flatbonk.powerup.Powerup;

public class PickupRangePowerup extends Powerup {

    public PickupRangePowerup() {
        super("Magnet", "+20% pickup range", 10);
    }

    @Override
    public void apply(Entity player) {
        PlayerStatsComponent stats = player.getPlayerStatsComponent();
        if (stats != null) {
            stats.addPickupRangeModifier(0.20f);
            levelUp();
        }
    }
}
