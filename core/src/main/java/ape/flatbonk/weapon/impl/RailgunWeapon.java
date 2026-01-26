package ape.flatbonk.weapon.impl;

import com.badlogic.gdx.graphics.Color;

import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.factory.BulletFactory;
import ape.flatbonk.weapon.Weapon;

public class RailgunWeapon extends Weapon {

    public RailgunWeapon() {
        super(2.5f, 50, 1200f);
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

        // High damage, high speed piercing shot
        BulletFactory.createPiercingBullet(entityManager, x, y, dx, dy,
            getDamage() + level * 10, getSpeed(), 4f, new Color(1f, 1f, 1f, 1f),
            1.5f, 20);
    }

    @Override
    public String getName() {
        return "Railgun";
    }
}
