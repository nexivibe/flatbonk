package ape.flatbonk.weapon.impl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import ape.flatbonk.entity.EntityManager;
import ape.flatbonk.entity.factory.BulletFactory;
import ape.flatbonk.weapon.Weapon;

public class PiercingLaserWeapon extends Weapon {
    private int pierceCount;

    public PiercingLaserWeapon() {
        super(1.2f, 25, 600f);
        this.pierceCount = 3;
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

        BulletFactory.createPiercingBullet(entityManager, x, y, dx, dy,
            getDamage(), getSpeed(), 10f, new Color(0.2f, 1f, 0.4f, 1f),
            2.5f, pierceCount + level - 1);
    }

    @Override
    protected void onUpgrade() {
        pierceCount++;
        baseDamage += 5;
    }

    @Override
    public String getName() {
        return "Piercing Laser";
    }
}
