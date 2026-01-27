package ape.flatbonk.weapon;

import com.badlogic.gdx.math.MathUtils;

import ape.flatbonk.util.ShapeType;
import ape.flatbonk.weapon.impl.*;

public class WeaponRegistry {

    private static final Class<?>[] ALL_WEAPONS = {
        SpreadShotWeapon.class,
        PiercingLaserWeapon.class,
        ShotgunWeapon.class,
        HomingMissileWeapon.class,
        BoomerangWeapon.class,
        NovaBurstWeapon.class,
        CrystalShardsWeapon.class,
        WaveBeamWeapon.class,
        RailgunWeapon.class,
        CrossPatternWeapon.class,
        RapidFireWeapon.class,
        LifeDrainWeapon.class,
        ArcSlashWeapon.class,
        ShieldBashWeapon.class,
        TurretWeapon.class
    };

    public static Weapon createRandomWeapon() {
        int index = MathUtils.random(ALL_WEAPONS.length - 1);
        try {
            return (Weapon) ALL_WEAPONS[index].getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return new SpreadShotWeapon();
        }
    }

    public static Weapon createWeaponForShape(ShapeType shape) {
        switch (shape) {
            case CIRCLE:
                return new SpreadShotWeapon();
            case TRIANGLE:
                return new PiercingLaserWeapon();
            case SQUARE:
                return new ShotgunWeapon();
            case PENTAGON:
                return new HomingMissileWeapon();
            case HEXAGON:
                return new BoomerangWeapon();
            case STAR:
                return new NovaBurstWeapon();
            case DIAMOND:
                return new CrystalShardsWeapon();
            case OVAL:
                return new WaveBeamWeapon();
            case RECTANGLE:
                return new RailgunWeapon();
            case CROSS:
                return new CrossPatternWeapon();
            case ARROW:
                return new RapidFireWeapon();
            case HEART:
                return new LifeDrainWeapon();
            case CRESCENT:
                return new ArcSlashWeapon();
            case SEMICIRCLE:
                return new ShieldBashWeapon();
            case OCTAGON:
                return new TurretWeapon();
            default:
                return new SpreadShotWeapon();
        }
    }
}
