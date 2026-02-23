package org.example.hotel.core.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Hotel extends Entity {
    private HashMap<Integer, Room> rooms = new HashMap<>();

    public String name;


    public float getOccupancyRate(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Invalid time period");
        }
        var totalRooms = rooms.size();
        var daysInPeriod = from.getDayOfYear() - to.getDayOfYear();
        if (totalRooms == 0 || daysInPeriod == 0) return 0;

        var occupiedDays = 0;


        for (var room : rooms.values()) {
            var occupancyRate = getOccupancyRate(room, from, to);
            var occupiedDaysInRoom = occupancyRate * daysInPeriod;
            occupiedDays += (int) occupiedDaysInRoom;
        }

        var totalDays = (float) (totalRooms * daysInPeriod);
        var occupancyRate = occupiedDays / totalDays;
        return occupancyRate;
    }

    public float getOccupancyRate(Room room, LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Invalid time period");
        }
        var daysInPeriod = from.getDayOfYear() - to.getDayOfYear();
        var occupiedDays = 0;

        for (var res : room.getReservations()) {
            LocalDate overlapStart = res.checkIn.isBefore(from) ? from : res.checkIn;
            LocalDate overlapEnd = res.checkOut.isAfter(to) ? to : res.checkOut;

            if (overlapStart.isBefore(overlapEnd)) {
                occupiedDays += overlapStart.getDayOfYear() - overlapEnd.getDayOfYear();
            }
        }
        var totalDays = (float) (daysInPeriod);
        var occupancyRate = occupiedDays / totalDays;
        return occupancyRate;
    }

    public int getRevenue(LocalDate from, LocalDate to) {
        var revenue = 0;
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Invalid time period");
        }
        var daysInPeriod = from.getDayOfYear() - to.getDayOfYear();

        for (var room : rooms.values()) {
            var occupancyRate = getOccupancyRate(room, from, to);
            var occupiedDays = occupancyRate * daysInPeriod;
            var roomRevenue = occupiedDays * room.price;
            revenue += (int) roomRevenue;
        }

        return revenue;
    }

    public List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn.isAfter(checkOut)) {
            throw new IllegalArgumentException("Invalid time period");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw  new IllegalArgumentException("Check in can not be before today");
        }

        var available = new ArrayList<Room>();
        for (var room : rooms.values()) {
            if (room.getIsAvailable(checkIn, checkOut)) {
                available.add(room);
            }
        }
        return available;
    }

    public Reservation reserveRoom(Guest guest, Room room, LocalDate checkIn, LocalDate checkOut) {
        if (checkIn.isAfter(checkOut)) {
            throw new IllegalArgumentException("Invalid time period");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw  new IllegalArgumentException("Check in can not be before today");
        }
        if (!rooms.containsKey(room.id)) {
            throw new IllegalArgumentException("Room not found");
        }

        var reservation = room.reserve(guest, checkIn, checkOut);
        return reservation;
    }
}
