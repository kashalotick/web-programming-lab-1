package org.example.hotel.core.model;

import java.time.LocalDate;
import java.util.ArrayList;

public class Hotel extends Entity {
    private ArrayList<Room> rooms = new ArrayList<>();


    public void findAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
    }

    public void bookRoom(Guest guest, Room roomId, LocalDate checkIn, LocalDate checkOut) {

    }
}
