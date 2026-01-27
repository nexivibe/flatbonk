package ape.flatbonk.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.powerup.Powerup;
import ape.flatbonk.powerup.PowerupRegistry;
import ape.flatbonk.state.GameState;
import ape.flatbonk.util.Constants;

import java.util.List;

public class LevelUpDialog {
    private final Stage stage;
    private final GameState gameState;
    private final EntityManager entityManager;
    private final Runnable onSelect;
    private final PowerupRegistry powerupRegistry;
    private final BitmapFont font;

    private Table dialogTable;
    private boolean visible;

    public LevelUpDialog(Stage stage, GameState gameState, EntityManager entityManager, Runnable onSelect) {
        this.stage = stage;
        this.gameState = gameState;
        this.entityManager = entityManager;
        this.onSelect = onSelect;
        this.powerupRegistry = new PowerupRegistry();
        this.font = new BitmapFont();
        this.visible = false;
    }

    public void show() {
        if (dialogTable != null) {
            dialogTable.remove();
        }

        visible = true;
        dialogTable = new Table();
        dialogTable.setFillParent(true);

        // Semi-transparent background would go here

        Table contentTable = new Table();

        // Title
        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = font;
        titleStyle.fontColor = Color.YELLOW;
        font.getData().setScale(2f);
        Label titleLabel = new Label("LEVEL UP!", titleStyle);
        contentTable.add(titleLabel).padBottom(20).row();
        font.getData().setScale(1.5f);

        // Refresh weapon powerup to show a weapon the player doesn't have
        Entity player = entityManager.getPlayerEntity();
        if (player != null) {
            powerupRegistry.refreshWeaponPowerup(player);
        }

        // Get 3 random powerups
        List<Powerup> options = powerupRegistry.getRandomPowerups(3);

        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.CYAN;
        buttonStyle.downFontColor = Color.GRAY;

        for (final Powerup powerup : options) {
            String buttonText = powerup.getName() + "\n" + powerup.getDescription();
            if (powerup.getLevel() > 0) {
                buttonText += " (Lv." + (powerup.getLevel() + 1) + ")";
            }

            TextButton button = new TextButton(buttonText, buttonStyle);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectPowerup(powerup);
                }
            });

            contentTable.add(button).width(250).height(70).padBottom(15).row();
        }

        dialogTable.add(contentTable).center();
        stage.addActor(dialogTable);
    }

    private void selectPowerup(Powerup powerup) {
        // Apply powerup to player
        Entity player = entityManager.getPlayerEntity();
        if (player != null) {
            powerup.apply(player);
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
}
