package org.example.hotel.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    @Test
    @DisplayName("Метод cancel() має видаляти посилання і в Кімнаті, і в Гостя")
    void testCancelWorkflow() {
        Hotel hotel = new Hotel("Test");
        Room room = hotel.addRoom(101, "Lux", 200);
        Guest guest = new Guest("Taras");

        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(2);

        Reservation res = hotel.reserveRoom(guest, room, start, end);

        assertEquals(1, room.getReservations().size());
        assertEquals(1, guest.getReservations().size());

        res.cancel();

        assertEquals(0, room.getReservations().size(), "Резервація має бути видалена з кімнати");
        assertEquals(0, guest.getReservations().size(), "Резервація має бути видалена з гостя");
    }
}