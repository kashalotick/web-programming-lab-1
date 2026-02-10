package org.example.hotel.core.repository.impl;

import org.example.hotel.core.model.Model;
import org.example.hotel.core.repository.Repository;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryRepository<T extends Model> implements Repository<T> {
    private final HashMap<Integer, T> repository = new HashMap<Integer, T>();
    private final AtomicInteger idGenerator = new AtomicInteger(0);


    @Override
    public T add(T model) {
        var newId = idGenerator.incrementAndGet();
        model.id = newId;
        repository.put(newId, model);
        return model;
    }

    @Override
    public T get(int id) {
        return repository.get(id);
    }

    @Override
    public T replace(int id, T model) {
        model.id = id;
        return repository.put(id, model);
    }

    @Override
    public void remove(int id) {
        repository.remove(id);
    }
}
