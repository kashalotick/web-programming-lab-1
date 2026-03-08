package org.example.hotel.core.model;

import org.example.hotel.core.view.IEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public abstract class Entity implements IEntity {
    private final int id;
    private static final HashMap<Class<?>, Integer> nextIds = new HashMap<>();
    private static final HashMap<Class<?>, HashMap<Integer, Entity>> entities = new HashMap<>();

    @Override
    public int getId() {
        return id;
    }

    protected Entity() {
        var clazz = this.getClass();
        this.id = nextIds.getOrDefault(clazz, 0);
        nextIds.put(clazz, this.id + 1);
    }
    protected Entity(int id) {
        this.id = id;
        var clazz = this.getClass();
        int currentNext = nextIds.getOrDefault(clazz, 0);
        if (id >= currentNext) {
            nextIds.put(clazz, id + 1);
        }
    }


    public static void create(Entity entity) {
        var clazz = entity.getClass();
        entities.computeIfAbsent(clazz, k -> new HashMap<>()).put(entity.id, entity);
    }

    public static <T extends Entity> T read(Class<T> clazz, int id) {
        if (!entities.containsKey(clazz)) return null;
        return clazz.cast(entities.get(clazz).get(id));
    }

    public static <T extends Entity> List<T> readAll(Class<T> clazz) {
        if (!entities.containsKey(clazz)) return List.of();

        return entities.get(clazz).values().stream()
                .map(clazz::cast)
                .toList();
    }

    public static void update(Entity entity) {
        var clazz = entity.getClass();
        if (entities.containsKey(clazz)) {
            entities.get(clazz).put(entity.id, entity);
        }
    }

    public static void delete(Class<?> clazz, int id) {
        if (entities.containsKey(clazz)) {
            entities.get(clazz).remove(id);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof IEntity other)) return false;
        if (obj.getClass() != this.getClass()) return false;
        return this.id == other.getId();
    }

    public abstract Serializable toDTO();

    public static void clear() {
        entities.clear();
        nextIds.clear();
    }
}
