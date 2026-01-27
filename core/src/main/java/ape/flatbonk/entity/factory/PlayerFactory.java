package ape.flatbonk.entity.factory;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.component.*;
import ape.flatbonk.state.GameState;
import ape.flatbonk.util.Constants;
import ape.flatbonk.weapon.WeaponRegistry;

public class PlayerFactory {

    public static Entity createPlayer(EntityManager entityManager, GameState gameState) {
        Entity player = entityManager.createEntity();
        player.setTag("player");

        // Transform - start in center of world
        TransformComponent transform = new TransformComponent(
            Constants.WORLD_WIDTH / 2,
            Constants.WORLD_HEIGHT / 2
        );
        player.addComponent("transform", transform);

        // Velocity
        VelocityComponent velocity = new VelocityComponent(Constants.PLAYER_BASE_SPEED);
        player.addComponent("velocity", velocity);

        // Health
        HealthComponent health = new HealthComponent(Constants.PLAYER_BASE_HEALTH);
        player.addComponent("health", health);

        // Render
        RenderComponent render = new RenderComponent(
            gameState.getPlayerShape(),
            gameState.getPlayerColor().getColor(),
            Constants.PLAYER_SIZE
        );
        render.setLayer(10);
        player.addComponent("render", render);

        // Collision
        CollisionComponent collision = new CollisionComponent(
            Constants.PLAYER_SIZE / 2,
            CollisionComponent.MASK_PLAYER,
            CollisionComponent.MASK_MONSTER | CollisionComponent.MASK_MONSTER_BULLET | CollisionComponent.MASK_PICKUP
        );
        player.addComponent("collision", collision);

        // Weapons
        WeaponComponent weapons = new WeaponComponent();
        weapons.addWeapon(WeaponRegistry.createWeaponForShape(gameState.getPlayerShape()));
        player.addComponent("weapon", weapons);

        // Player stats
        PlayerStatsComponent stats = new PlayerStatsComponent();
        player.addComponent("playerStats", stats);

        entityManager.setPlayerEntity(player);

        return player;
    }
}
