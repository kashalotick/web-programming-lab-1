package org.example.hotel.core.repository;

import org.example.hotel.core.model.Entity;

import java.util.List;
import java.util.Optional;

public interface IRepository<T extends Entity> {
    void create(T entity);
    Optional<T> read(int id);
    List<T> readAll();
    void update(T entity);
    void delete(int id);
}