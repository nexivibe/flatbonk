package ape.flatbonk.weapon.impl;

import com.badlogic.gdx.graphics.Color;

import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.factory.BulletFactory;
import ape.flatbonk.weapon.Weapon;
import ape.flatbonk.util.Constants;

public class TurretWeapon extends Weapon {

    public TurretWeapon() {
        super(0.4f, 10, Constants.DEFAULT_BULLET_SPEED);
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

        int bulletCount = 1 + (level / 2);
        for (int i = 0; i < bulletCount; i++) {
            float spread = (i - bulletCount / 2f) * 5f;
            float angle = (float) Math.atan2(dy, dx) + (float) Math.toRadians(spread);
            float bdx = (float) Math.cos(angle);
            float bdy = (float) Math.sin(angle);

            BulletFactory.createPlayerBullet(entityManager, x, y, bdx, bdy,
                getDamage(), getSpeed(), 5f, new Color(0.6f, 0.6f, 0.6f, 1f),
                Constants.DEFAULT_BULLET_LIFETIME);
        }
    }

    @Override
    public String getName() {
        return "Turret Deploy";
    }
}
