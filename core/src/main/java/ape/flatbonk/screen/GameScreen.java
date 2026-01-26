package ape.flatbonk.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ape.flatbonk.Main;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.component.RenderComponent;
import ape.flatbonk.entity.component.TransformComponent;
import ape.flatbonk.entity.factory.PlayerFactory;
import ape.flatbonk.input.ControlBar;
import ape.flatbonk.render.ShapeDefinition;
import ape.flatbonk.state.GameState;
import ape.flatbonk.state.PlayerConfig;
import ape.flatbonk.system.*;
import ape.flatbonk.ui.GameHUD;
import ape.flatbonk.ui.LevelUpDialog;
import ape.flatbonk.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GameScreen extends AbstractGameScreen {
    private final GameState gameState;
    private final EntityManager entityManager;
    private final ControlBar controlBar;
    private final GameHUD hud;
    private final LevelUpDialog levelUpDialog;

    private final List<GameSystem> systems;
    private boolean paused;
    private boolean showingLevelUp;

    public GameScreen(Main game) {
        super(game);

        PlayerConfig config = game.getPlayerConfig();
        this.gameState = new GameState(config);
        this.entityManager = new EntityManager();
        this.controlBar = new ControlBar(stage, viewport);
        this.hud = new GameHUD(gameState);
        this.levelUpDialog = new LevelUpDialog(stage, gameState, this::onPowerupSelected);

        this.systems = new ArrayList<GameSystem>();
        this.paused = false;
        this.showingLevelUp = false;

        initializeSystems();
        spawnPlayer();
    }

    private void initializeSystems() {
        systems.add(new InputSystem(entityManager, controlBar, gameState));
        systems.add(new MovementSystem(entityManager));
        systems.add(new CollisionSystem(entityManager, gameState));
        systems.add(new WeaponSystem(entityManager, gameState));
        systems.add(new AISystem(entityManager));
        systems.add(new SpawnSystem(entityManager, gameState));
        systems.add(new PickupSystem(entityManager, gameState));
        systems.add(new LevelUpSystem(gameState, this::showLevelUpDialog));
        systems.add(new HazardSystem(entityManager, gameState));
        systems.add(new CleanupSystem(entityManager));

        Collections.sort(systems, new Comparator<GameSystem>() {
            @Override
            public int compare(GameSystem a, GameSystem b) {
                return Integer.compare(a.getPriority(), b.getPriority());
            }
        });
    }

    private void spawnPlayer() {
        PlayerFactory.createPlayer(entityManager, gameState);
    }

    private void showLevelUpDialog() {
        showingLevelUp = true;
        paused = true;
        levelUpDialog.show();
    }

    private void onPowerupSelected() {
        showingLevelUp = false;
        paused = false;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!paused) {
            gameState.update(delta);
            for (GameSystem system : systems) {
                system.update(delta);
            }
        }

        // Draw game area boundary
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.2f, 0.3f, 0.4f, 1f);
        shapeRenderer.rect(0, Constants.CONTROL_BAR_HEIGHT,
            Constants.WORLD_WIDTH, Constants.GAME_AREA_HEIGHT);
        shapeRenderer.end();

        // Render entities
        renderEntities();

        // Render HUD
        hud.render(batch, shapeRenderer, font, viewport);

        // Render control bar
        controlBar.render(shapeRenderer, viewport);

        // Render stage (includes level up dialog if showing)
        stage.act(delta);
        stage.draw();

        // Check for game over
        if (gameState.isGameOver()) {
            game.setScreen(new GameOverScreen(game, gameState));
        }
    }

    private void renderEntities() {
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        entityManager.forEachEntity(entity -> {
            if (entity.hasComponent("render") && entity.hasComponent("transform")) {
                TransformComponent transform = entity.getTransformComponent();
                RenderComponent render = entity.getRenderComponent();

                if (render.getShapeType() != null) {
                    ShapeDefinition.drawShape(shapeRenderer,
                        render.getShapeType(),
                        transform.getX(),
                        transform.getY(),
                        transform.getScale() * render.getSize(),
                        render.getColor());
                } else {
                    // Draw as circle for pickups/bullets
                    shapeRenderer.setColor(render.getColor());
                    shapeRenderer.circle(transform.getX(), transform.getY(),
                        render.getSize() * transform.getScale());
                }
            }
        });

        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        entityManager.dispose();
    }
}
