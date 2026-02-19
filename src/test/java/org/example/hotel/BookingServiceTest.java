package org.example.hotel;

import org.example.hotel.model.Booking;
import org.example.hotel.model.Hotel;
import org.example.hotel.model.Room;
import org.example.hotel.model.Visitor;
import org.example.hotel.service.BookingService;
import org.example.hotel.service.RoomService;
import org.example.hotel.service.VisitorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BookingServiceTest {
    private RoomService roomService;
    private VisitorService visitorService;
    private BookingService bookingService;
    private Hotel hotel;

    @BeforeEach
    void setUp() {
        hotel = new Hotel("Test Hotel", 5);
        roomService = new RoomService();
        visitorService = new VisitorService();
        bookingService = new BookingService(hotel, roomService, visitorService);

        roomService.create(new Room("R1", "STANDARD", 1000, true));
        visitorService.create(new Visitor("V1", "Ivan Ivanov", "ivan@test.com"));
    }

    @Test
    void shouldBookRoomAndCalculateTotal() {
        Booking booking = bookingService.bookRoom("R1", "V1", LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 13));

        assertEquals(3000, booking.getTotalPrice());
        assertFalse(roomService.getById("R1").isAvailable());
        assertTrue(bookingService.getBookingStats().contains("Заброньовано: 1"));
    }

    @Test
    void shouldFailWhenBookingUnavailableRoom() {
        bookingService.bookRoom("R1", "V1", LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 11));

        assertThrows(IllegalStateException.class,
                () -> bookingService.bookRoom("R1", "V1", LocalDate.of(2026, 2, 10), LocalDate.of(2026, 2, 11)));
    }

    @Test
    void shouldFailOnInvalidDateRange() {
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.bookRoom("R1", "V1", LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 10)));
    }
}
