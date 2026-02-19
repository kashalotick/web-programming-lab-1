package org.example.hotel;

import org.example.hotel.model.Room;
import org.example.hotel.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class RoomServiceTest {
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        roomService = new RoomService();
    }

    @Test
    void shouldCreateAndReadRoom() {
        Room room = new Room("R1", "STANDARD", 1500, true);
        roomService.create(room);

        Room found = roomService.getById("R1");
        assertEquals(room, found);
    }

    @Test
    void shouldFailOnDuplicateRoomId() {
        roomService.create(new Room("R1", "STANDARD", 1500, true));

        assertThrows(IllegalArgumentException.class,
                () -> roomService.create(new Room("R1", "LUX", 2500, true)));
    }

    @Test
    void shouldUpdateRoom() {
        roomService.create(new Room("R1", "STANDARD", 1500, true));

        Room updated = roomService.update("R1", new Room("R1", "LUX", 2500, false));

        assertEquals("LUX", updated.getType());
        assertFalse(updated.isAvailable());
    }

    @Test
    void shouldThrowWhenDeletingUnknownRoom() {
        assertThrows(NoSuchElementException.class, () -> roomService.delete("UNKNOWN"));
    }
}
