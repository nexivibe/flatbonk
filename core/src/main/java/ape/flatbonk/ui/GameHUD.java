package ape.flatbonk.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.component.WeaponComponent;
import ape.flatbonk.state.GameState;
import ape.flatbonk.util.Constants;
import ape.flatbonk.weapon.Weapon;

public class GameHUD {
    private final GameState gameState;
    private final EntityManager entityManager;
    private float playerHealthPercent = 1f;

    // Portrait mode adjusted sizes
    private static final float HEALTH_BAR_WIDTH = 160f;
    private static final float HEALTH_BAR_HEIGHT = 12f;
    private static final float XP_BAR_WIDTH = 180f;
    private static final float XP_BAR_HEIGHT = 8f;
    private static final float PADDING = 8f;
    private static final float WEAPON_SLOT_SIZE = 35f;
    private static final float WEAPON_SLOT_GAP = 4f;

    public GameHUD(GameState gameState, EntityManager entityManager) {
        this.gameState = gameState;
        this.entityManager = entityManager;
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, BitmapFont font, Viewport viewport) {
        renderBars(shapeRenderer, viewport);
        renderWeapons(shapeRenderer, batch, font, viewport);
        renderText(batch, font, viewport);
    }

    private void renderBars(ShapeRenderer shapeRenderer, Viewport viewport) {
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        // Health bar background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1f);
        shapeRenderer.rect(PADDING, Constants.VIEWPORT_HEIGHT - PADDING - HEALTH_BAR_HEIGHT,
            HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);

        // Health bar fill
        float healthPercent = Math.max(0, Math.min(1, getHealthPercent()));
        if (healthPercent > 0.5f) {
            shapeRenderer.setColor(0.2f, 0.8f, 0.2f, 1f);
        } else if (healthPercent > 0.25f) {
            shapeRenderer.setColor(0.9f, 0.7f, 0.1f, 1f);
        } else {
            shapeRenderer.setColor(0.9f, 0.2f, 0.2f, 1f);
        }
        shapeRenderer.rect(PADDING, Constants.VIEWPORT_HEIGHT - PADDING - HEALTH_BAR_HEIGHT,
            HEALTH_BAR_WIDTH * healthPercent, HEALTH_BAR_HEIGHT);

        // XP bar background
        float xpBarY = Constants.VIEWPORT_HEIGHT - PADDING - HEALTH_BAR_HEIGHT - 5 - XP_BAR_HEIGHT;
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1f);
        shapeRenderer.rect(PADDING, xpBarY, XP_BAR_WIDTH, XP_BAR_HEIGHT);

        // XP bar fill
        shapeRenderer.setColor(0.3f, 0.6f, 1f, 1f);
        shapeRenderer.rect(PADDING, xpBarY,
            XP_BAR_WIDTH * gameState.getXPProgress(), XP_BAR_HEIGHT);

        shapeRenderer.end();

        // Health bar border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(PADDING, Constants.VIEWPORT_HEIGHT - PADDING - HEALTH_BAR_HEIGHT,
            HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);
        shapeRenderer.rect(PADDING, xpBarY, XP_BAR_WIDTH, XP_BAR_HEIGHT);
        shapeRenderer.end();
    }

    private void renderWeapons(ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font, Viewport viewport) {
        Entity player = entityManager.getPlayerEntity();
        if (player == null) return;

        WeaponComponent weaponComp = player.getWeaponComponent();
        if (weaponComp == null) return;

        // Position in bottom left, above control bar
        float startX = PADDING;
        float startY = Constants.CONTROL_BAR_HEIGHT + PADDING;

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        // Draw weapon slot backgrounds
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < Constants.MAX_WEAPONS; i++) {
            float x = startX + i * (WEAPON_SLOT_SIZE + WEAPON_SLOT_GAP);

            // Slot background
            if (i < weaponComp.getWeapons().size) {
                // Active weapon slot
                shapeRenderer.setColor(0.15f, 0.15f, 0.25f, 0.9f);
            } else {
                // Empty slot
                shapeRenderer.setColor(0.08f, 0.08f, 0.12f, 0.7f);
            }
            shapeRenderer.rect(x, startY, WEAPON_SLOT_SIZE, WEAPON_SLOT_SIZE);
        }
        shapeRenderer.end();

        // Draw borders
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int i = 0; i < Constants.MAX_WEAPONS; i++) {
            float x = startX + i * (WEAPON_SLOT_SIZE + WEAPON_SLOT_GAP);

            if (i < weaponComp.getWeapons().size) {
                shapeRenderer.setColor(Color.CYAN);
            } else {
                shapeRenderer.setColor(0.3f, 0.3f, 0.4f, 1f);
            }
            shapeRenderer.rect(x, startY, WEAPON_SLOT_SIZE, WEAPON_SLOT_SIZE);
        }
        shapeRenderer.end();

        // Draw weapon info text
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        font.getData().setScale(0.8f);

        for (int i = 0; i < weaponComp.getWeapons().size; i++) {
            Weapon weapon = weaponComp.getWeapons().get(i);
            float x = startX + i * (WEAPON_SLOT_SIZE + WEAPON_SLOT_GAP);

            // Weapon level
            font.setColor(Color.YELLOW);
            font.draw(batch, "Lv" + weapon.getLevel(),
                x + 4, startY + WEAPON_SLOT_SIZE - 4);

            // Weapon name (abbreviated)
            font.setColor(Color.WHITE);
            String name = weapon.getName();
            if (name.length() > 6) {
                name = name.substring(0, 6);
            }
            font.draw(batch, name, x + 4, startY + 14);
        }

        font.getData().setScale(1f);
        batch.end();
    }

    private void renderText(SpriteBatch batch, BitmapFont font, Viewport viewport) {
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        font.getData().setScale(0.9f);

        // Level display - top left, after health bar
        font.setColor(Color.CYAN);
        font.draw(batch, "Lv." + gameState.getPlayerLevel(),
            PADDING + HEALTH_BAR_WIDTH + 5,
            Constants.VIEWPORT_HEIGHT - PADDING - 2);

        // Money display - top right
        font.setColor(Color.YELLOW);
        font.draw(batch, "$" + gameState.getMoney(),
            Constants.VIEWPORT_WIDTH - 60,
            Constants.VIEWPORT_HEIGHT - PADDING - 2);

        // Score display - below XP bar
        font.setColor(Color.WHITE);
        float scoreY = Constants.VIEWPORT_HEIGHT - PADDING - HEALTH_BAR_HEIGHT - 5 - XP_BAR_HEIGHT - 18;
        font.draw(batch, "Score: " + gameState.getScore(),
            PADDING,
            scoreY);

        // Kill count - right side
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "Kills: " + gameState.getKillCount(),
            Constants.VIEWPORT_WIDTH - 80,
            Constants.VIEWPORT_HEIGHT - PADDING - 18);

        // Time - right side below kills
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, formatTime(gameState.getElapsedTime()),
            Constants.VIEWPORT_WIDTH - 50,
            Constants.VIEWPORT_HEIGHT - PADDING - 34);

        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        batch.end();
    }

    public void setPlayerHealthPercent(float percent) {
        this.playerHealthPercent = percent;
    }

    private float getHealthPercent() {
        return playerHealthPercent;
    }

    private String formatTime(float seconds) {
        int mins = (int) (seconds / 60);
        int secs = (int) (seconds % 60);
        return String.format("%d:%02d", mins, secs);
    }
}
