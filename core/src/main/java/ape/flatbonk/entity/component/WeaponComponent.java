package ape.flatbonk.entity.component;

import com.badlogic.gdx.utils.Array;

import ape.flatbonk.weapon.Weapon;
import ape.flatbonk.util.Constants;

public class WeaponComponent {
    private final Array<Weapon> weapons;
    private float attackCooldownModifier;

    public WeaponComponent() {
        this.weapons = new Array<Weapon>();
        this.attackCooldownModifier = 1f;
    }

    public Array<Weapon> getWeapons() {
        return weapons;
    }

    public boolean addWeapon(Weapon weapon) {
        if (weapons.size < Constants.MAX_WEAPONS) {
            weapons.add(weapon);
            return true;
        }
        return false;
    }

    public void upgradeWeapon(int index) {
        if (index >= 0 && index < weapons.size) {
            weapons.get(index).upgrade();
        }
    }

    public float getAttackCooldownModifier() {
        return attackCooldownModifier;
    }

    public void setAttackCooldownModifier(float attackCooldownModifier) {
        this.attackCooldownModifier = attackCooldownModifier;
    }

    public void update(float delta) {
        for (Weapon weapon : weapons) {
            weapon.update(delta);
        }
    }

    public boolean hasWeaponOfType(Class<? extends Weapon> weaponClass) {
        for (Weapon weapon : weapons) {
            if (weaponClass.isInstance(weapon)) {
                return true;
            }
        }
        return false;
    }

    public Weapon getWeaponOfType(Class<? extends Weapon> weaponClass) {
        for (Weapon weapon : weapons) {
            if (weaponClass.isInstance(weapon)) {
                return weapon;
            }
        }
        return null;
    }
}
