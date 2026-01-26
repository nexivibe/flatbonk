package ape.flatbonk.weapon.impl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.factory.BulletFactory;
import ape.flatbonk.weapon.Weapon;

public class HomingMissileWeapon extends Weapon {
    private int missileCount;

    public HomingMissileWeapon() {
        super(1.5f, 30, 250f);
        this.missileCount = 1;
    }

    @Override
    protected void doFire(EntityManager entityManager, float x, float y, float targetX, float targetY) {
        float baseAngle = MathUtils.atan2(targetY - y, targetX - x) * MathUtils.radiansToDegrees;

        for (int i = 0; i < missileCount + level - 1; i++) {
            float angleOffset = (i - (missileCount + level - 2) / 2f) * 15f;
            float angle = baseAngle + angleOffset;
            float dx = MathUtils.cosDeg(angle);
            float dy = MathUtils.sinDeg(angle);

            BulletFactory.createHomingBullet(entityManager, x, y, dx, dy,
                getDamage(), getSpeed(), 8f, new Color(1f, 0.5f, 0f, 1f), 4f);
        }
    }

    @Override
    protected void onUpgrade() {
        missileCount++;
    }

    @Override
    public String getName() {
        return "Homing Missiles";
    }
}
