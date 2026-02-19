package org.example.hotel.service;

import org.example.hotel.model.Visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class VisitorService {
    private final Map<String, Visitor> visitors = new HashMap<>();

    public Visitor create(Visitor visitor) {
        if (!visitor.hasValidEmail()) {
            throw new IllegalArgumentException("Некоректний email відвідувача");
        }
        if (visitors.containsKey(visitor.getId())) {
            throw new IllegalArgumentException("Відвідувач з таким id вже існує");
        }
        visitors.put(visitor.getId(), visitor);
        return visitor;
    }

    public Visitor getById(String id) {
        Visitor visitor = visitors.get(id);
        if (visitor == null) {
            throw new NoSuchElementException("Відвідувача не знайдено");
        }
        return visitor;
    }

    public List<Visitor> getAll() {
        return new ArrayList<>(visitors.values());
    }

    public Visitor update(String id, Visitor updatedVisitor) {
        if (!updatedVisitor.hasValidEmail()) {
            throw new IllegalArgumentException("Некоректний email відвідувача");
        }
        if (!visitors.containsKey(id)) {
            throw new NoSuchElementException("Відвідувача не знайдено");
        }
        updatedVisitor.setId(id);
        visitors.put(id, updatedVisitor);
        return updatedVisitor;
    }

    public void delete(String id) {
        if (visitors.remove(id) == null) {
            throw new NoSuchElementException("Відвідувача не знайдено");
        }
    }
}
