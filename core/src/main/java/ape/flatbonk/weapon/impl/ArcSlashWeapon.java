package ape.flatbonk.weapon.impl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.factory.BulletFactory;
import ape.flatbonk.weapon.Weapon;

public class ArcSlashWeapon extends Weapon {

    public ArcSlashWeapon() {
        super(0.5f, 20, 200f);
    }

    @Override
    protected void doFire(EntityManager entityManager, float x, float y, float targetX, float targetY) {
        float baseAngle = MathUtils.atan2(targetY - y, targetX - x) * MathUtils.radiansToDegrees;
        int slashCount = 5 + level * 2;
        float arcAngle = 120f;

        for (int i = 0; i < slashCount; i++) {
            float angle = baseAngle - arcAngle / 2 + (arcAngle / slashCount) * i;
            float dx = MathUtils.cosDeg(angle);
            float dy = MathUtils.sinDeg(angle);

            BulletFactory.createPlayerBullet(entityManager, x, y, dx, dy,
                getDamage(), getSpeed(), 15f, new Color(0.8f, 0.8f, 1f, 1f),
                0.3f);
        }
    }

    @Override
    public String getName() {
        return "Arc Slash";
    }
}
