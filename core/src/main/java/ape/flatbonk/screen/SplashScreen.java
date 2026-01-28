package ape.flatbonk.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import ape.flatbonk.Main;
import ape.flatbonk.util.Constants;

public class SplashScreen extends AbstractGameScreen {
    private Texture logoTexture;
    private float displayTime;
    private boolean textureLoaded;

    private static final float SPLASH_DURATION = 2.0f;
    private static final float BORDER_WIDTH = 2f;
    private static final float MARGIN_WIDTH = 2f;

    public SplashScreen(Main game) {
        super(game);
        this.displayTime = 0;
        this.textureLoaded = false;

        // Try to load the logo
        try {
            logoTexture = new Texture(Gdx.files.internal("logo.png"));
            textureLoaded = true;
        } catch (Exception e) {
            // Logo not found, skip to main menu
            textureLoaded = false;
        }
    }

    @Override
    public void render(float delta) {
        displayTime += delta;

        // Clear to black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!textureLoaded) {
            // No logo, go straight to main menu
            game.setScreen(new MainMenuScreen(game));
            return;
        }

        viewport.apply();

        // Calculate logo dimensions preserving aspect ratio
        float logoWidth = logoTexture.getWidth();
        float logoHeight = logoTexture.getHeight();
        float logoAspect = logoWidth / logoHeight;

        // Maximum size the logo can be (with margins)
        float maxWidth = Constants.VIEWPORT_WIDTH - (BORDER_WIDTH + MARGIN_WIDTH) * 4;
        float maxHeight = Constants.VIEWPORT_HEIGHT - (BORDER_WIDTH + MARGIN_WIDTH) * 4;

        float displayWidth, displayHeight;
        if (maxWidth / maxHeight > logoAspect) {
            // Height limited
            displayHeight = maxHeight;
            displayWidth = displayHeight * logoAspect;
        } else {
            // Width limited
            displayWidth = maxWidth;
            displayHeight = displayWidth / logoAspect;
        }

        // Center position
        float logoX = (Constants.VIEWPORT_WIDTH - displayWidth) / 2;
        float logoY = (Constants.VIEWPORT_HEIGHT - displayHeight) / 2;

        // Draw black margin (already cleared to black, so just draw white border area)
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        // Draw white border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(
            logoX - BORDER_WIDTH - MARGIN_WIDTH,
            logoY - BORDER_WIDTH - MARGIN_WIDTH,
            displayWidth + (BORDER_WIDTH + MARGIN_WIDTH) * 2,
            displayHeight + (BORDER_WIDTH + MARGIN_WIDTH) * 2
        );

        // Draw black inner margin
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(
            logoX - MARGIN_WIDTH,
            logoY - MARGIN_WIDTH,
            displayWidth + MARGIN_WIDTH * 2,
            displayHeight + MARGIN_WIDTH * 2
        );
        shapeRenderer.end();

        // Draw the logo
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(logoTexture, logoX, logoY, displayWidth, displayHeight);
        batch.end();

        // Transition to main menu after splash duration
        if (displayTime >= SPLASH_DURATION) {
            game.setScreen(new MainMenuScreen(game));
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (logoTexture != null) {
            logoTexture.dispose();
        }
    }
}
