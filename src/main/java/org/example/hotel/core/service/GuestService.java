package org.example.hotel.core.service;

import org.example.hotel.core.model.Entity;
import org.example.hotel.core.model.Guest;
import org.example.hotel.core.view.IGuest;

import java.util.List;

public class GuestService {


    public static IGuest create(String name) {
        var guest = new Guest(name);
        Entity.create(guest);
        return new Guest(name);
    }

    public static IGuest get(int id) {
        return getWritable(id);
    }

    public static List<IGuest> getAll() {
        var guests = Entity.readAll(Guest.class);
        if (guests == null) {
            throw new IllegalArgumentException("No guests found");
        }
        return List.copyOf(guests);
    }

    public static IGuest setName(int id, String name) {
        var guest = getWritable(id);
        guest.setName(name);
        Entity.update(guest);
        return guest;
    }

    public static void delete(int id) {
        var guest = get(id);
        if (!guest.getReservations().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete guest with reservations");
        }
        Entity.delete(Guest.class, id);
    }

    private static Guest getWritable(int id) {
        var guest = Entity.read(Guest.class, id);
        if (guest == null) {
            throw new IllegalArgumentException("Guest not found");
        }
        return guest;
    }
}
