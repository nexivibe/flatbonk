package ape.flatbonk.util;

public final class Constants {
    private Constants() {}

    // World dimensions
    public static final float WORLD_WIDTH = 800f;
    public static final float WORLD_HEIGHT = 480f;
    public static final float GAME_AREA_HEIGHT = 360f;
    public static final float CONTROL_BAR_HEIGHT = 120f;

    // Player settings
    public static final float PLAYER_BASE_SPEED = 200f;
    public static final float PLAYER_SIZE = 20f;
    public static final int PLAYER_BASE_HEALTH = 100;

    // Dash settings
    public static final float DASH_COOLDOWN = 3.0f;
    public static final float DASH_SPEED = 600f;
    public static final float DASH_DURATION = 0.15f;

    // Progression
    public static final int XP_PER_LEVEL = 100;
    public static final int MAX_WEAPONS = 5;

    // Spawning
    public static final float INITIAL_SPAWN_INTERVAL = 3.0f;
    public static final float MIN_SPAWN_INTERVAL = 0.5f;

    // Difficulty scaling
    public static final float DIFFICULTY_INTERVAL = 30f;
    public static final float HEALTH_SCALE_PER_INTERVAL = 0.10f;
    public static final float DAMAGE_SCALE_PER_INTERVAL = 0.05f;

    // UI
    public static final float BUTTON_WIDTH = 200f;
    public static final float BUTTON_HEIGHT = 60f;
    public static final float BUTTON_PADDING = 20f;

    // Joystick
    public static final float JOYSTICK_RADIUS = 50f;
    public static final float JOYSTICK_DEADZONE = 0.1f;

    // Bullets
    public static final float DEFAULT_BULLET_SPEED = 400f;
    public static final float DEFAULT_BULLET_LIFETIME = 2.0f;

    // Pickup
    public static final float XP_MAGNET_RANGE = 100f;
    public static final float PICKUP_SPEED = 300f;
}
