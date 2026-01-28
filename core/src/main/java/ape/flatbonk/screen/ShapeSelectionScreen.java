package ape.flatbonk.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import ape.flatbonk.Main;
import ape.flatbonk.render.ShapeDefinition;
import ape.flatbonk.util.Constants;
import ape.flatbonk.util.ShapeType;

public class ShapeSelectionScreen extends AbstractGameScreen {
    // Portrait mode: 3 columns x 5 rows
    private static final int COLS = 3;
    private static final int ROWS = 5;
    private static final float CELL_SIZE = 80f;
    private static final float CELL_PADDING = 12f;
    private static final float GRID_START_X = (Constants.VIEWPORT_WIDTH - (COLS * CELL_SIZE + (COLS - 1) * CELL_PADDING)) / 2;
    private static final float GRID_START_Y = Constants.VIEWPORT_HEIGHT - 100f;

    private final Rectangle[] cellBounds;
    private final ShapeType[] shapes;
    private int hoveredIndex = -1;
    private final GlyphLayout titleLayout;
    private final GlyphLayout weaponLayout;
    private final Vector3 touchPoint = new Vector3();

    public ShapeSelectionScreen(Main game) {
        super(game);

        shapes = ShapeType.values();
        cellBounds = new Rectangle[shapes.length];

        titleLayout = new GlyphLayout();
        weaponLayout = new GlyphLayout();

        font.getData().setScale(2f);
        titleLayout.setText(font, "SELECT YOUR SHAPE");
        font.getData().setScale(1.5f);

        initCellBounds();
    }

    private void initCellBounds() {
        for (int i = 0; i < shapes.length; i++) {
            int col = i % COLS;
            int row = i / COLS;
            float x = GRID_START_X + col * (CELL_SIZE + CELL_PADDING);
            float y = GRID_START_Y - row * (CELL_SIZE + CELL_PADDING) - CELL_SIZE;
            cellBounds[i] = new Rectangle(x, y, CELL_SIZE, CELL_SIZE);
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        handleInput();

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        // Draw cell backgrounds
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < shapes.length; i++) {
            Rectangle bounds = cellBounds[i];
            if (i == hoveredIndex) {
                shapeRenderer.setColor(0.3f, 0.4f, 0.6f, 1f);
            } else {
                shapeRenderer.setColor(0.15f, 0.2f, 0.3f, 1f);
            }
            shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
        shapeRenderer.end();

        // Draw cell borders and shapes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.CYAN);
        for (int i = 0; i < shapes.length; i++) {
            Rectangle bounds = cellBounds[i];
            shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
        shapeRenderer.end();

        // Draw shapes inside cells
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < shapes.length; i++) {
            Rectangle bounds = cellBounds[i];
            Color shapeColor = (i == hoveredIndex) ? Color.WHITE : Color.LIGHT_GRAY;
            ShapeDefinition.drawShape(shapeRenderer, shapes[i],
                bounds.x + bounds.width / 2,
                bounds.y + bounds.height / 2,
                CELL_SIZE * 0.6f,
                shapeColor);
        }
        shapeRenderer.end();

        // Draw text
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        // Title
        font.getData().setScale(2f);
        font.setColor(Color.CYAN);
        font.draw(batch, "SELECT YOUR SHAPE",
            (Constants.VIEWPORT_WIDTH - titleLayout.width) / 2,
            Constants.VIEWPORT_HEIGHT - 30);

        // Shape name and weapon info when hovering
        if (hoveredIndex >= 0 && hoveredIndex < shapes.length) {
            ShapeType hovered = shapes[hoveredIndex];
            font.getData().setScale(1.2f);
            font.setColor(Color.WHITE);
            String info = hovered.getDisplayName() + " - " + hovered.getWeaponName();
            weaponLayout.setText(font, info);
            font.draw(batch, info,
                (Constants.VIEWPORT_WIDTH - weaponLayout.width) / 2,
                60);
        }

        font.getData().setScale(1f);
        font.setColor(Color.GRAY);
        font.draw(batch, "Tap a shape to select", 20, 30);

        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isTouched()) {
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(touchPoint);

            hoveredIndex = -1;
            for (int i = 0; i < cellBounds.length; i++) {
                if (cellBounds[i].contains(touchPoint.x, touchPoint.y)) {
                    hoveredIndex = i;
                    break;
                }
            }
        }

        if (Gdx.input.justTouched()) {
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(touchPoint);

            for (int i = 0; i < cellBounds.length; i++) {
                if (cellBounds[i].contains(touchPoint.x, touchPoint.y)) {
                    game.getPlayerConfig().setSelectedShape(shapes[i]);
                    game.setScreen(new ColorSelectionScreen(game));
                    return;
                }
            }
        }
    }
}
