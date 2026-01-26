package ape.flatbonk.entity;

import java.util.HashMap;
import java.util.Map;

import ape.flatbonk.entity.component.*;

public class Entity {
    private static int nextId = 0;

    private final int id;
    private final Map<String, Object> components;
    private boolean active;
    private String tag;

    public Entity() {
        this.id = nextId++;
        this.components = new HashMap<String, Object>();
        this.active = true;
        this.tag = "";
    }

    public int getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void addComponent(String name, Object component) {
        components.put(name, component);
    }

    public boolean hasComponent(String name) {
        return components.containsKey(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T getComponent(String name, Class<T> type) {
        return (T) components.get(name);
    }

    public TransformComponent getTransformComponent() {
        return getComponent("transform", TransformComponent.class);
    }

    public VelocityComponent getVelocityComponent() {
        return getComponent("velocity", VelocityComponent.class);
    }

    public HealthComponent getHealthComponent() {
        return getComponent("health", HealthComponent.class);
    }

    public RenderComponent getRenderComponent() {
        return getComponent("render", RenderComponent.class);
    }

    public CollisionComponent getCollisionComponent() {
        return getComponent("collision", CollisionComponent.class);
    }

    public WeaponComponent getWeaponComponent() {
        return getComponent("weapon", WeaponComponent.class);
    }

    public AIComponent getAIComponent() {
        return getComponent("ai", AIComponent.class);
    }

    public DropComponent getDropComponent() {
        return getComponent("drop", DropComponent.class);
    }

    public LifetimeComponent getLifetimeComponent() {
        return getComponent("lifetime", LifetimeComponent.class);
    }

    public PlayerStatsComponent getPlayerStatsComponent() {
        return getComponent("playerStats", PlayerStatsComponent.class);
    }

    public void reset() {
        components.clear();
        active = true;
        tag = "";
    }
}
