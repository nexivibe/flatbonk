package ape.flatbonk.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ape.flatbonk.Main;
import ape.flatbonk.state.GameState;
import ape.flatbonk.util.Constants;

public class GameOverScreen extends AbstractGameScreen {
    private final GameState finalState;
    private final GlyphLayout titleLayout;

    public GameOverScreen(Main game, GameState finalState) {
        super(game);
        this.finalState = finalState;

        titleLayout = new GlyphLayout();
        font.getData().setScale(3f);
        titleLayout.setText(font, "GAME OVER");
        font.getData().setScale(1.5f);

        createButtons();
    }

    private void createButtons() {
        TextButtonStyle style = new TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;
        style.overFontColor = Color.YELLOW;
        style.downFontColor = Color.GRAY;

        TextButton retryButton = new TextButton("RETRY", style);
        retryButton.setSize(Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT);
        retryButton.setPosition(
            (Constants.WORLD_WIDTH - Constants.BUTTON_WIDTH) / 2,
            120
        );
        retryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });

        TextButton menuButton = new TextButton("MAIN MENU", style);
        menuButton.setSize(Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT);
        menuButton.setPosition(
            (Constants.WORLD_WIDTH - Constants.BUTTON_WIDTH) / 2,
            50
        );
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        stage.addActor(retryButton);
        stage.addActor(menuButton);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        // Draw button backgrounds
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.3f, 0.5f, 1f);
        shapeRenderer.rect(
            (Constants.WORLD_WIDTH - Constants.BUTTON_WIDTH) / 2,
            120,
            Constants.BUTTON_WIDTH,
            Constants.BUTTON_HEIGHT
        );
        shapeRenderer.rect(
            (Constants.WORLD_WIDTH - Constants.BUTTON_WIDTH) / 2,
            50,
            Constants.BUTTON_WIDTH,
            Constants.BUTTON_HEIGHT
        );
        shapeRenderer.end();

        // Draw button borders
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.rect(
            (Constants.WORLD_WIDTH - Constants.BUTTON_WIDTH) / 2,
            120,
            Constants.BUTTON_WIDTH,
            Constants.BUTTON_HEIGHT
        );
        shapeRenderer.rect(
            (Constants.WORLD_WIDTH - Constants.BUTTON_WIDTH) / 2,
            50,
            Constants.BUTTON_WIDTH,
            Constants.BUTTON_HEIGHT
        );
        shapeRenderer.end();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        // Title
        font.getData().setScale(3f);
        font.setColor(Color.RED);
        font.draw(batch, "GAME OVER",
            (Constants.WORLD_WIDTH - titleLayout.width) / 2,
            Constants.WORLD_HEIGHT - 60);

        // Stats
        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);

        float statsY = Constants.WORLD_HEIGHT - 150;
        float statsX = Constants.WORLD_WIDTH / 2 - 100;

        font.draw(batch, "Level: " + finalState.getPlayerLevel(), statsX, statsY);
        font.draw(batch, "Kills: " + finalState.getKillCount(), statsX, statsY - 35);
        font.draw(batch, "Money: " + finalState.getMoney(), statsX, statsY - 70);
        font.draw(batch, "Time: " + formatTime(finalState.getElapsedTime()), statsX, statsY - 105);

        batch.end();

        stage.draw();
    }

    private String formatTime(float seconds) {
        int mins = (int) (seconds / 60);
        int secs = (int) (seconds % 60);
        return String.format("%d:%02d", mins, secs);
    }
}
