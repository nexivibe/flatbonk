package ape.flatbonk.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ape.flatbonk.Main;
import ape.flatbonk.util.Constants;

public class InformationScreen extends AbstractGameScreen {
    private final GlyphLayout titleLayout;
    private final GlyphLayout comingSoonLayout;

    public InformationScreen(Main game) {
        super(game);

        titleLayout = new GlyphLayout();
        font.getData().setScale(2.5f);
        titleLayout.setText(font, "INFORMATION");

        comingSoonLayout = new GlyphLayout();
        font.getData().setScale(2f);
        comingSoonLayout.setText(font, "Coming Soon!");
        font.getData().setScale(1.5f);

        createBackButton();
    }

    private void createBackButton() {
        TextButtonStyle style = new TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;
        style.overFontColor = Color.YELLOW;
        style.downFontColor = Color.GRAY;

        TextButton backButton = new TextButton("BACK", style);
        backButton.setSize(Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT);
        backButton.setPosition(
            (Constants.VIEWPORT_WIDTH - Constants.BUTTON_WIDTH) / 2,
            80
        );
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        stage.addActor(backButton);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        // Draw button background
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.3f, 0.5f, 1f);
        shapeRenderer.rect(
            (Constants.VIEWPORT_WIDTH - Constants.BUTTON_WIDTH) / 2,
            80,
            Constants.BUTTON_WIDTH,
            Constants.BUTTON_HEIGHT
        );
        shapeRenderer.end();

        // Draw button border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.rect(
            (Constants.VIEWPORT_WIDTH - Constants.BUTTON_WIDTH) / 2,
            80,
            Constants.BUTTON_WIDTH,
            Constants.BUTTON_HEIGHT
        );
        shapeRenderer.end();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        // Title
        font.getData().setScale(2.5f);
        font.setColor(Color.CYAN);
        font.draw(batch, "INFORMATION",
            (Constants.VIEWPORT_WIDTH - titleLayout.width) / 2,
            Constants.VIEWPORT_HEIGHT - 60);

        // Coming Soon message
        font.getData().setScale(2f);
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "Coming Soon!",
            (Constants.VIEWPORT_WIDTH - comingSoonLayout.width) / 2,
            Constants.VIEWPORT_HEIGHT / 2 + 30);

        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        batch.end();

        stage.draw();
    }
}
