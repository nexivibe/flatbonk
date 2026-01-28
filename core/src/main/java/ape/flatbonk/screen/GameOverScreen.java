package ape.flatbonk.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ape.flatbonk.Main;
import ape.flatbonk.state.GameState;
import ape.flatbonk.util.Constants;

public class GameOverScreen extends AbstractGameScreen {
    private final GameState finalState;
    private final String trollQuestion;

    // Troll questions - implied insults with no options
    private static final String[] TROLL_QUESTIONS = {
        "Skill issue?",
        "Maybe try using both hands?",
        "Did you forget this isn't a walking simulator?",
        "The monsters send their regards.",
        "Your reflexes called. They quit.",
        "Even the tutorial boss is laughing.",
        "Impressive... impressively bad.",
        "That was almost acceptable. Almost.",
        "The 'git gud' queue starts over there.",
        "Task failed successfully?",
        "Your gaming chair must be defective.",
        "Have you tried turning your skills on?",
        "Participation trophy incoming.",
        "The shapes were rooting against you.",
        "New speedrun category: fastest death?"
    };

    public GameOverScreen(Main game, GameState finalState) {
        super(game);
        this.finalState = finalState;

        // Pick random troll question
        this.trollQuestion = TROLL_QUESTIONS[MathUtils.random(TROLL_QUESTIONS.length - 1)];

        createUI();
    }

    private void createUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().padTop(30);

        // Title
        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = font;
        titleStyle.fontColor = Color.RED;
        font.getData().setScale(2.8f);
        Label titleLabel = new Label("GAME OVER", titleStyle);
        mainTable.add(titleLabel).padBottom(20).row();

        // Troll question (implied insult)
        font.getData().setScale(1.3f);
        Label.LabelStyle trollStyle = new Label.LabelStyle();
        trollStyle.font = font;
        trollStyle.fontColor = Color.CYAN;
        Label trollLabel = new Label(trollQuestion, trollStyle);
        mainTable.add(trollLabel).padBottom(25).row();

        // Score summary
        font.getData().setScale(1.4f);
        Label.LabelStyle headerStyle = new Label.LabelStyle();
        headerStyle.font = font;
        headerStyle.fontColor = Color.YELLOW;

        font.getData().setScale(1.2f);
        Label.LabelStyle statStyle = new Label.LabelStyle();
        statStyle.font = font;
        statStyle.fontColor = Color.WHITE;

        Label.LabelStyle valueStyle = new Label.LabelStyle();
        valueStyle.font = font;
        valueStyle.fontColor = Color.CYAN;

        Table statsTable = new Table();
        statsTable.defaults().pad(2);

        statsTable.add(new Label("SCORE", headerStyle)).colspan(2).padBottom(5).row();
        statsTable.add(new Label(String.valueOf(finalState.getScore()), valueStyle)).colspan(2).padBottom(10).row();

        statsTable.add(new Label("Time: ", statStyle)).right();
        statsTable.add(new Label(formatTime(finalState.getElapsedTime()), valueStyle)).left().row();

        statsTable.add(new Label("Level: ", statStyle)).right();
        statsTable.add(new Label(String.valueOf(finalState.getPlayerLevel()), valueStyle)).left().row();

        statsTable.add(new Label("Kills: ", statStyle)).right();
        statsTable.add(new Label(String.valueOf(finalState.getKillCount()), valueStyle)).left().row();

        statsTable.add(new Label("Damage: ", statStyle)).right();
        statsTable.add(new Label(String.valueOf(finalState.getTotalDamageDealt()), valueStyle)).left().row();

        mainTable.add(statsTable).padBottom(25).row();

        // Action buttons - properly sized
        font.getData().setScale(1.3f);
        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.YELLOW;
        buttonStyle.downFontColor = Color.GRAY;

        Table buttonTable = new Table();

        TextButton retryButton = new TextButton("RETRY", buttonStyle);
        retryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game, finalState.getGameSeed()));
            }
        });

        TextButton newGameButton = new TextButton("NEW GAME", buttonStyle);
        newGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });

        TextButton menuButton = new TextButton("MENU", buttonStyle);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        buttonTable.add(retryButton).width(160).height(50).padRight(10);
        buttonTable.add(newGameButton).width(160).height(50).row();
        buttonTable.add(menuButton).width(160).height(50).colspan(2).padTop(10);

        mainTable.add(buttonTable);

        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        // Stats box background - sized to fit content
        float boxWidth = 150;
        float boxHeight = 110;
        float boxX = (Constants.VIEWPORT_WIDTH - boxWidth) / 2;
        float boxY = Constants.VIEWPORT_HEIGHT - 260;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.1f, 0.1f, 0.15f, 0.9f);
        shapeRenderer.rect(boxX, boxY, boxWidth, boxHeight);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.rect(boxX, boxY, boxWidth, boxHeight);
        shapeRenderer.end();

        // Button backgrounds
        float btnWidth = 160;
        float btnHeight = 50;
        float btnY1 = 95;
        float btnY2 = 35;
        float btnX1 = Constants.VIEWPORT_WIDTH / 2 - btnWidth - 5;
        float btnX2 = Constants.VIEWPORT_WIDTH / 2 + 5;
        float btnXCenter = (Constants.VIEWPORT_WIDTH - btnWidth) / 2;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.3f, 0.5f, 1f);
        shapeRenderer.rect(btnX1, btnY1, btnWidth, btnHeight);
        shapeRenderer.rect(btnX2, btnY1, btnWidth, btnHeight);
        shapeRenderer.rect(btnXCenter, btnY2, btnWidth, btnHeight);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.rect(btnX1, btnY1, btnWidth, btnHeight);
        shapeRenderer.rect(btnX2, btnY1, btnWidth, btnHeight);
        shapeRenderer.rect(btnXCenter, btnY2, btnWidth, btnHeight);
        shapeRenderer.end();

        stage.draw();
    }

    private String formatTime(float seconds) {
        int mins = (int) (seconds / 60);
        int secs = (int) (seconds % 60);
        return String.format("%d:%02d", mins, secs);
    }
}
