package org.example.hotel.core.model;

import org.example.hotel.core.dto.GuestDTO;
import org.example.hotel.core.view.IEntity;
import org.example.hotel.core.view.IGuest;
import org.example.hotel.core.view.IReservation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Guest extends Entity implements IGuest {
    private String name;
    private final ArrayList<Reservation> reservations = new ArrayList<>();

    public Guest(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<IReservation> getReservations() {
        return List.copyOf(reservations);
    }

    public void addReservation(Reservation reservation) {
        if (!reservation.getGuest().equals(this)) {
            throw new IllegalArgumentException("Reservation does not belong to this guest");
        }
        reservations.add(reservation);
    }

    public void removeReservation(Reservation reservation) {
        if (!reservation.getGuest().equals(this)) {
            throw new IllegalArgumentException("Reservation does not belong to this guest");
        }
        if (!reservations.contains(reservation)) {
            throw new IllegalStateException("Reservation not found");  // ← додати це
        }
        reservations.remove(reservation);
    }

    @Override
    public GuestDTO toDTO() {
        var dto = new GuestDTO();
        dto.id = getId();
        dto.name = name;
        return dto;
    }

    public static Guest fromDTO(GuestDTO dto) {
        var guest = new Guest(dto.name);
        guest.setId(dto.id);
        return guest;
    }


    @Override
    public String toString() {
        return "Guest: id=" + getId() + ", name=" + getName() + ", reservations=" + getReservations().stream().map(IEntity::getId).collect(Collectors.toList());
    }
}
