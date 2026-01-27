package ape.flatbonk.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import ape.flatbonk.Main;
import ape.flatbonk.render.ShapeDefinition;
import ape.flatbonk.util.Constants;
import ape.flatbonk.util.PlayerColor;
import ape.flatbonk.util.ShapeType;

public class ColorSelectionScreen extends AbstractGameScreen {
    private static final float BUTTON_SIZE = 120f;
    private static final float BUTTON_SPACING = 30f;

    private final Rectangle[] colorBounds;
    private final PlayerColor[] colors;
    private int hoveredIndex = -1;
    private final GlyphLayout titleLayout;
    private final GlyphLayout hazardLayout;
    private final Vector3 touchPoint = new Vector3();

    public ColorSelectionScreen(Main game) {
        super(game);

        colors = PlayerColor.values();
        colorBounds = new Rectangle[colors.length];

        titleLayout = new GlyphLayout();
        hazardLayout = new GlyphLayout();

        font.getData().setScale(2f);
        titleLayout.setText(font, "SELECT YOUR COLOR");
        font.getData().setScale(1.5f);

        initColorBounds();
    }

    private void initColorBounds() {
        float totalWidth = colors.length * BUTTON_SIZE + (colors.length - 1) * BUTTON_SPACING;
        float startX = (Constants.VIEWPORT_WIDTH - totalWidth) / 2;
        float y = Constants.VIEWPORT_HEIGHT / 2 - BUTTON_SIZE / 2;

        for (int i = 0; i < colors.length; i++) {
            float x = startX + i * (BUTTON_SIZE + BUTTON_SPACING);
            colorBounds[i] = new Rectangle(x, y, BUTTON_SIZE, BUTTON_SIZE);
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        handleInput();

        ShapeType selectedShape = game.getPlayerConfig().getSelectedShape();

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        // Draw color buttons
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < colors.length; i++) {
            Rectangle bounds = colorBounds[i];

            // Background
            if (i == hoveredIndex) {
                shapeRenderer.setColor(0.3f, 0.4f, 0.6f, 1f);
            } else {
                shapeRenderer.setColor(0.15f, 0.2f, 0.3f, 1f);
            }
            shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);

            // Draw shape with this color
            ShapeDefinition.drawShape(shapeRenderer, selectedShape,
                bounds.x + bounds.width / 2,
                bounds.y + bounds.height / 2,
                BUTTON_SIZE * 0.6f,
                colors[i].getColor());
        }
        shapeRenderer.end();

        // Draw borders
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int i = 0; i < colors.length; i++) {
            Rectangle bounds = colorBounds[i];
            if (i == hoveredIndex) {
                shapeRenderer.setColor(Color.WHITE);
            } else {
                shapeRenderer.setColor(colors[i].getColor());
            }
            shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
        shapeRenderer.end();

        // Draw text
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        // Title
        font.getData().setScale(2f);
        font.setColor(Color.CYAN);
        font.draw(batch, "SELECT YOUR COLOR",
            (Constants.VIEWPORT_WIDTH - titleLayout.width) / 2,
            Constants.VIEWPORT_HEIGHT - 60);

        // Color names
        font.getData().setScale(1f);
        for (int i = 0; i < colors.length; i++) {
            Rectangle bounds = colorBounds[i];
            font.setColor(colors[i].getColor());
            GlyphLayout nameLayout = new GlyphLayout(font, colors[i].getDisplayName());
            font.draw(batch, colors[i].getDisplayName(),
                bounds.x + (bounds.width - nameLayout.width) / 2,
                bounds.y - 10);
        }

        // Hazard info when hovering
        if (hoveredIndex >= 0 && hoveredIndex < colors.length) {
            PlayerColor hovered = colors[hoveredIndex];
            font.getData().setScale(1.2f);
            font.setColor(Color.WHITE);
            String info = "Hazard: " + hovered.getHazardName();
            hazardLayout.setText(font, info);
            font.draw(batch, info,
                (Constants.VIEWPORT_WIDTH - hazardLayout.width) / 2,
                100);
        }

        font.getData().setScale(1f);
        font.setColor(Color.GRAY);
        font.draw(batch, "Color determines environmental hazards", 20, 30);

        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isTouched()) {
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(touchPoint);

            hoveredIndex = -1;
            for (int i = 0; i < colorBounds.length; i++) {
                if (colorBounds[i].contains(touchPoint.x, touchPoint.y)) {
                    hoveredIndex = i;
                    break;
                }
            }
        }

        if (Gdx.input.justTouched()) {
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(touchPoint);

            for (int i = 0; i < colorBounds.length; i++) {
                if (colorBounds[i].contains(touchPoint.x, touchPoint.y)) {
                    game.getPlayerConfig().setSelectedColor(colors[i]);
                    game.setScreen(new ConfirmationScreen(game));
                    return;
                }
            }
        }
    }
}
