package org.example.hotel.core.model;

import org.example.hotel.core.view.IHotel;
import org.example.hotel.core.view.IReservation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reservation extends Entity implements IReservation {

    private final Guest guest;
    private final Room room;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final int grandTotal;

    public Reservation(Guest guest, Room room, LocalDate checkIn, LocalDate checkOut, int grandTotal) {
        this.guest = guest;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.grandTotal = grandTotal;
    }

    @Override
    public Guest getGuest() {
        return guest;
    }

    @Override
    public IHotel getHotel() {
        return room.getHotel();
    }

    @Override
    public Room getRoom() {
        return room;
    }

    @Override
    public LocalDate getCheckIn() {
        return checkIn;
    }

    @Override
    public LocalDate getCheckOut() {
        return checkOut;
    }

    @Override
    public int getGrandTotal() {
        return grandTotal;
    }

    @Override
    public int getDuration() {
        var nights = ChronoUnit.DAYS.between(checkOut, checkIn);
        return (int) nights;
    }

    public void cancel() {
        room.removeReservation(this);
        guest.removeReservation(this);
    }
}
