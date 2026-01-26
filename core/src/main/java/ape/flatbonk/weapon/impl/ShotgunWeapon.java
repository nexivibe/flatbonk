package ape.flatbonk.weapon.impl;

import com.badlogic.gdx.graphics.Color;

import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.factory.BulletFactory;
import ape.flatbonk.weapon.Weapon;

public class ShotgunWeapon extends Weapon {
    private int pelletCount;

    public ShotgunWeapon() {
        super(1.0f, 8, 350f);
        this.pelletCount = 8;
    }

    @Override
    protected void doFire(EntityManager entityManager, float x, float y, float targetX, float targetY) {
        BulletFactory.createSpreadShot(entityManager, x, y, targetX, targetY,
            getDamage(), getSpeed(), 5f, new Color(0.9f, 0.7f, 0.2f, 1f),
            pelletCount + (level - 1) * 2, 45f);
    }

    @Override
    protected void onUpgrade() {
        pelletCount += 2;
    }

    @Override
    public String getName() {
        return "Shotgun Blast";
    }
}
