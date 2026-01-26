package ape.flatbonk.weapon.impl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.factory.BulletFactory;
import ape.flatbonk.weapon.Weapon;
import ape.flatbonk.util.Constants;

public class NovaBurstWeapon extends Weapon {
    private int rayCount;

    public NovaBurstWeapon() {
        super(2.0f, 18, Constants.DEFAULT_BULLET_SPEED);
        this.rayCount = 8;
    }

    @Override
    protected void doFire(EntityManager entityManager, float x, float y, float targetX, float targetY) {
        int totalRays = rayCount + (level - 1) * 2;
        float angleStep = 360f / totalRays;

        for (int i = 0; i < totalRays; i++) {
            float angle = i * angleStep;
            float dx = MathUtils.cosDeg(angle);
            float dy = MathUtils.sinDeg(angle);

            BulletFactory.createPlayerBullet(entityManager, x, y, dx, dy,
                getDamage(), getSpeed(), 7f, new Color(1f, 1f, 0.3f, 1f),
                Constants.DEFAULT_BULLET_LIFETIME);
        }
    }

    @Override
    protected void onUpgrade() {
        rayCount += 2;
    }

    @Override
    public String getName() {
        return "Nova Burst";
    }
}
