package org.example.hotel.core.model;

import org.example.hotel.core.view.IGuest;
import org.example.hotel.core.view.IReservation;

import java.util.ArrayList;
import java.util.List;

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
        reservations.remove(reservation);
    }
}
