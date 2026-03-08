package org.example.hotel.core.model;

import org.example.hotel.core.dto.ReservationDTO;
import org.example.hotel.core.view.IHotel;
import org.example.hotel.core.view.IReservation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    protected Reservation(int id, Guest guest, Room room, LocalDate checkIn, LocalDate checkOut, int grandTotal) {
        super(id);
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

    @Override
    public ReservationDTO toDTO() {
        var dto = new ReservationDTO();
        dto.id = getId();
        dto.guestId = guest.getId();
        dto.hotelId = room.getHotel().getId();
        dto.roomId = room.getId();
        dto.checkIn = checkIn.format(DateTimeFormatter.ISO_DATE);
        dto.checkOut = checkOut.format(DateTimeFormatter.ISO_DATE);
        dto.grandTotal = grandTotal;
        return dto;
    }

    public static Reservation fromDTO(ReservationDTO dto, Room room, Guest guest) {
        var checkIn = LocalDate.parse(dto.checkIn, DateTimeFormatter.ISO_DATE);
        var checkOut = LocalDate.parse(dto.checkOut, DateTimeFormatter.ISO_DATE);
        return new Reservation(dto.id, guest, room, checkIn, checkOut, dto.grandTotal);
    }
}
