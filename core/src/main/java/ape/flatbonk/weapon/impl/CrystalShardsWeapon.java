package ape.flatbonk.weapon.impl;

import com.badlogic.gdx.graphics.Color;

import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.factory.BulletFactory;
import ape.flatbonk.weapon.Weapon;
import ape.flatbonk.util.Constants;

public class CrystalShardsWeapon extends Weapon {

    public CrystalShardsWeapon() {
        super(0.9f, 12, Constants.DEFAULT_BULLET_SPEED * 1.2f);
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

        // Main shard
        BulletFactory.createPlayerBullet(entityManager, x, y, dx, dy,
            getDamage(), getSpeed(), 8f, new Color(0.8f, 0.4f, 1f, 1f),
            Constants.DEFAULT_BULLET_LIFETIME);

        // Fragment shards at angles
        int fragments = 2 + level;
        float spreadAngle = 20f;
        for (int i = 0; i < fragments; i++) {
            float offset = (i - fragments / 2f) * spreadAngle / fragments;
            float angle = (float) Math.atan2(dy, dx) + (float) Math.toRadians(offset);
            float fdx = (float) Math.cos(angle);
            float fdy = (float) Math.sin(angle);

            BulletFactory.createPlayerBullet(entityManager, x, y, fdx, fdy,
                getDamage() / 2, getSpeed() * 0.8f, 5f, new Color(0.7f, 0.3f, 0.9f, 1f),
                Constants.DEFAULT_BULLET_LIFETIME * 0.7f);
        }
    }

    @Override
    public String getName() {
        return "Crystal Shards";
    }
}
