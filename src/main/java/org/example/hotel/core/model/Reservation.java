package org.example.hotel.core.model;

import java.time.LocalDate;

public class Reservation extends Entity{
    private boolean isActive;

    public Guest guest;
    public Room room;
    public LocalDate checkIn;
    public LocalDate checkOut;
    public int grandTotal;




    public Reservation(Guest guest, Room room, LocalDate checkIn, LocalDate checkOut, int grandTotal) {
        this.guest = guest;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.grandTotal = grandTotal;
    }


    public boolean getIsActive() {
        return isActive;
    }

    public int getDuration() {
        var nights = checkIn.getDayOfYear() - checkOut.getDayOfYear();

        return nights;
    }
    public void cancel() {
        room.cancelReservation(this);
        isActive = false;
    }
}
