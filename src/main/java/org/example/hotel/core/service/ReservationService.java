package org.example.hotel.core.service;

import org.example.hotel.core.model.*;
import org.example.hotel.core.repository.GuestRepository;
import org.example.hotel.core.repository.HotelRepository;
import org.example.hotel.core.repository.ReservationRepository;
import org.example.hotel.core.repository.RoomRepository;
import org.example.hotel.core.view.IReservation;
import org.example.hotel.core.view.IRoom;

import java.time.LocalDate;
import java.util.List;

public class ReservationService {
    private final HotelRepository hotelRepository;
    private final GuestRepository guestRepository;
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public ReservationService(HotelRepository hotelRepository, GuestRepository guestRepository,
                              ReservationRepository reservationRepository, RoomRepository roomRepository) {
        this.hotelRepository = hotelRepository;
        this.guestRepository = guestRepository;
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    public List<IRoom> getAvailableRooms(int hotelId, LocalDate checkIn, LocalDate checkOut) {
        var hotel = hotelRepository.readWithRooms(hotelId, roomRepository)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found"));
        return List.copyOf(hotel.findAvailableRooms(checkIn, checkOut));
    }

    public IReservation makeReservation(int hotelId, int roomLocalId, int guestId, LocalDate checkIn, LocalDate checkOut) {
        var hotel = hotelRepository.readWithRooms(hotelId, roomRepository)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found"));
        var guest = guestRepository.read(guestId)
                .orElseThrow(() -> new IllegalArgumentException("Guest not found"));
        var room = hotel.getRoom(roomLocalId);
        if (room == null) {
            throw new IllegalArgumentException("Room not found: " + roomLocalId);
        }

        var reservation = hotel.reserveRoom(guest, room, checkIn, checkOut);
        reservationRepository.create(reservation);
        return reservation;
    }

    public IReservation getReservation(int reservationId) {
        // Резервація потребує room і guest — дістаємо через JOIN який вже є у findByGuestId
        // Простіше зробити окремий метод в репозиторії
        return reservationRepository.read(reservationId, hotelRepository, roomRepository, guestRepository)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
    }

    public void cancelReservation(int reservationId) {
        reservationRepository.read(reservationId, hotelRepository, roomRepository, guestRepository)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        // Просто видаляємо з БД — не чіпаємо in-memory стан
        reservationRepository.delete(reservationId);

//        var reservation = (Reservation) getReservation(reservationId);
//        reservation.cancel();
        reservationRepository.delete(reservationId);
    }
}