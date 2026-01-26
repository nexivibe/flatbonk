package ape.flatbonk.powerup.impl;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.component.PlayerStatsComponent;
import ape.flatbonk.powerup.Powerup;

public class ArmorPowerup extends Powerup {

    public ArmorPowerup() {
        super("Armor", "+2 damage reduction", 10);
    }

    @Override
    public void apply(Entity player) {
        PlayerStatsComponent stats = player.getPlayerStatsComponent();
        if (stats != null) {
            stats.addArmor(2);
            levelUp();
        }
    }
}
