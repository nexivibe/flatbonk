package ape.flatbonk.weapon.impl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.factory.BulletFactory;
import ape.flatbonk.weapon.Weapon;
import ape.flatbonk.util.Constants;

public class WaveBeamWeapon extends Weapon {

    public WaveBeamWeapon() {
        super(0.6f, 10, Constants.DEFAULT_BULLET_SPEED * 0.9f);
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

        int waveCount = 3 + level;
        float waveSpread = 8f;

        for (int i = 0; i < waveCount; i++) {
            float offset = (i - waveCount / 2f) * waveSpread;
            float perpX = -dy * offset;
            float perpY = dx * offset;

            BulletFactory.createPlayerBullet(entityManager,
                x + perpX, y + perpY, dx, dy,
                getDamage(), getSpeed(), 10f, new Color(0.3f, 0.7f, 1f, 1f),
                Constants.DEFAULT_BULLET_LIFETIME);
        }
    }

    @Override
    public String getName() {
        return "Wave Beam";
    }
}
