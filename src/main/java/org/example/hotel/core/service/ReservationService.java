package org.example.hotel.core.service;

import org.example.hotel.core.model.*;
import org.example.hotel.core.view.IReservation;
import org.example.hotel.core.view.IRoom;

import java.time.LocalDate;
import java.util.List;

public class ReservationService {
    public List<IRoom> getAvailableRooms(int hotelId, LocalDate checkIn, LocalDate checkOut) {
        var hotel = Entity.read(Hotel.class, hotelId);
        if (hotel == null) {
            throw new IllegalArgumentException("Hotel not found");
        }
        var availableRooms = hotel.findAvailableRooms(checkIn, checkOut);
        return List.copyOf(availableRooms);
    }

    public IReservation makeReservation(int hotelId, int roomLocalId, int guestId, LocalDate checkIn, LocalDate checkOut) {
        var hotel = Entity.read(Hotel.class, hotelId);
        if (hotel == null) {
            throw new IllegalArgumentException("Hotel not found");
        }
        var room = hotel.getRoom(roomLocalId);
        var guest = Entity.read(Guest.class, guestId);
        if (room == null || guest == null) {
            throw new IllegalArgumentException("Room or guest not found: " + roomLocalId + ", " + guestId);
        }
        var reservation = hotel.reserveRoom(guest, room, checkIn, checkOut);

        Entity.create(reservation);
        Entity.update(room);
        Entity.update(guest);
        return reservation;
    }

    public IReservation getReservation(int reservationId) {
        var reservation = Entity.read(Reservation.class, reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found");
        }
        return reservation;
    }

    public void cancelReservation(int reservationId) {
        var reservation = Entity.read(Reservation.class, reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found");
        }
        var room = reservation.getRoom();
        var guest = reservation.getGuest();

        reservation.cancel();
        Entity.delete(Reservation.class, reservationId);
        Entity.update(room);
        Entity.update(guest);
    }
}
