package ape.flatbonk.util;

public enum ShapeType {
    CIRCLE("Circle", "Spread Shot"),
    TRIANGLE("Triangle", "Piercing Laser"),
    SQUARE("Square", "Shotgun Blast"),
    PENTAGON("Pentagon", "Homing Missiles"),
    HEXAGON("Hexagon", "Boomerang"),
    STAR("Star", "Nova Burst"),
    DIAMOND("Diamond", "Crystal Shards"),
    OVAL("Oval", "Wave Beam"),
    RECTANGLE("Rectangle", "Railgun"),
    CROSS("Cross", "Cross Pattern"),
    ARROW("Arrow", "Rapid Fire"),
    HEART("Heart", "Life Drain"),
    CRESCENT("Crescent", "Arc Slash"),
    SEMICIRCLE("Semicircle", "Shield Bash"),
    OCTAGON("Octagon", "Turret Deploy");

    private final String displayName;
    private final String weaponName;

    ShapeType(String displayName, String weaponName) {
        this.displayName = displayName;
        this.weaponName = weaponName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getWeaponName() {
        return weaponName;
    }
}
