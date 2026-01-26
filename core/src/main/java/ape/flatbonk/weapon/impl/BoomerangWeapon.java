package ape.flatbonk.weapon.impl;

import com.badlogic.gdx.graphics.Color;

import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.factory.BulletFactory;
import ape.flatbonk.weapon.Weapon;

public class BoomerangWeapon extends Weapon {

    public BoomerangWeapon() {
        super(1.8f, 20, 300f);
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

        // Create piercing bullet that acts like a boomerang
        BulletFactory.createPiercingBullet(entityManager, x, y, dx, dy,
            getDamage(), getSpeed(), 12f, new Color(0.6f, 0.8f, 1f, 1f),
            3f, 10 + level * 2);
    }

    @Override
    public String getName() {
        return "Boomerang";
    }
}
