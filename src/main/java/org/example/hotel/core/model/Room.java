package org.example.hotel.core.model;

import java.time.LocalDate;
import java.util.LinkedList;

public class Room extends Entity {
    private final LinkedList<Reservation> reservations = new LinkedList<>();

    public String type;
    public int price;

    public Room(String type, int price) {
        this.type = type;
        this.price = price;
    }

    public LinkedList<Reservation> getReservations() {
        return reservations;
    }

    public int calculatePrice(LocalDate from, LocalDate to) {
        var nights = to.getDayOfYear() - from.getDayOfYear();
        return nights * price;
    }


    public boolean getIsAvailable(LocalDate checkIn, LocalDate checkOut) {
        for (var reservation : reservations) {
            if (reservation.checkIn.isBefore(checkOut) && reservation.checkOut.isAfter(checkIn)) {
                return false;
            }
        }
        return true;
    }

    public Reservation reserve(Guest guest, LocalDate checkIn, LocalDate checkOut){
        if (!getIsAvailable(checkIn, checkOut)) {
            throw new IllegalStateException("Room is not available");
        }
        var grandTotal = calculatePrice(checkIn, checkOut);

        var reservation = new Reservation(guest, this, checkIn, checkOut, grandTotal);
        reservations.add(reservation);
        return reservation;
    }

    public void cancelReservation(Reservation reservation) {
        reservations.remove(reservation);
    }

}
