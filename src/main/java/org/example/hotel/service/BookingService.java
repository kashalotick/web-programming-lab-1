package org.example.hotel.service;

import org.example.hotel.model.Booking;
import org.example.hotel.model.Hotel;
import org.example.hotel.model.Room;
import org.example.hotel.model.Visitor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookingService {
    private final Hotel hotel;
    private final RoomService roomService;
    private final VisitorService visitorService;
    private final List<Booking> bookings = new ArrayList<>();

    public BookingService(Hotel hotel, RoomService roomService, VisitorService visitorService) {
        this.hotel = hotel;
        this.roomService = roomService;
        this.visitorService = visitorService;
    }

    public Booking bookRoom(String roomId, String visitorId, LocalDate checkIn, LocalDate checkOut) {
        Room room = roomService.getById(roomId);
        Visitor ignored = visitorService.getById(visitorId);

        if (!room.isAvailable()) {
            throw new IllegalStateException("Номер вже заброньований");
        }

        Booking bookingDraft = new Booking(UUID.randomUUID().toString(), roomId, visitorId, checkIn, checkOut, 0.0);
        long nights = bookingDraft.getNights();
        double totalPrice = room.calculateStayCost((int) nights);
        bookingDraft.setTotalPrice(totalPrice);

        room.setAvailable(false);
        hotel.onRoomBooked();
        bookings.add(bookingDraft);
        return bookingDraft;
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }

    public String getBookingStats() {
        return hotel.bookingStatistics();
    }
}
