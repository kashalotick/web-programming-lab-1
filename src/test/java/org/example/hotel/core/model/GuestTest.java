package org.example.hotel.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GuestTest {

    @Test
    @DisplayName("Має забороняти додавати чужі резервації")
    void testAddForeignReservation() {
        Guest guest1 = new Guest("Ivan");
        Guest guest2 = new Guest("Oleg");
        Room mockRoom = mock(Room.class);

        Reservation resForOther = new Reservation(guest2, mockRoom, null, null, 100);

        assertThrows(IllegalArgumentException.class, () -> guest1.addReservation(resForOther));
    }

    @Test
    @DisplayName("Має правильно створювати DTO")
    void testToDTO() {
        Guest guest = new Guest("Marina");
        var dto = guest.toDTO();

        assertEquals("Marina", dto.name);
    }
}