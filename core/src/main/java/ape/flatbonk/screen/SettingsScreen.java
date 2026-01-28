package ape.flatbonk.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ape.flatbonk.Main;
import ape.flatbonk.state.GameSettings;
import ape.flatbonk.util.Constants;

public class SettingsScreen extends AbstractGameScreen {
    private final GlyphLayout titleLayout;
    private TextButton handButton;

    public SettingsScreen(Main game) {
        super(game);

        titleLayout = new GlyphLayout();
        font.getData().setScale(2.5f);
        titleLayout.setText(font, "SETTINGS");
        font.getData().setScale(1.5f);

        createButtons();
    }

    private void createButtons() {
        TextButtonStyle style = new TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;
        style.overFontColor = Color.YELLOW;
        style.downFontColor = Color.GRAY;

        // Handedness toggle button
        handButton = new TextButton(getHandednessText(), style);
        handButton.setSize(Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT);
        handButton.setPosition(
            (Constants.VIEWPORT_WIDTH - Constants.BUTTON_WIDTH) / 2,
            Constants.VIEWPORT_HEIGHT / 2 + 40
        );
        handButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameSettings settings = GameSettings.getInstance();
                settings.setLeftHanded(!settings.isLeftHanded());
                handButton.setText(getHandednessText());
            }
        });

        // Back button
        TextButton backButton = new TextButton("BACK", style);
        backButton.setSize(Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT);
        backButton.setPosition(
            (Constants.VIEWPORT_WIDTH - Constants.BUTTON_WIDTH) / 2,
            Constants.VIEWPORT_HEIGHT / 2 - 60
        );
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        stage.addActor(handButton);
        stage.addActor(backButton);
    }

    private String getHandednessText() {
        return GameSettings.getInstance().isLeftHanded() ? "LEFT HAND" : "RIGHT HAND";
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        font.getData().setScale(2.5f);
        font.setColor(Color.CYAN);
        font.draw(batch, "SETTINGS",
            (Constants.VIEWPORT_WIDTH - titleLayout.width) / 2,
            Constants.VIEWPORT_HEIGHT - 100);

        // Hand preference label
        font.getData().setScale(1.2f);
        font.setColor(Color.WHITE);
        font.draw(batch, "Controls Layout:",
            (Constants.VIEWPORT_WIDTH - 120) / 2,
            Constants.VIEWPORT_HEIGHT / 2 + 120);
        font.getData().setScale(1.5f);
        batch.end();

        // Draw button backgrounds
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.3f, 0.5f, 1f);

        // Handedness button background
        shapeRenderer.rect(
            (Constants.VIEWPORT_WIDTH - Constants.BUTTON_WIDTH) / 2,
            Constants.VIEWPORT_HEIGHT / 2 + 40,
            Constants.BUTTON_WIDTH,
            Constants.BUTTON_HEIGHT
        );

        // Back button background
        shapeRenderer.rect(
            (Constants.VIEWPORT_WIDTH - Constants.BUTTON_WIDTH) / 2,
            Constants.VIEWPORT_HEIGHT / 2 - 60,
            Constants.BUTTON_WIDTH,
            Constants.BUTTON_HEIGHT
        );
        shapeRenderer.end();

        // Draw button borders
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.rect(
            (Constants.VIEWPORT_WIDTH - Constants.BUTTON_WIDTH) / 2,
            Constants.VIEWPORT_HEIGHT / 2 + 40,
            Constants.BUTTON_WIDTH,
            Constants.BUTTON_HEIGHT
        );
        shapeRenderer.rect(
            (Constants.VIEWPORT_WIDTH - Constants.BUTTON_WIDTH) / 2,
            Constants.VIEWPORT_HEIGHT / 2 - 60,
            Constants.BUTTON_WIDTH,
            Constants.BUTTON_HEIGHT
        );
        shapeRenderer.end();

        // Redraw stage on top
        stage.draw();
    }
}
