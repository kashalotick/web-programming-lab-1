package org.example.hotel.core.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HotelTest {
    private Hotel hotel;
    private Guest mockGuest;

    @BeforeEach
    void setUp() {
        hotel = new Hotel("Grand Hotel");
        mockGuest = mock(Guest.class);

    }

    @Test
    @DisplayName("Має знаходити тільки доступні кімнати")
    void testFindAvailableRooms() {
        Room r1 = hotel.addRoom(1, "SNG", 50);
        Room r2 = hotel.addRoom(2, "DBL", 100);

        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(2);

        r1.reserve(mockGuest, start, end);

        var available = hotel.findAvailableRooms(start, end);

        assertEquals(1, available.size());
        assertEquals(2, available.get(0).getLocalId());
    }

    @Test
    @DisplayName("Має правильно агрегувати Occupancy Rate готелю")
    void testHotelOccupancyRate() {
        Room r1 = hotel.addRoom(1, "SNG", 100);
        Room r2 = hotel.addRoom(2, "SNG", 100);

        LocalDate from = LocalDate.now().plusDays(1);
        LocalDate to = LocalDate.now().plusDays(2);

        // Якщо 1 кімната зайнята на цю ніч, а інша вільна -> 50%
        r1.reserve(mockGuest, from, to);

        assertEquals(50.0f, hotel.getOccupancyRate(from, to));
    }

    @Test
    @DisplayName("Має викидати помилку при некоректних датах")
    void testInvalidDates() {
        LocalDate today = LocalDate.now();
        Room room = hotel.addRoom(1, "SNG", 100);

        // Check-out перед check-in
        assertThrows(IllegalArgumentException.class,
                () -> hotel.reserveRoom(mockGuest, room, today.plusDays(5), today.plusDays(1)));

        // Бронювання в минулому
        assertThrows(IllegalArgumentException.class,
                () -> hotel.reserveRoom(mockGuest, room, today.minusDays(5), today.plusDays(1)));
    }

    @Test
    @DisplayName("Має забороняти додавання кімнати з дублюючим localId")
    void testDuplicateRoomId() {
        hotel.addRoom(101, "Standard", 100);

        assertThrows(IllegalArgumentException.class, () ->
                hotel.addRoom(101, "Duplicate", 200)
        );
    }

    @Test
    @DisplayName("Має викидати помилку, якщо кімната не належить цьому готелю")
    void testRoomNotFoundInHotel() {
        Hotel otherHotel = new Hotel("Other Hotel");
        Room otherRoom = otherHotel.addRoom(202, "Luxury", 500);

        LocalDate in = LocalDate.now().plusDays(1);
        LocalDate out = LocalDate.now().plusDays(2);

        assertThrows(IllegalArgumentException.class, () ->
                        hotel.reserveRoom(mockGuest, otherRoom, in, out),
                "Має бути помилка 'Room not found'"
        );
    }

}