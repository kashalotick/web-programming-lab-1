package org.example.hotel.core.service;

import org.example.hotel.core.model.Entity;
import org.example.hotel.core.model.Guest;
import org.example.hotel.core.view.IGuest;

import java.util.List;

public class GuestService {
    public IGuest create(String name) {
        var guest = new Guest(name);
        Entity.create(guest);
        return guest;
    }

    public IGuest get(int id) {
        return getWritable(id);
    }

    public List<IGuest> getAll() {
        var guests = Entity.readAll(Guest.class);
        return List.copyOf(guests);
    }

    public IGuest setName(int id, String name) {
        var guest = getWritable(id);
        guest.setName(name);
        Entity.update(guest);
        return guest;
    }

    public void delete(int id) {
        var guest = get(id);
        if (!guest.getReservations().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete guest with reservations");
        }
        Entity.delete(Guest.class, id);
    }

    private Guest getWritable(int id) {
        var guest = Entity.read(Guest.class, id);
        if (guest == null) {
            throw new IllegalArgumentException("Guest not found");
        }
        return guest;
    }
}
