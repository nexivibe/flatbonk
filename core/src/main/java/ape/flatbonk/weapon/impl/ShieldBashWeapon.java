package ape.flatbonk.weapon.impl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.factory.BulletFactory;
import ape.flatbonk.weapon.Weapon;

public class ShieldBashWeapon extends Weapon {

    public ShieldBashWeapon() {
        super(1.5f, 35, 150f);
    }

    @Override
    protected void doFire(EntityManager entityManager, float x, float y, float targetX, float targetY) {
        float baseAngle = MathUtils.atan2(targetY - y, targetX - x) * MathUtils.radiansToDegrees;

        // Create a wide arc of short-range projectiles
        int count = 7 + level * 2;
        float arcAngle = 90f;

        for (int i = 0; i < count; i++) {
            float angle = baseAngle - arcAngle / 2 + (arcAngle / count) * i;
            float dx = MathUtils.cosDeg(angle);
            float dy = MathUtils.sinDeg(angle);

            BulletFactory.createPiercingBullet(entityManager, x, y, dx, dy,
                getDamage(), getSpeed(), 20f, new Color(0.5f, 0.7f, 1f, 0.8f),
                0.4f, 5);
        }
    }

    @Override
    public String getName() {
        return "Shield Bash";
    }
}
