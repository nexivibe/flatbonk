package ape.flatbonk.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import ape.flatbonk.state.GameSettings;
import ape.flatbonk.util.Constants;

public class ControlBar extends InputAdapter {
    private final VirtualJoystick joystick;
    private final DashButton dashButton;
    private final Viewport viewport;
    private final Vector3 touchPoint;
    private final Vector2 keyboardDirection;
    private boolean useKeyboard;

    public ControlBar(Viewport viewport) {
        this.viewport = viewport;
        this.touchPoint = new Vector3();
        this.keyboardDirection = new Vector2();
        this.useKeyboard = false;

        // Get handedness preference
        boolean leftHanded = GameSettings.getInstance().isLeftHanded();

        // Position controls for single-hand operation
        // Right-handed (default): Joystick centered-right, Dash to left of joystick
        // Left-handed: Joystick centered-left, Dash to right of joystick
        float centerX = Constants.VIEWPORT_WIDTH / 2;
        float joystickOffset = Constants.JOYSTICK_RADIUS + 20;  // Offset from center
        float dashOffset = Constants.JOYSTICK_RADIUS * 2 + 50;  // Distance between joystick and dash

        float joystickX, dashX;
        if (leftHanded) {
            // Left-handed: joystick on left, dash to right of joystick
            joystickX = centerX - joystickOffset;
            dashX = joystickX + dashOffset;
        } else {
            // Right-handed: joystick on right, dash to left of joystick
            joystickX = centerX + joystickOffset;
            dashX = joystickX - dashOffset;
        }

        float joystickY = Constants.CONTROL_BAR_HEIGHT / 2;
        float dashY = Constants.CONTROL_BAR_HEIGHT / 2;

        this.joystick = new VirtualJoystick(joystickX, joystickY);
        this.dashButton = new DashButton(dashX, dashY);

        // Note: Input processor is set by GameScreen.show() to include this ControlBar
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchPoint.set(screenX, screenY, 0);
        viewport.unproject(touchPoint);

        useKeyboard = false;

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

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE || keycode == Input.Keys.SHIFT_LEFT) {
            dashButton.touchDown(dashButton.getCenterX(), dashButton.getCenterY());
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.SPACE || keycode == Input.Keys.SHIFT_LEFT) {
            dashButton.touchUp();
            return true;
        }
        return false;
    }

    public void update(float delta) {
        dashButton.update(delta);
        updateKeyboardInput();
    }

    private void updateKeyboardInput() {
        float kx = 0;
        float ky = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            ky += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            ky -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            kx -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            kx += 1;
        }

        if (kx != 0 || ky != 0) {
            useKeyboard = true;
            float len = (float) Math.sqrt(kx * kx + ky * ky);
            keyboardDirection.set(kx / len, ky / len);
        } else if (useKeyboard) {
            keyboardDirection.set(0, 0);
        }
    }

    public void render(ShapeRenderer shapeRenderer, Viewport viewport) {
        // Draw control bar background with retro style
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.02f, 0.02f, 0.05f, 1f);
        shapeRenderer.rect(0, 0, Constants.VIEWPORT_WIDTH, Constants.CONTROL_BAR_HEIGHT);
        shapeRenderer.end();

        // Draw neon separator line
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0f, 1f, 1f, 0.8f); // Cyan neon
        shapeRenderer.line(0, Constants.CONTROL_BAR_HEIGHT, Constants.VIEWPORT_WIDTH, Constants.CONTROL_BAR_HEIGHT);
        shapeRenderer.end();

        // Render controls
        joystick.render(shapeRenderer, viewport);
        dashButton.render(shapeRenderer, viewport);
    }

    public Vector2 getDirection() {
        if (useKeyboard) {
            return keyboardDirection;
        }
        return joystick.getDirection();
    }

    public VirtualJoystick getJoystick() {
        return joystick;
    }

    public DashButton getDashButton() {
        return dashButton;
    }

    public boolean isUsingKeyboard() {
        return useKeyboard;
    }
}
