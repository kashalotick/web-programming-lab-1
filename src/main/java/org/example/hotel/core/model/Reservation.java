package org.example.hotel.core.model;

import java.time.LocalDate;

public class Reservation extends Entity{
    public Guest guest;
    public Room room;
    public LocalDate checkIn;
    public LocalDate checkOut;

    public boolean isActive;
    public int getNights() {
        return checkOut.getDayOfYear() - checkIn.getDayOfYear();
    }

    public Reservation(Guest guest, Room room, LocalDate checkIn, LocalDate checkOut) {
        this.guest = guest;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }
}
