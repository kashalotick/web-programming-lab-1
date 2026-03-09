package org.example.hotel.core.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoomTest {
    private Hotel mockHotel;
    private Guest mockGuest;
    private Room room;

    @BeforeEach
    void setUp() {
        mockHotel = mock(Hotel.class);
        mockGuest = mock(Guest.class);

        room = new Room(mockHotel, 101, "Standard", 100);
    }

    @Test
    @DisplayName("Має перевіряти доступність кімнати на дати")
    void testAvailability() {
        LocalDate today = LocalDate.now();

        assertTrue(room.getIsAvailable(today, today.plusDays(1)));

        room.reserve(mockGuest, today, today.plusDays(1));

        assertFalse(room.getIsAvailable(today, today.plusDays(1)));
        assertTrue(room.getIsAvailable(today.plusDays(7), today.plusDays(8)));
    }

    @Test
    @DisplayName("Має викидати помилку при бронюванні зайнятої кімнати")
    void testReserveOccupiedRoom() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(3);

        room.reserve(mockGuest, start, end);

        assertThrows(IllegalStateException.class, () -> room.reserve(mockGuest, start, end));
    }

    @Test
    @DisplayName("Має правильно розраховувати дохід (revenue) при частковому перетині періодів")
    void testRevenueCalculation() {
        LocalDate checkIn = LocalDate.now().plusDays(2);
        LocalDate checkOut = LocalDate.now().plusDays(5); // 3 ночі = 300
        room.reserve(mockGuest, checkIn, checkOut);

        assertEquals(300, room.getRevenue(LocalDate.now(), LocalDate.now().plusDays(10)));

        // Період лише на 1 ніч перетинається
        // Рахуємо revenue для 2-го по 3-й день (1 ніч)
        assertEquals(100, room.getRevenue(checkIn, checkIn.plusDays(1)));
    }

    @Test
    @DisplayName("Має сортувати резервації за датою заїзду")
    void testReservationSorting() {
        LocalDate d1 = LocalDate.now().plusDays(10);
        LocalDate d2 = LocalDate.now().plusDays(1);

        room.reserve(mockGuest, d1, d1.plusDays(1));
        room.reserve(mockGuest, d2, d2.plusDays(1));

        var reservations = room.getReservations();
        assertEquals(2, reservations.size());
        // Першою має бути та, що раніше (d2)
        assertEquals(d2, reservations.get(0).getCheckIn());
    }

    @Test
    @DisplayName("Room не має видаляти бронювання, яке йому не належить")
    void testRoomSecurityOnRemove() {
        Room roomA = new Room(mockHotel, 1, "A", 100);
        Room roomB = new Room(mockHotel, 2, "B", 100);

        Reservation resA = new Reservation(mockGuest, roomA, LocalDate.now(), LocalDate.now().plusDays(1), 100);

        // Намагаємось видалити з roomB резервацію, яка стосується roomA
        assertThrows(IllegalArgumentException.class, () ->
                roomB.removeReservation(resA)
        );
    }

}