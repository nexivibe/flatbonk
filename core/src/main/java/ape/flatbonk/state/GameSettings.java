package ape.flatbonk.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Persistent game settings using libGDX Preferences.
 * Works across all platforms (desktop, Android, iOS, web).
 */
public class GameSettings {
    private static final String PREFS_NAME = "flatbonk_settings";
    private static final String KEY_LEFT_HANDED = "left_handed";
    private static final String KEY_SOUND_ENABLED = "sound_enabled";
    private static final String KEY_MUSIC_ENABLED = "music_enabled";
    private static final String KEY_VIBRATION_ENABLED = "vibration_enabled";

    private static GameSettings instance;
    private Preferences prefs;

    private boolean leftHanded;
    private boolean soundEnabled;
    private boolean musicEnabled;
    private boolean vibrationEnabled;

    private GameSettings() {
        load();
    }

    public static GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }

    private void load() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);

        // Default: right-handed (false = right-handed, true = left-handed)
        leftHanded = prefs.getBoolean(KEY_LEFT_HANDED, false);
        soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true);
        musicEnabled = prefs.getBoolean(KEY_MUSIC_ENABLED, true);
        vibrationEnabled = prefs.getBoolean(KEY_VIBRATION_ENABLED, true);
    }

    public void save() {
        prefs.putBoolean(KEY_LEFT_HANDED, leftHanded);
        prefs.putBoolean(KEY_SOUND_ENABLED, soundEnabled);
        prefs.putBoolean(KEY_MUSIC_ENABLED, musicEnabled);
        prefs.putBoolean(KEY_VIBRATION_ENABLED, vibrationEnabled);
        prefs.flush();
    }

    // Handedness - determines control layout
    public boolean isLeftHanded() {
        return leftHanded;
    }

    public void setLeftHanded(boolean leftHanded) {
        this.leftHanded = leftHanded;
        save();
    }

    /**
     * Returns true if the joystick should be on the right side (left-handed mode).
     * Default (right-handed): joystick on left, dash on further left.
     * Left-handed: joystick on right, dash on further right.
     */
    public boolean isJoystickOnRight() {
        return leftHanded;
    }

    // Sound settings
    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
        save();
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public void setMusicEnabled(boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
        save();
    }

    public boolean isVibrationEnabled() {
        return vibrationEnabled;
    }

    public void setVibrationEnabled(boolean vibrationEnabled) {
        this.vibrationEnabled = vibrationEnabled;
        save();
    }
}
