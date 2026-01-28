package ape.flatbonk.powerup;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.component.HealthComponent;
import ape.flatbonk.entity.component.PlayerStatsComponent;
import ape.flatbonk.entity.component.WeaponComponent;
import ape.flatbonk.util.Constants;
import ape.flatbonk.weapon.Weapon;
import ape.flatbonk.weapon.WeaponRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates context-aware upgrade options based on player's current state.
 */
public class UpgradeGenerator {

    public enum UpgradeType {
        // Weapon upgrades
        NEW_WEAPON("New Weapon", "Add another weapon to your arsenal"),
        WEAPON_DAMAGE("Weapon Power", "+15% weapon damage"),
        WEAPON_SPEED("Fire Rate", "-10% weapon cooldown"),
        EXTRA_PROJECTILES("Multi-Shot", "+1 projectile per weapon"),
        PROJECTILE_SIZE("Big Bullets", "+20% projectile size"),

        // Defense upgrades
        MAX_HEALTH("Vitality", "+25 max health"),
        ARMOR("Armor", "+2 damage reduction"),
        HEALTH_REGEN("Regeneration", "+1 HP per second"),

        // Mobility upgrades
        MOVE_SPEED("Swift", "+12% movement speed"),
        DASH_DISTANCE("Long Dash", "+25% dash distance"),

        // Utility upgrades
        XP_BONUS("Wisdom", "+15% XP gain"),
        PICKUP_RANGE("Magnetism", "+30% pickup range"),

        // Special upgrades
        CRIT_CHANCE("Lucky Shot", "+8% critical hit chance"),
        CRIT_DAMAGE("Critical Power", "+25% critical damage");

        public final String name;
        public final String description;

        UpgradeType(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }

    public static class Upgrade {
        public final UpgradeType type;
        public final String name;
        public final String description;
        public final Weapon newWeapon; // Only for NEW_WEAPON type

        public Upgrade(UpgradeType type) {
            this.type = type;
            this.name = type.name;
            this.description = type.description;
            this.newWeapon = null;
        }

        public Upgrade(UpgradeType type, Weapon weapon) {
            this.type = type;
            this.name = "+" + weapon.getName();
            this.description = weapon.getDescription();
            this.newWeapon = weapon;
        }
    }

    /**
     * Generate a list of upgrade options based on player's current state.
     */
    public static List<Upgrade> generateUpgrades(Entity player, int count) {
        List<Upgrade> available = new ArrayList<Upgrade>();
        List<Upgrade> selected = new ArrayList<Upgrade>();

        WeaponComponent weapons = player != null ? player.getWeaponComponent() : null;
        PlayerStatsComponent stats = player != null ? player.getPlayerStatsComponent() : null;
        HealthComponent health = player != null ? player.getHealthComponent() : null;

        // Always available upgrades
        available.add(new Upgrade(UpgradeType.WEAPON_DAMAGE));
        available.add(new Upgrade(UpgradeType.WEAPON_SPEED));
        available.add(new Upgrade(UpgradeType.MOVE_SPEED));
        available.add(new Upgrade(UpgradeType.MAX_HEALTH));
        available.add(new Upgrade(UpgradeType.XP_BONUS));
        available.add(new Upgrade(UpgradeType.PICKUP_RANGE));
        available.add(new Upgrade(UpgradeType.DASH_DISTANCE));
        available.add(new Upgrade(UpgradeType.ARMOR));
        available.add(new Upgrade(UpgradeType.HEALTH_REGEN));
        available.add(new Upgrade(UpgradeType.EXTRA_PROJECTILES));
        available.add(new Upgrade(UpgradeType.PROJECTILE_SIZE));
        available.add(new Upgrade(UpgradeType.CRIT_CHANCE));
        available.add(new Upgrade(UpgradeType.CRIT_DAMAGE));

        // New weapon option if player has room (< 5 weapons)
        if (weapons != null && weapons.getWeapons().size < Constants.MAX_WEAPONS) {
            // Generate a weapon the player doesn't have
            Weapon newWeapon = generateUniqueWeapon(weapons);
            if (newWeapon != null) {
                available.add(new Upgrade(UpgradeType.NEW_WEAPON, newWeapon));
            }
        }

        // Weight certain upgrades based on context
        List<Upgrade> weighted = new ArrayList<Upgrade>();
        for (Upgrade upgrade : available) {
            int weight = 1;

            // Boost new weapon chance if player has few weapons
            if (upgrade.type == UpgradeType.NEW_WEAPON && weapons != null) {
                weight = 3;
                if (weapons.getWeapons().size <= 2) {
                    weight = 5; // Higher chance early game
                }
            }

            // Boost damage if player has multiple weapons
            if (upgrade.type == UpgradeType.WEAPON_DAMAGE && weapons != null && weapons.getWeapons().size >= 3) {
                weight = 2;
            }

            // Boost defense if player is low health
            if (health != null && health.getCurrentHealth() < health.getMaxHealth() * 0.5f) {
                if (upgrade.type == UpgradeType.MAX_HEALTH ||
                    upgrade.type == UpgradeType.ARMOR ||
                    upgrade.type == UpgradeType.HEALTH_REGEN) {
                    weight = 2;
                }
            }

            for (int i = 0; i < weight; i++) {
                weighted.add(upgrade);
            }
        }

        // Select random upgrades
        while (selected.size() < count && !weighted.isEmpty()) {
            int index = MathUtils.random(weighted.size() - 1);
            Upgrade chosen = weighted.get(index);

            // Avoid duplicates
            boolean alreadySelected = false;
            for (Upgrade s : selected) {
                if (s.type == chosen.type) {
                    alreadySelected = true;
                    break;
                }
            }

            if (!alreadySelected) {
                selected.add(chosen);
            }

            // Remove all instances of this upgrade type from weighted list
            for (int i = weighted.size() - 1; i >= 0; i--) {
                if (weighted.get(i).type == chosen.type) {
                    weighted.remove(i);
                }
            }
        }

        return selected;
    }

    private static Weapon generateUniqueWeapon(WeaponComponent weapons) {
        int attempts = 0;
        while (attempts < 20) {
            Weapon candidate = WeaponRegistry.createRandomWeapon();
            if (!weapons.hasWeaponOfType(candidate.getClass())) {
                return candidate;
            }
            attempts++;
        }
        return null;
    }

    /**
     * Apply an upgrade to the player.
     */
    public static void applyUpgrade(Entity player, Upgrade upgrade) {
        PlayerStatsComponent stats = player.getPlayerStatsComponent();
        HealthComponent health = player.getHealthComponent();
        WeaponComponent weapons = player.getWeaponComponent();

        if (stats == null) return;

        switch (upgrade.type) {
            case NEW_WEAPON:
                if (weapons != null && upgrade.newWeapon != null) {
                    weapons.addWeapon(upgrade.newWeapon);
                }
                break;

            case WEAPON_DAMAGE:
                stats.addDamageModifier(0.15f);
                break;

            case WEAPON_SPEED:
                stats.addCooldownModifier(0.10f);
                break;

            case EXTRA_PROJECTILES:
                stats.addProjectileBonus(1);
                break;

            case PROJECTILE_SIZE:
                stats.addProjectileSizeModifier(0.20f);
                break;

            case MAX_HEALTH:
                if (health != null) {
                    health.setMaxHealth(health.getMaxHealth() + 25);
                    health.heal(25); // Also heal the amount gained
                }
                break;

            case ARMOR:
                stats.addArmor(2);
                break;

            case HEALTH_REGEN:
                stats.addHealthRegenRate(1f);
                break;

            case MOVE_SPEED:
                stats.addSpeedModifier(0.12f);
                break;

            case DASH_DISTANCE:
                stats.addDashDistanceModifier(0.25f);
                break;

            case XP_BONUS:
                stats.addXpBonusModifier(0.15f);
                break;

            case PICKUP_RANGE:
                stats.addPickupRangeModifier(0.30f);
                break;

            case CRIT_CHANCE:
                stats.addCritChance(0.08f);
                break;

            case CRIT_DAMAGE:
                stats.addCritMultiplier(0.25f);
                break;
        }
    }
}
