package ape.flatbonk.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ape.flatbonk.Main;
import ape.flatbonk.render.ShapeDefinition;
import ape.flatbonk.state.PlayerConfig;
import ape.flatbonk.util.Constants;
import ape.flatbonk.util.PlayerColor;
import ape.flatbonk.util.ShapeType;

public class ConfirmationScreen extends AbstractGameScreen {
    // Smaller preview for portrait mode
    private static final float PREVIEW_SIZE = 100f;

    private final GlyphLayout titleLayout;
    private float rotationAngle = 0f;

    public ConfirmationScreen(Main game) {
        super(game);

        titleLayout = new GlyphLayout();
        font.getData().setScale(2f);
        titleLayout.setText(font, "READY TO PLAY?");
        font.getData().setScale(1.5f);

        createButtons();
    }

    private void createButtons() {
        TextButtonStyle style = new TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;
        style.overFontColor = Color.YELLOW;
        style.downFontColor = Color.GRAY;

        float buttonWidth = Constants.BUTTON_WIDTH * 0.8f;
        float centerX = (Constants.VIEWPORT_WIDTH - buttonWidth) / 2;

        // Back to shape selection
        TextButton shapeButton = new TextButton("< SHAPE", style);
        shapeButton.setSize(buttonWidth * 0.6f, Constants.BUTTON_HEIGHT * 0.8f);
        shapeButton.setPosition(20, 100);
        shapeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new ShapeSelectionScreen(game));
            }
        });

        // Back to color selection
        TextButton colorButton = new TextButton("< COLOR", style);
        colorButton.setSize(buttonWidth * 0.6f, Constants.BUTTON_HEIGHT * 0.8f);
        colorButton.setPosition(Constants.VIEWPORT_WIDTH - buttonWidth * 0.6f - 20, 100);
        colorButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new ColorSelectionScreen(game));
            }
        });

        // Ready button - bottom center
        TextButton readyButton = new TextButton("READY!", style);
        readyButton.setSize(buttonWidth, Constants.BUTTON_HEIGHT);
        readyButton.setPosition(centerX, 170);
        readyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });

        stage.addActor(shapeButton);
        stage.addActor(colorButton);
        stage.addActor(readyButton);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        rotationAngle += delta * 30f;

        PlayerConfig config = game.getPlayerConfig();
        ShapeType shape = config.getSelectedShape();
        PlayerColor color = config.getSelectedColor();

        float buttonWidth = Constants.BUTTON_WIDTH * 0.8f;
        float centerX = (Constants.VIEWPORT_WIDTH - buttonWidth) / 2;

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        // Draw button backgrounds
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Back buttons background
        shapeRenderer.setColor(0.2f, 0.25f, 0.35f, 1f);
        shapeRenderer.rect(20, 100, buttonWidth * 0.6f, Constants.BUTTON_HEIGHT * 0.8f);
        shapeRenderer.rect(Constants.VIEWPORT_WIDTH - buttonWidth * 0.6f - 20, 100, buttonWidth * 0.6f, Constants.BUTTON_HEIGHT * 0.8f);

        // Ready button background (green tint)
        shapeRenderer.setColor(0.2f, 0.4f, 0.2f, 1f);
        shapeRenderer.rect(centerX, 170, buttonWidth, Constants.BUTTON_HEIGHT);

        // Preview area background - upper portion of screen
        float previewX = Constants.VIEWPORT_WIDTH / 2;
        float previewY = Constants.VIEWPORT_HEIGHT - 200;
        shapeRenderer.setColor(0.1f, 0.12f, 0.18f, 1f);
        shapeRenderer.rect(previewX - PREVIEW_SIZE, previewY - PREVIEW_SIZE,
            PREVIEW_SIZE * 2, PREVIEW_SIZE * 2);

        shapeRenderer.end();

        // Draw borders
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.rect(20, 100, buttonWidth * 0.6f, Constants.BUTTON_HEIGHT * 0.8f);
        shapeRenderer.rect(Constants.VIEWPORT_WIDTH - buttonWidth * 0.6f - 20, 100, buttonWidth * 0.6f, Constants.BUTTON_HEIGHT * 0.8f);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(centerX, 170, buttonWidth, Constants.BUTTON_HEIGHT);
        shapeRenderer.setColor(color.getColor());
        shapeRenderer.rect(previewX - PREVIEW_SIZE, previewY - PREVIEW_SIZE,
            PREVIEW_SIZE * 2, PREVIEW_SIZE * 2);
        shapeRenderer.end();

        // Draw the preview shape
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        ShapeDefinition.drawShape(shapeRenderer, shape, previewX, previewY, PREVIEW_SIZE, color.getColor());
        shapeRenderer.end();

        // Draw text
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        // Title
        font.getData().setScale(2f);
        font.setColor(Color.CYAN);
        font.draw(batch, "READY TO PLAY?",
            (Constants.VIEWPORT_WIDTH - titleLayout.width) / 2,
            Constants.VIEWPORT_HEIGHT - 40);

        // Shape and color info - below preview, centered
        font.getData().setScale(1.1f);
        String shapeInfo = "Shape: " + shape.getDisplayName();
        String weaponInfo = "Weapon: " + shape.getWeaponName();
        String colorInfo = "Color: " + color.getDisplayName();
        String hazardInfo = "Hazard: " + color.getHazardName();

        float infoY = previewY - PREVIEW_SIZE - 30;
        float lineHeight = 28f;

        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, shapeInfo, 40, infoY);
        font.draw(batch, weaponInfo, 40, infoY - lineHeight);
        font.setColor(color.getColor());
        font.draw(batch, colorInfo, 40, infoY - lineHeight * 2);
        font.draw(batch, hazardInfo, 40, infoY - lineHeight * 3);

        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        batch.end();

        stage.draw();
    }
}
