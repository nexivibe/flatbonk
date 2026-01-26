package ape.flatbonk.weapon;

import ape.flatbonk.util.ShapeType;
import ape.flatbonk.weapon.impl.*;

public class WeaponRegistry {

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
