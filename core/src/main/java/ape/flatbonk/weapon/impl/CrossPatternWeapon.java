package ape.flatbonk.weapon.impl;

import com.badlogic.gdx.graphics.Color;

import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.factory.BulletFactory;
import ape.flatbonk.weapon.Weapon;
import ape.flatbonk.util.Constants;

public class CrossPatternWeapon extends Weapon {

    public CrossPatternWeapon() {
        super(0.7f, 14, Constants.DEFAULT_BULLET_SPEED);
    }

    @Override
    protected void doFire(EntityManager entityManager, float x, float y, float targetX, float targetY) {
        // Fire in 4 cardinal directions
        float[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (float[] dir : directions) {
            BulletFactory.createPlayerBullet(entityManager, x, y, dir[0], dir[1],
                getDamage(), getSpeed(), 7f, new Color(1f, 0.4f, 0.4f, 1f),
                Constants.DEFAULT_BULLET_LIFETIME);
        }

        // At higher levels, add diagonal shots
        if (level >= 3) {
            float[][] diagonals = {{0.707f, 0.707f}, {-0.707f, 0.707f},
                {0.707f, -0.707f}, {-0.707f, -0.707f}};
            for (float[] dir : diagonals) {
                BulletFactory.createPlayerBullet(entityManager, x, y, dir[0], dir[1],
                    getDamage() / 2, getSpeed(), 5f, new Color(1f, 0.6f, 0.6f, 1f),
                    Constants.DEFAULT_BULLET_LIFETIME);
            }
        }
    }

    @Override
    public String getName() {
        return "Cross Pattern";
    }
}
