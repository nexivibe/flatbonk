package ape.flatbonk.weapon.impl;

import com.badlogic.gdx.graphics.Color;

import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.factory.BulletFactory;
import ape.flatbonk.weapon.Weapon;
import ape.flatbonk.util.Constants;

public class SpreadShotWeapon extends Weapon {
    private int projectileCount;
    private float spreadAngle;

    public SpreadShotWeapon() {
        super(0.8f, 15, Constants.DEFAULT_BULLET_SPEED);
        this.projectileCount = 5;
        this.spreadAngle = 60f;
    }

    @Override
    protected void doFire(EntityManager entityManager, float x, float y, float targetX, float targetY) {
        BulletFactory.createSpreadShot(entityManager, x, y, targetX, targetY,
            getDamage(), getSpeed(), 6f, Color.CYAN,
            projectileCount + (level - 1), spreadAngle);
    }

    @Override
    protected void onUpgrade() {
        projectileCount++;
        spreadAngle = Math.min(90f, spreadAngle + 5f);
    }

    @Override
    public String getName() {
        return "Spread Shot";
    }
}
