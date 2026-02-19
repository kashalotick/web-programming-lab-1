package org.example.hotel.service;

import org.example.hotel.model.Room;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class RoomService {
    private final Map<String, Room> rooms = new HashMap<>();

    public Room create(Room room) {
        if (rooms.containsKey(room.getId())) {
            throw new IllegalArgumentException("Номер з таким id вже існує");
        }
        rooms.put(room.getId(), room);
        return room;
    }

    public Room getById(String id) {
        Room room = rooms.get(id);
        if (room == null) {
            throw new NoSuchElementException("Номер не знайдено");
        }
        return room;
    }

    public List<Room> getAll() {
        return new ArrayList<>(rooms.values());
    }

    public Room update(String id, Room updatedRoom) {
        if (!rooms.containsKey(id)) {
            throw new NoSuchElementException("Номер не знайдено");
        }
        updatedRoom.setId(id);
        rooms.put(id, updatedRoom);
        return updatedRoom;
    }

    public void delete(String id) {
        if (rooms.remove(id) == null) {
            throw new NoSuchElementException("Номер не знайдено");
        }
    }

    public List<Room> getSorted(Comparator<Room> comparator) {
        List<Room> snapshot = getAll();
        snapshot.sort(comparator);
        return snapshot;
    }
}
