package ape.flatbonk.entity;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EntityManager {
    private final Array<Entity> entities;
    private final Array<Entity> entitiesToAdd;
    private final Array<Entity> entitiesToRemove;
    private final Pool<Entity> entityPool;

    private Entity playerEntity;

    public EntityManager() {
        this.entities = new Array<Entity>();
        this.entitiesToAdd = new Array<Entity>();
        this.entitiesToRemove = new Array<Entity>();

        this.entityPool = new Pool<Entity>() {
            @Override
            protected Entity newObject() {
                return new Entity();
            }
        };
    }

    public Entity createEntity() {
        Entity entity = entityPool.obtain();
        entity.reset();
        entitiesToAdd.add(entity);
        return entity;
    }

    public void removeEntity(Entity entity) {
        entity.setActive(false);
        entitiesToRemove.add(entity);
    }

    public void setPlayerEntity(Entity player) {
        this.playerEntity = player;
    }

    public Entity getPlayerEntity() {
        return playerEntity;
    }

    public void forEachEntity(Consumer<Entity> action) {
        for (Entity entity : entities) {
            if (entity.isActive()) {
                action.accept(entity);
            }
        }
    }

    public List<Entity> getEntitiesWithTag(String tag) {
        List<Entity> result = new ArrayList<Entity>();
        for (Entity entity : entities) {
            if (entity.isActive() && entity.getTag().equals(tag)) {
                result.add(entity);
            }
        }
        return result;
    }

    public List<Entity> getEntitiesWithComponents(String... componentNames) {
        List<Entity> result = new ArrayList<Entity>();
        for (Entity entity : entities) {
            if (entity.isActive()) {
                boolean hasAll = true;
                for (String name : componentNames) {
                    if (!entity.hasComponent(name)) {
                        hasAll = false;
                        break;
                    }
                }
                if (hasAll) {
                    result.add(entity);
                }
            }
        }
        return result;
    }

    public void update() {
        // Add pending entities
        for (Entity entity : entitiesToAdd) {
            entities.add(entity);
        }
        entitiesToAdd.clear();

        // Remove pending entities
        for (Entity entity : entitiesToRemove) {
            entities.removeValue(entity, true);
            entityPool.free(entity);
        }
        entitiesToRemove.clear();
    }

    public void dispose() {
        entities.clear();
        entitiesToAdd.clear();
        entitiesToRemove.clear();
        entityPool.clear();
    }

    public int getEntityCount() {
        return entities.size;
    }
}
