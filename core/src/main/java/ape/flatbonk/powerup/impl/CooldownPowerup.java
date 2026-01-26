package ape.flatbonk.powerup.impl;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.component.PlayerStatsComponent;
import ape.flatbonk.powerup.Powerup;

public class CooldownPowerup extends Powerup {

    public CooldownPowerup() {
        super("Attack Speed", "-8% attack cooldown", 10);
    }

    @Override
    public void apply(Entity player) {
        PlayerStatsComponent stats = player.getPlayerStatsComponent();
        if (stats != null) {
            stats.addCooldownModifier(0.08f);
            levelUp();
        }
    }
}
