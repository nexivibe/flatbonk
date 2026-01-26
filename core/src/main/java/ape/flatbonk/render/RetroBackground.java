package ape.flatbonk.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

import ape.flatbonk.util.Constants;

public class RetroBackground {
    private static final int TEXTURE_SIZE = 256;
    private static final float NOISE_SCALE = 0.03f;

    private final Texture noiseTexture;
    private float offsetX;
    private float offsetY;

    public RetroBackground() {
        this.noiseTexture = generateNoiseTexture();
        this.noiseTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        this.offsetX = 0;
        this.offsetY = 0;
    }

    private Texture generateNoiseTexture() {
        Pixmap pixmap = new Pixmap(TEXTURE_SIZE, TEXTURE_SIZE, Pixmap.Format.RGBA8888);

        for (int y = 0; y < TEXTURE_SIZE; y++) {
            for (int x = 0; x < TEXTURE_SIZE; x++) {
                float noiseValue = PerlinNoise.octaveNoise(x * NOISE_SCALE, y * NOISE_SCALE, 4, 0.5f);

                // Create subtle gray variations (0.08 to 0.15 range for dark background)
                float gray = 0.08f + noiseValue * 0.07f;

                // Add occasional grid lines for retro feel
                boolean gridLine = (x % 32 == 0 || y % 32 == 0);
                if (gridLine) {
                    gray += 0.03f;
                }

                // Add scanline effect
                if (y % 4 == 0) {
                    gray *= 0.95f;
                }

                pixmap.setColor(gray, gray, gray * 1.1f, 1f);
                pixmap.drawPixel(x, y);
            }
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public void update(float playerX, float playerY, float delta) {
        // Scroll background based on player position for parallax effect
        offsetX = playerX * 0.1f;
        offsetY = playerY * 0.1f;
    }

    public void render(SpriteBatch batch, Viewport viewport) {
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        // Calculate UV offset for scrolling
        float u = offsetX / TEXTURE_SIZE;
        float v = offsetY / TEXTURE_SIZE;

        // Draw tiled background covering the game area
        float tileWidth = TEXTURE_SIZE * 2;
        float tileHeight = TEXTURE_SIZE * 2;

        for (float x = -tileWidth; x < Constants.WORLD_WIDTH + tileWidth; x += tileWidth) {
            for (float y = Constants.CONTROL_BAR_HEIGHT - tileHeight; y < Constants.WORLD_HEIGHT + tileHeight; y += tileHeight) {
                batch.draw(noiseTexture,
                    x - (offsetX % tileWidth), y - (offsetY % tileHeight),
                    tileWidth, tileHeight);
            }
        }

        batch.end();
    }

    public void dispose() {
        noiseTexture.dispose();
    }
}
