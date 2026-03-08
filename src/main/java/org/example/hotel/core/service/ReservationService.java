package org.example.hotel.core.service;

import org.example.hotel.core.model.*;
import org.example.hotel.core.view.IReservation;
import org.example.hotel.core.view.IRoom;

import java.time.LocalDate;
import java.util.List;

public class ReservationService {
    public static List<IRoom> getAvailableRooms(int hotelId, LocalDate checkIn, LocalDate checkOut) {
        var hotel = Entity.read(Hotel.class, hotelId);
        if (hotel == null) {
            throw new IllegalArgumentException("Hotel not found");
        }
        var availableRooms = hotel.findAvailableRooms(checkIn, checkOut);
        if (availableRooms.isEmpty()) {
            throw new IllegalArgumentException("No available rooms");
        }
        return List.copyOf(availableRooms);
    }

    public static IReservation makeReservation(int hotelId, int roomId, int guestId, LocalDate checkIn, LocalDate checkOut) {
        var hotel = Entity.read(Hotel.class, hotelId);
        var room = Entity.read(Room.class, roomId);
        var guest = Entity.read(Guest.class, guestId);
        if (hotel == null || room == null || guest == null) {
            throw new IllegalArgumentException("Hotel, room, or guest not found: " + hotelId + ", " + roomId + ", " + guestId);
        }
        var reservation = hotel.reserveRoom(guest, room, checkIn, checkOut);

        Entity.create(reservation);
        Entity.update(room);
        Entity.update(guest);
        return reservation;
    }

    public static void cancelReservation(int reservationId) {
        var reservation = Entity.read(Reservation.class, reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found");
        }
        reservation.cancel();
        Entity.delete(Reservation.class, reservationId);
    }
}
