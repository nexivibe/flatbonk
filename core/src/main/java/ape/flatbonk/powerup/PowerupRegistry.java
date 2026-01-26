package ape.flatbonk.powerup;

import com.badlogic.gdx.math.MathUtils;

import ape.flatbonk.powerup.impl.*;

import java.util.ArrayList;
import java.util.List;

public class PowerupRegistry {
    private final List<Powerup> availablePowerups;

    public PowerupRegistry() {
        availablePowerups = new ArrayList<Powerup>();
        availablePowerups.add(new DamagePowerup());
        availablePowerups.add(new SpeedPowerup());
        availablePowerups.add(new CooldownPowerup());
        availablePowerups.add(new HealthPowerup());
        availablePowerups.add(new ArmorPowerup());
        availablePowerups.add(new PickupRangePowerup());
        availablePowerups.add(new RegenPowerup());
    }

    public List<Powerup> getRandomPowerups(int count) {
        List<Powerup> available = new ArrayList<Powerup>();
        for (Powerup p : availablePowerups) {
            if (!p.isMaxed()) {
                available.add(p);
            }
        }

        List<Powerup> selected = new ArrayList<Powerup>();
        while (selected.size() < count && !available.isEmpty()) {
            int index = MathUtils.random(available.size() - 1);
            selected.add(available.remove(index));
        }

        return selected;
    }

    public List<Powerup> getAllPowerups() {
        return new ArrayList<Powerup>(availablePowerups);
    }
}
