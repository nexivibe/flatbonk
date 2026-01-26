package ape.flatbonk.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;

import ape.flatbonk.state.GameState;
import ape.flatbonk.util.Constants;

public class GameHUD {
    private final GameState gameState;

    private static final float HEALTH_BAR_WIDTH = 200f;
    private static final float HEALTH_BAR_HEIGHT = 15f;
    private static final float XP_BAR_WIDTH = 300f;
    private static final float XP_BAR_HEIGHT = 10f;
    private static final float PADDING = 10f;

    public GameHUD(GameState gameState) {
        this.gameState = gameState;
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, BitmapFont font, Viewport viewport) {
        renderBars(shapeRenderer, viewport);
        renderText(batch, font, viewport);
    }

    private void renderBars(ShapeRenderer shapeRenderer, Viewport viewport) {
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        // Health bar background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1f);
        shapeRenderer.rect(PADDING, Constants.WORLD_HEIGHT - PADDING - HEALTH_BAR_HEIGHT,
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
        shapeRenderer.rect(PADDING, Constants.WORLD_HEIGHT - PADDING - HEALTH_BAR_HEIGHT,
            HEALTH_BAR_WIDTH * healthPercent, HEALTH_BAR_HEIGHT);

        // XP bar background
        float xpBarY = Constants.WORLD_HEIGHT - PADDING - HEALTH_BAR_HEIGHT - 5 - XP_BAR_HEIGHT;
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
        shapeRenderer.rect(PADDING, Constants.WORLD_HEIGHT - PADDING - HEALTH_BAR_HEIGHT,
            HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);
        shapeRenderer.rect(PADDING, xpBarY, XP_BAR_WIDTH, XP_BAR_HEIGHT);
        shapeRenderer.end();
    }

    private void renderText(SpriteBatch batch, BitmapFont font, Viewport viewport) {
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        font.getData().setScale(1f);

        // Level display
        font.setColor(Color.CYAN);
        font.draw(batch, "Lv." + gameState.getPlayerLevel(),
            PADDING + HEALTH_BAR_WIDTH + 10,
            Constants.WORLD_HEIGHT - PADDING - 2);

        // Money display
        font.setColor(Color.YELLOW);
        font.draw(batch, "$" + gameState.getMoney(),
            Constants.WORLD_WIDTH - 80,
            Constants.WORLD_HEIGHT - PADDING - 2);

        // Kill count
        font.setColor(Color.WHITE);
        font.draw(batch, "Kills: " + gameState.getKillCount(),
            Constants.WORLD_WIDTH - 100,
            Constants.WORLD_HEIGHT - PADDING - 20);

        // Time
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, formatTime(gameState.getElapsedTime()),
            Constants.WORLD_WIDTH - 60,
            Constants.WORLD_HEIGHT - PADDING - 38);

        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        batch.end();
    }

    private float getHealthPercent() {
        // This will be updated when we have access to player entity
        // For now return a placeholder
        return 1f;
    }

    private String formatTime(float seconds) {
        int mins = (int) (seconds / 60);
        int secs = (int) (seconds % 60);
        return String.format("%d:%02d", mins, secs);
    }
}
