package ape.flatbonk.powerup.impl;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.component.HealthComponent;
import ape.flatbonk.powerup.Powerup;

public class HealthPowerup extends Powerup {

    public HealthPowerup() {
        super("Max Health", "+20 max health", 10);
    }

    @Override
    public void apply(Entity player) {
        HealthComponent health = player.getHealthComponent();
        if (health != null) {
            health.setMaxHealth(health.getMaxHealth() + 20);
            health.heal(20);
            levelUp();
        }
    }
}
