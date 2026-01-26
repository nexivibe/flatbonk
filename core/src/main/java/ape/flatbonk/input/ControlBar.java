package ape.flatbonk.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

import ape.flatbonk.util.Constants;

public class ControlBar extends InputAdapter {
    private final VirtualJoystick joystick;
    private final DashButton dashButton;
    private final Viewport viewport;
    private final Vector3 touchPoint;

    public ControlBar(Stage stage, Viewport viewport) {
        this.viewport = viewport;
        this.touchPoint = new Vector3();

        // Position joystick on left side of control bar
        float joystickX = Constants.JOYSTICK_RADIUS + 30;
        float joystickY = Constants.CONTROL_BAR_HEIGHT / 2;
        this.joystick = new VirtualJoystick(joystickX, joystickY);

        // Position dash button on right side
        float dashX = Constants.WORLD_WIDTH - 80;
        float dashY = Constants.CONTROL_BAR_HEIGHT / 2;
        this.dashButton = new DashButton(dashX, dashY);

        // Set this as input processor (combined with stage)
        Gdx.input.setInputProcessor(new com.badlogic.gdx.InputMultiplexer(stage, this));
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchPoint.set(screenX, screenY, 0);
        viewport.unproject(touchPoint);

        if (joystick.touchDown(touchPoint.x, touchPoint.y, pointer)) {
            return true;
        }
        if (dashButton.touchDown(touchPoint.x, touchPoint.y)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        touchPoint.set(screenX, screenY, 0);
        viewport.unproject(touchPoint);

        return joystick.touchDragged(touchPoint.x, touchPoint.y, pointer);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        joystick.touchUp(pointer);
        dashButton.touchUp();
        return false;
    }

    public void update(float delta) {
        dashButton.update(delta);
    }

    public void render(ShapeRenderer shapeRenderer, Viewport viewport) {
        // Draw control bar background
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.08f, 0.08f, 0.12f, 1f);
        shapeRenderer.rect(0, 0, Constants.WORLD_WIDTH, Constants.CONTROL_BAR_HEIGHT);
        shapeRenderer.end();

        // Draw separator line
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.2f, 0.3f, 0.4f, 1f);
        shapeRenderer.line(0, Constants.CONTROL_BAR_HEIGHT, Constants.WORLD_WIDTH, Constants.CONTROL_BAR_HEIGHT);
        shapeRenderer.end();

        // Render controls
        joystick.render(shapeRenderer, viewport);
        dashButton.render(shapeRenderer, viewport);
    }

    public VirtualJoystick getJoystick() {
        return joystick;
    }

    public DashButton getDashButton() {
        return dashButton;
    }
}
