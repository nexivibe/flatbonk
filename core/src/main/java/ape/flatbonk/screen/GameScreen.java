package ape.flatbonk.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import ape.flatbonk.Main;
import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.component.FloatingTextComponent;
import ape.flatbonk.entity.component.HealthComponent;
import ape.flatbonk.entity.component.RenderComponent;
import ape.flatbonk.entity.component.TransformComponent;
import ape.flatbonk.entity.component.WeaponComponent;
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
    private final Stage uiStage;

    private final List<GameSystem> systems;
    private boolean paused;
    private boolean showingLevelUp;

    public GameScreen(Main game) {
        this(game, System.currentTimeMillis());
    }

    public GameScreen(Main game, long seed) {
        super(game);

        // Create a separate UI viewport that doesn't follow the camera
        this.uiViewport = new FitViewport(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        this.uiViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        // Create a separate stage for UI overlays (level up dialog) that stays centered
        this.uiStage = new Stage(uiViewport);

        PlayerConfig config = game.getPlayerConfig();
        this.gameState = new GameState(config, seed);
        this.entityManager = new EntityManager();
        this.controlBar = new ControlBar(uiViewport);
        this.hud = new GameHUD(gameState, entityManager);
        this.levelUpDialog = new LevelUpDialog(uiStage, gameState, entityManager, this::onPowerupSelected);
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

        // Render game stage (control bar)
        stage.act(delta);
        stage.draw();

        // Render level up dialog if showing (on top of everything)
        if (showingLevelUp) {
            uiViewport.apply();
            levelUpDialog.renderBackground(uiViewport);
            uiStage.act(delta);
            uiStage.draw();
        }

        // Check for game over
        if (gameState.isGameOver()) {
            game.setScreen(new GameOverScreen(game, gameState));
        }
    }

    @Override
    public void show() {
        // Use input multiplexer so all input handlers can receive input
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);    // UI stage first (for level up dialog)
        multiplexer.addProcessor(stage);       // Game stage second
        multiplexer.addProcessor(controlBar);  // Control bar for joystick/dash (touch and mouse)
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void renderEntities() {
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Find closest enemy to player for eye direction
        final Entity player = entityManager.getPlayerEntity();
        final float[] closestEnemyPos = new float[2];
        final boolean[] hasEnemy = {false};

        if (player != null) {
            TransformComponent playerTrans = player.getTransformComponent();
            if (playerTrans != null) {
                float closestDistSq = Float.MAX_VALUE;
                List<Entity> monsters = entityManager.getEntitiesWithTag("monster");
                for (Entity monster : monsters) {
                    TransformComponent mt = monster.getTransformComponent();
                    if (mt != null) {
                        float dx = mt.getX() - playerTrans.getX();
                        float dy = mt.getY() - playerTrans.getY();
                        float distSq = dx * dx + dy * dy;
                        if (distSq < closestDistSq) {
                            closestDistSq = distSq;
                            closestEnemyPos[0] = mt.getX();
                            closestEnemyPos[1] = mt.getY();
                            hasEnemy[0] = true;
                        }
                    }
                }
            }
        }

        entityManager.forEachEntity(entity -> {
            if (entity.hasComponent("render") && entity.hasComponent("transform")) {
                TransformComponent transform = entity.getTransformComponent();
                RenderComponent render = entity.getRenderComponent();

                // Check if this is an irregular polygon monster
                if (render.getPolygonSides() > 0) {
                    // Draw irregular polygon for monster
                    float size = render.getSize() * transform.getScale();

                    // Boss glow effect
                    if (render.isBoss()) {
                        shapeRenderer.setColor(render.getColor().r, render.getColor().g, render.getColor().b, 0.3f);
                        shapeRenderer.circle(transform.getX(), transform.getY(), size * 0.8f);
                    }

                    ShapeDefinition.drawIrregularPolygon(shapeRenderer,
                        transform.getX(),
                        transform.getY(),
                        size / 2,
                        render.getPolygonSides(),
                        render.getVertexOffsets(),
                        render.getColor(),
                        transform.getRotation());

                    // Draw Flatlandia-style organs inside monster
                    ShapeDefinition.drawOrgans(shapeRenderer,
                        transform.getX(),
                        transform.getY(),
                        size / 2,
                        render.getOrganOffsets(),
                        render.getOrganCount(),
                        render.getColor());

                } else if (render.getShapeType() != null) {
                    ShapeDefinition.drawShape(shapeRenderer,
                        render.getShapeType(),
                        transform.getX(),
                        transform.getY(),
                        transform.getScale() * render.getSize(),
                        render.getColor(),
                        transform.getRotation());

                    // Draw eyes on player
                    if (entity.getTag().equals("player")) {
                        drawPlayerEyes(transform, render, closestEnemyPos, hasEnemy[0]);
                    }
                } else if (entity.getTag().equals("diamond")) {
                    // Draw diamond shape
                    drawDiamond(transform, render);
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

        // Draw outlines for Flatlandia style (monsters only)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        entityManager.forEachEntity(entity -> {
            if (entity.hasComponent("render") && entity.hasComponent("transform")) {
                TransformComponent transform = entity.getTransformComponent();
                RenderComponent render = entity.getRenderComponent();

                if (render.getPolygonSides() > 0) {
                    float size = render.getSize() * transform.getScale();
                    // Darker outline for Flatlandia look
                    Color outlineColor = new Color(
                        render.getColor().r * 0.3f,
                        render.getColor().g * 0.3f,
                        render.getColor().b * 0.3f,
                        1f
                    );
                    ShapeDefinition.drawIrregularPolygonOutline(shapeRenderer,
                        transform.getX(),
                        transform.getY(),
                        size / 2,
                        render.getPolygonSides(),
                        render.getVertexOffsets(),
                        outlineColor,
                        transform.getRotation());
                }
            }
        });
        shapeRenderer.end();

        // Render floating text
        renderFloatingText();
    }

    private void drawDiamond(TransformComponent transform, RenderComponent render) {
        float x = transform.getX();
        float y = transform.getY();
        float size = render.getSize();
        Color color = render.getColor();

        // Glow effect
        shapeRenderer.setColor(color.r, color.g, color.b, 0.4f);
        shapeRenderer.circle(x, y, size * 1.8f);

        // Diamond shape (rotated square)
        shapeRenderer.setColor(color);
        float h = size * 1.3f;  // Height
        float w = size * 0.8f;  // Width

        // Draw as two triangles
        shapeRenderer.triangle(x, y + h, x - w, y, x + w, y);  // Top
        shapeRenderer.triangle(x, y - h * 0.7f, x - w, y, x + w, y);  // Bottom
    }

    private void renderFloatingText() {
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        entityManager.forEachEntity(entity -> {
            if (entity.hasComponent("floatingText") && entity.hasComponent("transform")) {
                TransformComponent transform = entity.getTransformComponent();
                FloatingTextComponent text = entity.getFloatingTextComponent();

                if (text != null) {
                    text.update(Gdx.graphics.getDeltaTime());

                    float alpha = text.getAlpha();
                    Color color = text.getColor();
                    font.setColor(color.r, color.g, color.b, alpha);
                    font.getData().setScale(text.getScale());

                    // Center the text
                    font.draw(batch, text.getText(),
                        transform.getX() - 30,
                        transform.getY());
                }
            }
        });

        font.getData().setScale(1f);
        font.setColor(Color.WHITE);
        batch.end();
    }

    private void drawPlayerEyes(TransformComponent transform, RenderComponent render, float[] targetPos, boolean hasTarget) {
        float x = transform.getX();
        float y = transform.getY();
        float size = render.getSize() * transform.getScale();

        // Eye positions relative to player center
        float eyeSpacing = size * 0.25f;
        float eyeYOffset = size * 0.1f;
        float eyeRadius = size * 0.12f;
        float pupilRadius = eyeRadius * 0.5f;

        // Calculate pupil direction
        float dirX = 0, dirY = 1;  // Default: look up
        if (hasTarget) {
            float dx = targetPos[0] - x;
            float dy = targetPos[1] - y;
            float len = (float) Math.sqrt(dx * dx + dy * dy);
            if (len > 0) {
                dirX = dx / len;
                dirY = dy / len;
            }
        }

        // Pupil offset from eye center (limited to stay inside white)
        float pupilOffset = eyeRadius * 0.4f;

        // Left eye
        float leftEyeX = x - eyeSpacing;
        float leftEyeY = y + eyeYOffset;
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.circle(leftEyeX, leftEyeY, eyeRadius);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.circle(leftEyeX + dirX * pupilOffset, leftEyeY + dirY * pupilOffset, pupilRadius);

        // Right eye
        float rightEyeX = x + eyeSpacing;
        float rightEyeY = y + eyeYOffset;
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.circle(rightEyeX, rightEyeY, eyeRadius);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.circle(rightEyeX + dirX * pupilOffset, rightEyeY + dirY * pupilOffset, pupilRadius);
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
        levelUpDialog.dispose();
        uiStage.dispose();
    }
}
