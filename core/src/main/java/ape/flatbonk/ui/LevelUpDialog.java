package ape.flatbonk.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.powerup.UpgradeGenerator;
import ape.flatbonk.powerup.UpgradeGenerator.Upgrade;
import ape.flatbonk.powerup.UpgradeGenerator.UpgradeType;
import ape.flatbonk.state.GameState;
import ape.flatbonk.util.Constants;

import java.util.List;

public class LevelUpDialog {
    private final Stage stage;
    private final GameState gameState;
    private final EntityManager entityManager;
    private final Runnable onSelect;
    private final BitmapFont font;
    private final BitmapFont titleFont;
    private final ShapeRenderer shapeRenderer;
    private final Texture buttonUpTex;
    private final Texture buttonOverTex;
    private final Texture buttonDownTex;

    private Table dialogTable;
    private boolean visible;

    public LevelUpDialog(Stage stage, GameState gameState, EntityManager entityManager, Runnable onSelect) {
        this.stage = stage;
        this.gameState = gameState;
        this.entityManager = entityManager;
        this.onSelect = onSelect;
        this.font = new BitmapFont();
        this.titleFont = new BitmapFont();
        this.shapeRenderer = new ShapeRenderer();
        this.visible = false;

        // Create button background textures
        buttonUpTex = createColorTexture(0.15f, 0.15f, 0.25f, 0.95f);
        buttonOverTex = createColorTexture(0.2f, 0.2f, 0.35f, 0.95f);
        buttonDownTex = createColorTexture(0.1f, 0.1f, 0.15f, 0.95f);
    }

    private Texture createColorTexture(float r, float g, float b, float a) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(r, g, b, a);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public void show() {
        if (dialogTable != null) {
            dialogTable.remove();
        }

        visible = true;
        dialogTable = new Table();
        dialogTable.setFillParent(true);

        Table contentTable = new Table();

        // Title with level number - use separate font for title
        titleFont.getData().setScale(2f);
        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = titleFont;
        titleStyle.fontColor = Color.YELLOW;
        Label titleLabel = new Label("LEVEL " + gameState.getPlayerLevel() + "!", titleStyle);
        contentTable.add(titleLabel).padBottom(8).row();

        // Subtitle - reset font scale
        font.getData().setScale(1f);
        Label.LabelStyle subtitleStyle = new Label.LabelStyle();
        subtitleStyle.font = font;
        subtitleStyle.fontColor = Color.CYAN;
        Label subtitleLabel = new Label("Choose an upgrade", subtitleStyle);
        contentTable.add(subtitleLabel).padBottom(20).row();

        // Get player and generate upgrades
        Entity player = entityManager.getPlayerEntity();
        List<Upgrade> options = UpgradeGenerator.generateUpgrades(player, 3);

        // Button style with visible backgrounds
        font.getData().setScale(1f);
        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.YELLOW;
        buttonStyle.downFontColor = Color.GRAY;
        buttonStyle.up = new TextureRegionDrawable(buttonUpTex);
        buttonStyle.over = new TextureRegionDrawable(buttonOverTex);
        buttonStyle.down = new TextureRegionDrawable(buttonDownTex);

        for (final Upgrade upgrade : options) {
            String icon = getIconForUpgrade(upgrade.type);
            String buttonText = icon + " " + upgrade.name + "\n    " + upgrade.description;

            TextButton button = new TextButton(buttonText, buttonStyle);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectUpgrade(upgrade);
                }
            });

            // Portrait mode: narrower buttons
            contentTable.add(button).width(280).height(55).padBottom(8).row();
        }

        dialogTable.add(contentTable).center();
        stage.addActor(dialogTable);
    }

    private String getIconForUpgrade(UpgradeType type) {
        switch (type) {
            case NEW_WEAPON: return "[+]";
            case WEAPON_DAMAGE: return "[!]";
            case WEAPON_SPEED: return "[>]";
            case EXTRA_PROJECTILES: return "[*]";
            case PROJECTILE_SIZE: return "[O]";
            case MAX_HEALTH: return "[H]";
            case ARMOR: return "[#]";
            case HEALTH_REGEN: return "[~]";
            case MOVE_SPEED: return "[^]";
            case DASH_DISTANCE: return "[=]";
            case XP_BONUS: return "[X]";
            case PICKUP_RANGE: return "[@]";
            case CRIT_CHANCE: return "[%]";
            case CRIT_DAMAGE: return "[&]";
            default: return "[?]";
        }
    }

    private void selectUpgrade(Upgrade upgrade) {
        Entity player = entityManager.getPlayerEntity();
        if (player != null) {
            UpgradeGenerator.applyUpgrade(player, upgrade);
        }

        hide();

        if (onSelect != null) {
            onSelect.run();
        }
    }

    public void hide() {
        visible = false;
        if (dialogTable != null) {
            dialogTable.remove();
            dialogTable = null;
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void renderBackground(Viewport viewport) {
        if (!visible) return;

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Dark overlay
        shapeRenderer.setColor(0, 0, 0, 0.75f);
        shapeRenderer.rect(0, 0, Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);

        // Dialog background - centered (portrait mode)
        float dialogWidth = 300;
        float dialogHeight = 280;
        float dialogX = (Constants.VIEWPORT_WIDTH - dialogWidth) / 2;
        float dialogY = (Constants.VIEWPORT_HEIGHT - dialogHeight) / 2;

        shapeRenderer.setColor(0.08f, 0.08f, 0.15f, 0.98f);
        shapeRenderer.rect(dialogX, dialogY, dialogWidth, dialogHeight);
        shapeRenderer.end();

        // Border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.rect(dialogX, dialogY, dialogWidth, dialogHeight);
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
        titleFont.dispose();
        buttonUpTex.dispose();
        buttonOverTex.dispose();
        buttonDownTex.dispose();
    }
}
