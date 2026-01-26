package ape.flatbonk.util;

import com.badlogic.gdx.graphics.Color;

public enum PlayerColor {
    RED("Red", "Fireballs", new Color(0.9f, 0.2f, 0.2f, 1f)),
    GREEN("Green", "Poison Pools", new Color(0.2f, 0.9f, 0.2f, 1f)),
    BLUE("Blue", "Ice Patches", new Color(0.2f, 0.4f, 0.9f, 1f)),
    PURPLE("Purple", "Gravity Wells", new Color(0.7f, 0.2f, 0.9f, 1f));

    private final String displayName;
    private final String hazardName;
    private final Color color;

    PlayerColor(String displayName, String hazardName, Color color) {
        this.displayName = displayName;
        this.hazardName = hazardName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getHazardName() {
        return hazardName;
    }

    public Color getColor() {
        return color;
    }
}
