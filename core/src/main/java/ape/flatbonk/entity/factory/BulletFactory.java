package ape.flatbonk.entity.factory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.component.*;
import ape.flatbonk.util.Constants;

public class BulletFactory {

    public static Entity createPlayerBullet(EntityManager entityManager,
                                            float x, float y,
                                            float dirX, float dirY,
                                            int damage, float speed,
                                            float size, Color color,
                                            float lifetime) {
        Entity bullet = entityManager.createEntity();
        bullet.setTag("playerBullet");

        // Normalize direction
        float len = (float) Math.sqrt(dirX * dirX + dirY * dirY);
        if (len > 0) {
            dirX /= len;
            dirY /= len;
        }

        // Transform
        TransformComponent transform = new TransformComponent(x, y);
        bullet.addComponent("transform", transform);

        // Velocity
        VelocityComponent velocity = new VelocityComponent(speed);
        velocity.set(dirX * speed, dirY * speed);
        bullet.addComponent("velocity", velocity);

        // Render
        RenderComponent render = new RenderComponent();
        render.setColor(color);
        render.setSize(size);
        bullet.addComponent("render", render);

        // Collision - use larger radius for better hit detection
        CollisionComponent collision = new CollisionComponent(
            Math.max(size, 8f),
            CollisionComponent.MASK_PLAYER_BULLET,
            CollisionComponent.MASK_MONSTER
        );
        bullet.addComponent("collision", collision);

        // Lifetime
        LifetimeComponent lifetimeComp = new LifetimeComponent(lifetime);
        bullet.addComponent("lifetime", lifetimeComp);

        // Drop component to store damage
        DropComponent damageHolder = new DropComponent(damage, 0);
        bullet.addComponent("drop", damageHolder);

        return bullet;
    }

    public static Entity createHomingBullet(EntityManager entityManager,
                                            float x, float y,
                                            float dirX, float dirY,
                                            int damage, float speed,
                                            float size, Color color,
                                            float lifetime) {
        Entity bullet = createPlayerBullet(entityManager, x, y, dirX, dirY, damage, speed, size, color, lifetime);
        bullet.setTag("homingBullet");
        return bullet;
    }

    public static Entity createPiercingBullet(EntityManager entityManager,
                                              float x, float y,
                                              float dirX, float dirY,
                                              int damage, float speed,
                                              float size, Color color,
                                              float lifetime,
                                              int pierceCount) {
        Entity bullet = createPlayerBullet(entityManager, x, y, dirX, dirY, damage, speed, size, color, lifetime);
        bullet.setTag("piercingBullet");
        // Store pierce count in health component
        HealthComponent pierceData = new HealthComponent(pierceCount);
        bullet.addComponent("health", pierceData);
        return bullet;
    }

    public static void createSpreadShot(EntityManager entityManager,
                                        float x, float y,
                                        float targetX, float targetY,
                                        int damage, float speed,
                                        float size, Color color,
                                        int projectileCount, float spreadAngle) {
        float baseAngle = MathUtils.atan2(targetY - y, targetX - x) * MathUtils.radiansToDegrees;
        float angleStep = spreadAngle / (projectileCount - 1);
        float startAngle = baseAngle - spreadAngle / 2;

        for (int i = 0; i < projectileCount; i++) {
            float angle = startAngle + angleStep * i;
            float dirX = MathUtils.cosDeg(angle);
            float dirY = MathUtils.sinDeg(angle);
            createPlayerBullet(entityManager, x, y, dirX, dirY, damage, speed, size, color, Constants.DEFAULT_BULLET_LIFETIME);
        }
    }
}
