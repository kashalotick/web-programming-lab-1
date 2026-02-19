package org.example.hotel.model;

import java.util.Objects;

public class Hotel {
    private String name;
    private int totalRooms;
    private int availableRooms;
    private int bookedRooms;

    public Hotel(String name, int totalRooms) {
        this.name = name;
        this.totalRooms = totalRooms;
        this.availableRooms = totalRooms;
        this.bookedRooms = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(int totalRooms) {
        this.totalRooms = totalRooms;
    }

    public int getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(int availableRooms) {
        this.availableRooms = availableRooms;
    }

    public int getBookedRooms() {
        return bookedRooms;
    }

    public void setBookedRooms(int bookedRooms) {
        this.bookedRooms = bookedRooms;
    }

    public void onRoomBooked() {
        if (availableRooms <= 0) {
            throw new IllegalStateException("Немає вільних номерів");
        }
        availableRooms--;
        bookedRooms++;
    }

    public void onBookingCanceled() {
        if (bookedRooms <= 0) {
            throw new IllegalStateException("Немає заброньованих номерів для скасування");
        }
        bookedRooms--;
        availableRooms++;
    }

    public String bookingStatistics() {
        return "Готель: " + name + " | Всього: " + totalRooms + " | Вільно: " + availableRooms + " | Заброньовано: " + bookedRooms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hotel hotel)) return false;
        return totalRooms == hotel.totalRooms &&
                availableRooms == hotel.availableRooms &&
                bookedRooms == hotel.bookedRooms &&
                Objects.equals(name, hotel.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, totalRooms, availableRooms, bookedRooms);
    }
}
