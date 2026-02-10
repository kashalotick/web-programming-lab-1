package org.example.hotel.core.repository;

import org.example.hotel.core.model.Model;

public interface Repository<T extends Model> {
    T add(T model);
    T get(int id);
    T replace(int id, T model);
    void remove(int id);
}
