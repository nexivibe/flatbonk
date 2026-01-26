package ape.flatbonk.entity.component;

public class CollisionComponent {
    public static final int MASK_NONE = 0;
    public static final int MASK_PLAYER = 1;
    public static final int MASK_MONSTER = 2;
    public static final int MASK_PLAYER_BULLET = 4;
    public static final int MASK_MONSTER_BULLET = 8;
    public static final int MASK_PICKUP = 16;
    public static final int MASK_HAZARD = 32;

    private float radius;
    private int collisionMask;
    private int collidesWithMask;

    public CollisionComponent(float radius, int collisionMask, int collidesWithMask) {
        this.radius = radius;
        this.collisionMask = collisionMask;
        this.collidesWithMask = collidesWithMask;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public int getCollisionMask() {
        return collisionMask;
    }

    public void setCollisionMask(int collisionMask) {
        this.collisionMask = collisionMask;
    }

    public int getCollidesWithMask() {
        return collidesWithMask;
    }

    public void setCollidesWithMask(int collidesWithMask) {
        this.collidesWithMask = collidesWithMask;
    }

    public boolean canCollideWith(CollisionComponent other) {
        return (this.collidesWithMask & other.collisionMask) != 0;
    }

    public boolean overlaps(TransformComponent thisTransform, TransformComponent otherTransform, CollisionComponent other) {
        float dx = otherTransform.getX() - thisTransform.getX();
        float dy = otherTransform.getY() - thisTransform.getY();
        float distSq = dx * dx + dy * dy;
        float radiusSum = this.radius + other.radius;
        return distSq <= radiusSum * radiusSum;
    }
}
