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
    private static final float PREVIEW_SIZE = 150f;

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

        // Back to shape selection
        TextButton shapeButton = new TextButton("< SHAPE", style);
        shapeButton.setSize(Constants.BUTTON_WIDTH * 0.6f, Constants.BUTTON_HEIGHT);
        shapeButton.setPosition(60, 60);
        shapeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new ShapeSelectionScreen(game));
            }
        });

        // Back to color selection
        TextButton colorButton = new TextButton("< COLOR", style);
        colorButton.setSize(Constants.BUTTON_WIDTH * 0.6f, Constants.BUTTON_HEIGHT);
        colorButton.setPosition(60, 130);
        colorButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new ColorSelectionScreen(game));
            }
        });

        // Ready button
        TextButton readyButton = new TextButton("READY!", style);
        readyButton.setSize(Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT);
        readyButton.setPosition(Constants.VIEWPORT_WIDTH - Constants.BUTTON_WIDTH - 60, 80);
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

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        // Draw button backgrounds
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Back buttons background
        shapeRenderer.setColor(0.2f, 0.25f, 0.35f, 1f);
        shapeRenderer.rect(60, 60, Constants.BUTTON_WIDTH * 0.6f, Constants.BUTTON_HEIGHT);
        shapeRenderer.rect(60, 130, Constants.BUTTON_WIDTH * 0.6f, Constants.BUTTON_HEIGHT);

        // Ready button background (green tint)
        shapeRenderer.setColor(0.2f, 0.4f, 0.2f, 1f);
        shapeRenderer.rect(Constants.VIEWPORT_WIDTH - Constants.BUTTON_WIDTH - 60, 80,
            Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT);

        // Preview area background
        float previewX = Constants.VIEWPORT_WIDTH / 2;
        float previewY = Constants.VIEWPORT_HEIGHT / 2 + 20;
        shapeRenderer.setColor(0.1f, 0.12f, 0.18f, 1f);
        shapeRenderer.rect(previewX - PREVIEW_SIZE, previewY - PREVIEW_SIZE,
            PREVIEW_SIZE * 2, PREVIEW_SIZE * 2);

        shapeRenderer.end();

        // Draw borders
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.rect(60, 60, Constants.BUTTON_WIDTH * 0.6f, Constants.BUTTON_HEIGHT);
        shapeRenderer.rect(60, 130, Constants.BUTTON_WIDTH * 0.6f, Constants.BUTTON_HEIGHT);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(Constants.VIEWPORT_WIDTH - Constants.BUTTON_WIDTH - 60, 80,
            Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT);
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

        // Shape and color info
        font.getData().setScale(1.3f);
        font.setColor(Color.WHITE);
        String shapeInfo = "Shape: " + shape.getDisplayName();
        String weaponInfo = "Weapon: " + shape.getWeaponName();
        String colorInfo = "Color: " + color.getDisplayName();
        String hazardInfo = "Hazard: " + color.getHazardName();

        float infoX = Constants.VIEWPORT_WIDTH / 2 - PREVIEW_SIZE - 180;
        float infoY = Constants.VIEWPORT_HEIGHT / 2 + 80;

        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, shapeInfo, infoX, infoY);
        font.draw(batch, weaponInfo, infoX, infoY - 30);
        font.setColor(color.getColor());
        font.draw(batch, colorInfo, infoX, infoY - 70);
        font.draw(batch, hazardInfo, infoX, infoY - 100);

        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        batch.end();

        stage.draw();
    }
}
