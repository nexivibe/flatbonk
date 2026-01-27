package ape.flatbonk.system;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.component.*;
import ape.flatbonk.input.ControlBar;
import ape.flatbonk.input.DashButton;
import ape.flatbonk.input.VirtualJoystick;
import ape.flatbonk.state.GameState;
import ape.flatbonk.util.Constants;

public class InputSystem implements GameSystem {
    private final EntityManager entityManager;
    private final ControlBar controlBar;
    private final GameState gameState;

    private boolean isDashing;
    private float dashTimer;
    private float dashDirX;
    private float dashDirY;

    public InputSystem(EntityManager entityManager, ControlBar controlBar, GameState gameState) {
        this.entityManager = entityManager;
        this.controlBar = controlBar;
        this.gameState = gameState;
        this.isDashing = false;
        this.dashTimer = 0;
    }

    @Override
    public void update(float delta) {
        controlBar.update(delta);

        Entity player = entityManager.getPlayerEntity();
        if (player == null || !player.isActive()) return;

        VelocityComponent velocity = player.getVelocityComponent();
        PlayerStatsComponent stats = player.getPlayerStatsComponent();
        TransformComponent transform = player.getTransformComponent();

        if (velocity == null) return;

        // Get direction from control bar (supports both joystick and keyboard)
        Vector2 dir = controlBar.getDirection();

        // Handle dash
        DashButton dashButton = controlBar.getDashButton();
        if (dashButton.isPressed() && !dashButton.isOnCooldown() && !isDashing) {
            if (dir.len() > 0.1f) {
                isDashing = true;
                dashTimer = Constants.DASH_DURATION;
                dashDirX = dir.x;
                dashDirY = dir.y;
                float len = (float) Math.sqrt(dashDirX * dashDirX + dashDirY * dashDirY);
                dashDirX /= len;
                dashDirY /= len;
                dashButton.triggerCooldown();

                // Make player invincible during dash
                HealthComponent health = player.getHealthComponent();
                if (health != null) {
                    health.setInvincibilityTimer(Constants.DASH_DURATION);
                }
            }
        }

        // Apply movement
        if (isDashing) {
            dashTimer -= delta;
            velocity.set(dashDirX * Constants.DASH_SPEED, dashDirY * Constants.DASH_SPEED);

            if (dashTimer <= 0) {
                isDashing = false;
            }
        } else {
            float speedMod = stats != null ? stats.getSpeedModifier() : 1f;
            float speed = Constants.PLAYER_BASE_SPEED * speedMod;

            velocity.set(dir.x * speed, dir.y * speed);
        }

        // Update player rotation to face movement direction
        if (dir.len() > 0.1f) {
            float targetRotation = MathUtils.atan2(dir.y, dir.x) * MathUtils.radiansToDegrees;
            transform.setRotation(targetRotation);
        }

        // Clamp player to game area
        float halfSize = Constants.PLAYER_SIZE / 2;
        float minY = Constants.CONTROL_BAR_HEIGHT + halfSize;
        float maxY = Constants.WORLD_HEIGHT - halfSize;
        float minX = halfSize;
        float maxX = Constants.WORLD_WIDTH - halfSize;

        if (transform.getX() < minX) transform.setX(minX);
        if (transform.getX() > maxX) transform.setX(maxX);
        if (transform.getY() < minY) transform.setY(minY);
        if (transform.getY() > maxY) transform.setY(maxY);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
