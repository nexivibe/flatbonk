package ape.flatbonk.weapon.impl;

import com.badlogic.gdx.graphics.Color;

import ape.flatbonk.entity.Entity;
import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.factory.BulletFactory;
import ape.flatbonk.weapon.Weapon;
import ape.flatbonk.util.Constants;

public class LifeDrainWeapon extends Weapon {

    public LifeDrainWeapon() {
        super(1.0f, 12, Constants.DEFAULT_BULLET_SPEED * 0.8f);
    }

    @Override
    protected void doFire(EntityManager entityManager, float x, float y, float targetX, float targetY) {
        float dx = targetX - x;
        float dy = targetY - y;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len > 0) {
            dx /= len;
            dy /= len;
        }

        // The actual life drain effect would be handled in CollisionSystem
        // For now, create a special tagged bullet
        Entity bullet = BulletFactory.createPlayerBullet(entityManager, x, y, dx, dy,
            getDamage(), getSpeed(), 10f, new Color(1f, 0.3f, 0.5f, 1f),
            Constants.DEFAULT_BULLET_LIFETIME);
        bullet.setTag("lifeDrainBullet");
    }

    @Override
    public String getName() {
        return "Life Drain";
    }
}
