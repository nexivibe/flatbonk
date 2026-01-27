package ape.flatbonk.powerup.impl;

import com.badlogic.gdx.math.MathUtils;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.component.WeaponComponent;
import ape.flatbonk.powerup.Powerup;
import ape.flatbonk.util.Constants;
import ape.flatbonk.weapon.Weapon;
import ape.flatbonk.weapon.WeaponRegistry;

public class NewWeaponPowerup extends Powerup {
    private Weapon pendingWeapon;

    public NewWeaponPowerup() {
        super("New Weapon", "Add a new weapon", Constants.MAX_WEAPONS - 1);
        generateNewWeapon();
    }

    private void generateNewWeapon() {
        pendingWeapon = WeaponRegistry.createRandomWeapon();
    }

    @Override
    public String getName() {
        if (pendingWeapon != null) {
            return "+" + pendingWeapon.getName();
        }
        return name;
    }

    @Override
    public String getDescription() {
        if (pendingWeapon != null) {
            return pendingWeapon.getDescription();
        }
        return description;
    }

    @Override
    public void apply(Entity player) {
        WeaponComponent weapons = player.getWeaponComponent();
        if (weapons != null && pendingWeapon != null) {
            if (weapons.addWeapon(pendingWeapon)) {
                levelUp();
                // Generate a new weapon for next time
                generateNewWeapon();
            }
        }
    }

    @Override
    public boolean isMaxed() {
        // This powerup is maxed when player has all 5 weapons
        return level >= maxLevel;
    }

    public void refreshWeapon(Entity player) {
        // Generate a weapon that the player doesn't already have
        WeaponComponent weapons = player.getWeaponComponent();
        if (weapons == null) return;

        int attempts = 0;
        while (attempts < 20) {
            Weapon candidate = WeaponRegistry.createRandomWeapon();
            if (!weapons.hasWeaponOfType(candidate.getClass())) {
                pendingWeapon = candidate;
                return;
            }
            attempts++;
        }
        // Fallback to any random weapon
        pendingWeapon = WeaponRegistry.createRandomWeapon();
    }
}
