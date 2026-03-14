package org.example.hotel.core.model;

import org.example.hotel.core.view.IEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public abstract class Entity implements IEntity {
    private int id;

    @Override
    public int getId() {
        return id;
    }
    public void setId(int id) { this.id = id; }

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

    @Override
    public String toString() {
        return "Entity: id=" + getId();
    }
}
