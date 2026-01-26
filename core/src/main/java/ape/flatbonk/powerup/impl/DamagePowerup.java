package ape.flatbonk.powerup.impl;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.component.PlayerStatsComponent;
import ape.flatbonk.powerup.Powerup;

public class DamagePowerup extends Powerup {

    public DamagePowerup() {
        super("Damage Up", "+10% damage", 10);
    }

    @Override
    public void apply(Entity player) {
        PlayerStatsComponent stats = player.getPlayerStatsComponent();
        if (stats != null) {
            stats.addDamageModifier(0.10f);
            levelUp();
        }
    }
}
