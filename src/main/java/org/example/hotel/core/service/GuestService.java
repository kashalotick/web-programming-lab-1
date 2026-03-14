package org.example.hotel.core.service;

import org.example.hotel.core.model.Entity;
import org.example.hotel.core.model.Guest;
import org.example.hotel.core.repository.GuestRepository;
import org.example.hotel.core.view.IGuest;

import java.util.List;

public class GuestService {
    private final GuestRepository guestRepository;

    public GuestService(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    public IGuest create(String name) {
        var guest = new Guest(name);
        guestRepository.create(guest);
        return guest;
    }

    public IGuest get(int id) {
        return guestRepository.read(id)
                .orElseThrow(() -> new IllegalArgumentException("Guest not found"));
    }

    public List<IGuest> getAll() {
        return List.copyOf(guestRepository.readAll());
    }

    public IGuest setName(int id, String name) {
        var guest = getWritable(id);
        guest.setName(name);
        guestRepository.update(guest);
        return guest;
    }

    public void delete(int id) {
        var guest = getWritable(id);
        if (!guest.getReservations().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete guest with reservations");
        }
        guestRepository.delete(id);
    }

    private Guest getWritable(int id) {
        return guestRepository.read(id)
                .orElseThrow(() -> new IllegalArgumentException("Guest not found"));
    }
}