package ape.flatbonk.weapon.impl;

import com.badlogic.gdx.graphics.Color;

import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.factory.BulletFactory;
import ape.flatbonk.weapon.Weapon;
import ape.flatbonk.util.Constants;

public class RapidFireWeapon extends Weapon {

    public RapidFireWeapon() {
        super(0.15f, 8, Constants.DEFAULT_BULLET_SPEED * 1.3f);
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

        BulletFactory.createPlayerBullet(entityManager, x, y, dx, dy,
            getDamage(), getSpeed(), 4f, new Color(0.9f, 0.9f, 0.3f, 1f),
            Constants.DEFAULT_BULLET_LIFETIME);
    }

    @Override
    protected void onUpgrade() {
        cooldown = Math.max(0.08f, cooldown - 0.02f);
    }

    @Override
    public String getName() {
        return "Rapid Fire";
    }
}
