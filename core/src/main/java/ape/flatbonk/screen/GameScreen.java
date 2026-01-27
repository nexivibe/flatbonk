package ape.flatbonk.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import ape.flatbonk.Main;
import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.component.HealthComponent;
import ape.flatbonk.entity.component.RenderComponent;
import ape.flatbonk.entity.component.TransformComponent;
import ape.flatbonk.entity.factory.PlayerFactory;
import ape.flatbonk.input.ControlBar;
import ape.flatbonk.render.RetroBackground;
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
    private final RetroBackground background;
    private final Viewport uiViewport;

    private final List<GameSystem> systems;
    private boolean paused;
    private boolean showingLevelUp;

    public GameScreen(Main game) {
        super(game);

        // Create a separate UI viewport that doesn't follow the camera
        this.uiViewport = new FitViewport(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        // Initialize the UI viewport with current screen size
        this.uiViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        PlayerConfig config = game.getPlayerConfig();
        this.gameState = new GameState(config);
        this.entityManager = new EntityManager();
        this.controlBar = new ControlBar(stage, uiViewport);
        this.hud = new GameHUD(gameState);
        this.levelUpDialog = new LevelUpDialog(stage, gameState, entityManager, this::onPowerupSelected);
        this.background = new RetroBackground();

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
        // Dark retro background
        Gdx.gl.glClearColor(0.02f, 0.02f, 0.04f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Process entity updates first so newly created entities are searchable
        entityManager.update();

        if (!paused) {
            gameState.update(delta);
            for (GameSystem system : systems) {
                system.update(delta);
            }
        }

        // Update camera to follow player
        Entity player = entityManager.getPlayerEntity();
        float camX = Constants.WORLD_WIDTH / 2;
        float camY = Constants.WORLD_HEIGHT / 2;

        if (player != null) {
            TransformComponent playerTransform = player.getTransformComponent();
            if (playerTransform != null) {
                // Center camera on player
                camX = playerTransform.getX();
                camY = playerTransform.getY();

                // Update background based on player position
                background.update(playerTransform.getX(), playerTransform.getY(), delta);
            }
        }

        // Clamp camera to world bounds
        float halfViewWidth = Constants.VIEWPORT_WIDTH / 2;
        float halfViewHeight = Constants.VIEWPORT_HEIGHT / 2;

        camX = Math.max(halfViewWidth, Math.min(camX, Constants.WORLD_WIDTH - halfViewWidth));
        camY = Math.max(halfViewHeight, Math.min(camY, Constants.WORLD_HEIGHT - halfViewHeight));

        viewport.getCamera().position.set(camX, camY, 0);
        viewport.getCamera().update();
        viewport.apply();

        background.render(batch, viewport);

        // Draw neon game area boundary (world bounds)
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0f, 1f, 1f, 0.3f); // Cyan neon glow
        shapeRenderer.rect(2, Constants.CONTROL_BAR_HEIGHT + 2,
            Constants.WORLD_WIDTH - 4, Constants.WORLD_HEIGHT - Constants.CONTROL_BAR_HEIGHT - 4);
        shapeRenderer.end();

        // Render entities
        renderEntities();

        // Switch to UI viewport for HUD and controls (fixed on screen)
        uiViewport.apply();

        // Update HUD with player health
        if (player != null) {
            HealthComponent playerHealth = player.getHealthComponent();
            if (playerHealth != null) {
                hud.setPlayerHealthPercent((float) playerHealth.getCurrentHealth() / playerHealth.getMaxHealth());
            }
        }

        // Render HUD with UI viewport (fixed on screen)
        hud.render(batch, shapeRenderer, font, uiViewport);

        // Render control bar with UI viewport (fixed on screen)
        controlBar.render(shapeRenderer, uiViewport);

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
                        render.getColor(),
                        transform.getRotation());
                } else {
                    // Draw as circle for pickups/bullets with glow effect
                    Color color = render.getColor();
                    float size = render.getSize() * transform.getScale();

                    // Outer glow
                    shapeRenderer.setColor(color.r, color.g, color.b, 0.3f);
                    shapeRenderer.circle(transform.getX(), transform.getY(), size * 1.5f);

                    // Core
                    shapeRenderer.setColor(color);
                    shapeRenderer.circle(transform.getX(), transform.getY(), size);
                }
            }
        });

        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        uiViewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        super.dispose();
        entityManager.dispose();
        background.dispose();
    }
}
