package org.example.hotel.core.model;

import org.example.hotel.core.dto.HotelDTO;
import org.example.hotel.core.view.IEntity;
import org.example.hotel.core.view.IHotel;
import org.example.hotel.core.view.IRoom;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Hotel extends Entity implements IHotel {
    private final HashMap<Integer, Room> rooms = new HashMap<>();
    private String name;

    public Hotel(String name) {
        this.name = name;
    }

    protected Hotel(int id, String name) {
        super(id);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<IRoom> getRooms() {
        return List.copyOf(rooms.values());
    }

    @Override
    public float getOccupancyRate(LocalDate from, LocalDate to) {
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("Invalid time period");
        }

        long nights = ChronoUnit.DAYS.between(from, to);
        if (rooms.isEmpty()) return 0f;

        int totalOccupiedNights = 0;
        for (var room : rooms.values()) {
            totalOccupiedNights += room.getOccupiedNights(from, to);
        }

        long totalPossibleNights = nights * rooms.size();
        return (float) totalOccupiedNights / totalPossibleNights * 100;
    }

    @Override
    public int getRevenue(LocalDate from, LocalDate to) {
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("Invalid time period");
        }

        var revenue = 0;
        for (var room : rooms.values()) {
            revenue += room.getRevenue(from, to);
        }
        return revenue;
    }

    @Override
    public List<IRoom> findAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        if (!checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException("Invalid time period: Check-out must be after check-in");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check in can not be before today");
        }

        var available = new ArrayList<IRoom>();
        for (var room : rooms.values()) {
            if (room.getIsAvailable(checkIn, checkOut)) {
                available.add(room);
            }
        }
        return available;
    }

    public Reservation reserveRoom(Guest guest, Room room, LocalDate checkIn, LocalDate checkOut) {
        if (!checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException("Invalid time period");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check in can not be before today");
        }
        if (!rooms.containsKey(room.getLocalId())) {
            throw new IllegalArgumentException("Room not found");
        }

        var reservation = room.reserve(guest, checkIn, checkOut);
        guest.addReservation(reservation);

        return reservation;
    }

    public Room addRoom(int localId, String type, int price) {
        if (rooms.containsKey(localId)) {
            throw new IllegalArgumentException("Room with this localId already exists");
        }
        var room = new Room(this, localId, type, price);
        rooms.put(localId, room);
        return room;
    }

    public void setRoomPrice(int hotelId, int localId, int price) {
        var room = getRoom(localId);
        room.setPrice(price);
        Entity.update(this);
    }

    @Override
    public Room getRoom(int localId) {
        return rooms.get(localId);
    }

    public void removeRoom(int id) {
        rooms.remove(id);
    }

    @Override
    public HotelDTO toDTO() {
        var dto = new HotelDTO();
        dto.id = getId();
        dto.name = name;
        return dto;
    }

    public static Hotel fromDTO(HotelDTO dto) {
        return new Hotel(dto.id, dto.name);
    }

    public void addRoom(Room room) {
        if (room.getHotel().equals(this)) {
            rooms.put(room.getLocalId(), room);
        }
     }
    @Override
    public String toString() {
        return "Hotel: id=" + getId() + ", name=" + getName() + ", rooms=" + getRooms().stream().map(IEntity::getId);
    }

}