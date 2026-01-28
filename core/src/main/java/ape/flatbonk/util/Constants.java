package ape.flatbonk.util;

public final class Constants {
    private Constants() {}

    // Viewport dimensions (portrait mode for mobile)
    public static final float VIEWPORT_WIDTH = 480f;
    public static final float VIEWPORT_HEIGHT = 800f;

    // World dimensions (the actual play area - much larger)
    public static final float WORLD_WIDTH = 2400f;
    public static final float WORLD_HEIGHT = 2400f;

    // UI dimensions
    public static final float CONTROL_BAR_HEIGHT = 100f;
    public static final float GAME_AREA_HEIGHT = VIEWPORT_HEIGHT - CONTROL_BAR_HEIGHT;

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

    // Spawning - fast action pacing for mobile
    public static final float INITIAL_SPAWN_INTERVAL = 0.8f;
    public static final float MIN_SPAWN_INTERVAL = 0.2f;
    public static final float SPAWN_DISTANCE = 350f;

    // Difficulty scaling - aggressive for action feel
    public static final float DIFFICULTY_INTERVAL = 20f;
    public static final float HEALTH_SCALE_PER_INTERVAL = 0.15f;
    public static final float DAMAGE_SCALE_PER_INTERVAL = 0.08f;

    // Knockback
    public static final float KNOCKBACK_FORCE = 150f;

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

    // Hazards
    public static final float HAZARD_LIFETIME = 4f;
    public static final float HAZARD_SIZE = 30f;
}
