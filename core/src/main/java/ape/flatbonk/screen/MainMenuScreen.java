package ape.flatbonk.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ape.flatbonk.Main;
import ape.flatbonk.util.Constants;

public class MainMenuScreen extends AbstractGameScreen {
    private final GlyphLayout titleLayout;

    public MainMenuScreen(Main game) {
        super(game);

        titleLayout = new GlyphLayout();
        font.getData().setScale(3f);
        titleLayout.setText(font, "FLATBONK");
        font.getData().setScale(1.5f);

        createButtons();
    }

    private void createButtons() {
        TextButtonStyle style = new TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;
        style.overFontColor = Color.YELLOW;
        style.downFontColor = Color.GRAY;

        TextButton playButton = new TextButton("PLAY", style);
        playButton.setSize(Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT);
        playButton.setPosition(
            (Constants.WORLD_WIDTH - Constants.BUTTON_WIDTH) / 2,
            Constants.WORLD_HEIGHT / 2
        );
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new ShapeSelectionScreen(game));
            }
        });

        TextButton infoButton = new TextButton("INFO", style);
        infoButton.setSize(Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT);
        infoButton.setPosition(
            (Constants.WORLD_WIDTH - Constants.BUTTON_WIDTH) / 2,
            Constants.WORLD_HEIGHT / 2 - Constants.BUTTON_HEIGHT - Constants.BUTTON_PADDING
        );
        infoButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new InformationScreen(game));
            }
        });

        stage.addActor(playButton);
        stage.addActor(infoButton);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        font.getData().setScale(3f);
        font.setColor(Color.CYAN);
        font.draw(batch, "FLATBONK",
            (Constants.WORLD_WIDTH - titleLayout.width) / 2,
            Constants.WORLD_HEIGHT - 80);
        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        batch.end();

        // Draw button backgrounds
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.3f, 0.5f, 1f);

        // Play button background
        shapeRenderer.rect(
            (Constants.WORLD_WIDTH - Constants.BUTTON_WIDTH) / 2,
            Constants.WORLD_HEIGHT / 2,
            Constants.BUTTON_WIDTH,
            Constants.BUTTON_HEIGHT
        );

        // Info button background
        shapeRenderer.rect(
            (Constants.WORLD_WIDTH - Constants.BUTTON_WIDTH) / 2,
            Constants.WORLD_HEIGHT / 2 - Constants.BUTTON_HEIGHT - Constants.BUTTON_PADDING,
            Constants.BUTTON_WIDTH,
            Constants.BUTTON_HEIGHT
        );
        shapeRenderer.end();

        // Draw button borders
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.rect(
            (Constants.WORLD_WIDTH - Constants.BUTTON_WIDTH) / 2,
            Constants.WORLD_HEIGHT / 2,
            Constants.BUTTON_WIDTH,
            Constants.BUTTON_HEIGHT
        );
        shapeRenderer.rect(
            (Constants.WORLD_WIDTH - Constants.BUTTON_WIDTH) / 2,
            Constants.WORLD_HEIGHT / 2 - Constants.BUTTON_HEIGHT - Constants.BUTTON_PADDING,
            Constants.BUTTON_WIDTH,
            Constants.BUTTON_HEIGHT
        );
        shapeRenderer.end();

        // Redraw stage on top
        stage.draw();
    }
}
