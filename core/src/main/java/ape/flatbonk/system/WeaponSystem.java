package ape.flatbonk.system;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.component.*;
import ape.flatbonk.state.GameState;
import ape.flatbonk.weapon.Weapon;

import java.util.List;

public class WeaponSystem implements GameSystem {
    private final EntityManager entityManager;
    private final GameState gameState;

    public WeaponSystem(EntityManager entityManager, GameState gameState) {
        this.entityManager = entityManager;
        this.gameState = gameState;
    }

    @Override
    public void update(float delta) {
        Entity player = entityManager.getPlayerEntity();
        if (player == null || !player.isActive()) return;

        WeaponComponent weapons = player.getWeaponComponent();
        TransformComponent playerTransform = player.getTransformComponent();
        PlayerStatsComponent stats = player.getPlayerStatsComponent();

        if (weapons == null || playerTransform == null) return;

        // Find nearest enemy
        Entity nearestEnemy = findNearestEnemy(playerTransform);

        // Update and fire weapons
        float cooldownMod = stats != null ? stats.getCooldownModifier() : 1f;
        weapons.setAttackCooldownModifier(cooldownMod);
        weapons.update(delta);

        for (Weapon weapon : weapons.getWeapons()) {
            if (weapon.canFire()) {
                float targetX, targetY;
                if (nearestEnemy != null) {
                    TransformComponent enemyTransform = nearestEnemy.getTransformComponent();
                    targetX = enemyTransform.getX();
                    targetY = enemyTransform.getY();
                } else {
                    // Fire in the direction the player is facing (right by default)
                    targetX = playerTransform.getX() + 100;
                    targetY = playerTransform.getY();
                }

                weapon.fire(entityManager, playerTransform.getX(), playerTransform.getY(),
                    targetX, targetY, cooldownMod);
            }
        }
    }

    private Entity findNearestEnemy(TransformComponent playerTransform) {
        List<Entity> monsters = entityManager.getEntitiesWithTag("monster");
        Entity nearest = null;
        float nearestDist = Float.MAX_VALUE;

        for (Entity monster : monsters) {
            if (!monster.isActive()) continue;

            TransformComponent monsterTransform = monster.getTransformComponent();
            if (monsterTransform == null) continue;

            float dist = playerTransform.distanceTo(monsterTransform);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = monster;
            }
        }

        return nearest;
    }

    @Override
    public int getPriority() {
        return 15;
    }
}
