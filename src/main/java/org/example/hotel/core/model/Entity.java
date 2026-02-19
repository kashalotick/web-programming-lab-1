package org.example.hotel.core.model;

import java.util.HashMap;

public abstract class Entity {
    public final int id;
    public static final HashMap<Class<?>, Integer> nextIds = new HashMap<>();
    public static final HashMap<Class<?>, HashMap<Integer, Entity>> entities = new HashMap<>();

    protected Entity() {
        this.id = nextIds.computeIfAbsent(this.getClass(), k -> 0);
        var clazz = this.getClass();
        System.out.println(clazz);
        nextIds.put(clazz, id + 1);
    }

    public void create(Entity entity) {
        var clazz = entity.getClass();
        entities.computeIfAbsent(clazz, k -> new HashMap<>()).put(entity.id, entity);
    }
    public Entity read(int id) {
        var clazz = this.getClass();
        return entities.get(clazz).get(id);
    }
    public void update(int id, Entity entity) {
        var clazz = this.getClass();
        entities.get(clazz).put(id, entity);
    }
    public void delete(int id) {
        var clazz = this.getClass();
        entities.get(clazz).remove(id);
    }
}
