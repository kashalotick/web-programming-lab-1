package org.example.hotel.core.model;

import org.example.hotel.core.dto.ReservationDTO;
import org.example.hotel.core.dto.RoomDTO;
import org.example.hotel.core.view.IEntity;
import org.example.hotel.core.view.IReservation;
import org.example.hotel.core.view.IRoom;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Room extends Entity implements IRoom {
    private final LinkedList<Reservation> reservations = new LinkedList<>();
    private final Hotel hotel;
    private final int localId;
    private final String type;
    private int price;

    public Room(Hotel hotel, int localId, String type, int price) {
        this.hotel = hotel;
        this.localId = localId;
        this.type = type;
        this.price = price;
    }

    protected Room(int id, Hotel hotel, int localId, String type, int price) {
        super(id);
        this.hotel = hotel;
        this.localId = localId;
        this.type = type;
        this.price = price;
    }

    @Override
    public Hotel getHotel() {
        return hotel;
    }

    @Override
    public int getLocalId() {
        return localId;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public List<IReservation> getReservations() {
        return List.copyOf(reservations);
    }



    public int getOccupiedNights(LocalDate from, LocalDate to) {
        int occupied = 0;
        for (var reservation : reservations) {
            LocalDate start = from.isAfter(reservation.getCheckIn()) ? from : reservation.getCheckIn();
            LocalDate end = to.isBefore(reservation.getCheckOut()) ? to : reservation.getCheckOut();

            int overlap = (int) ChronoUnit.DAYS.between(start, end);
            if (overlap > 0) {
                occupied += overlap;
            }
        }
        return occupied;
    }

    @Override
    public float getOccupancyRate(LocalDate from, LocalDate to) {
        long nights = ChronoUnit.DAYS.between(from, to);
        if (nights <= 0) return 0f;

        int occupiedNights = getOccupiedNights(from, to);
        return (float) occupiedNights / nights * 100;
    }

    @Override
    public int getRevenue(LocalDate from, LocalDate to) {
        int revenue = 0;
        for (var reservation : reservations) {
            LocalDate start = from.isAfter(reservation.getCheckIn()) ? from : reservation.getCheckIn();
            LocalDate end = to.isBefore(reservation.getCheckOut()) ? to : reservation.getCheckOut();

            int overlap = (int) ChronoUnit.DAYS.between(start, end);
            if (overlap > 0) {
                revenue += overlap * price;
            }
        }
        return revenue;
    }

    @Override
    public boolean getIsAvailable(LocalDate checkIn, LocalDate checkOut) {
        for (var reservation : reservations) {
            if (reservation.getCheckIn().isBefore(checkOut) && reservation.getCheckOut().isAfter(checkIn)) {
                return false;
            }
        }
        return true;
    }

    public Reservation reserve(Guest guest, LocalDate checkIn, LocalDate checkOut) {
        if (!getIsAvailable(checkIn, checkOut)) {
            throw new IllegalStateException("Room is not available");
        }
        var grandTotal = calculatePrice(checkIn, checkOut);

        var reservation = new Reservation(guest, this, checkIn, checkOut, grandTotal);
        insertReservation(reservation);
        return reservation;
    }

    private void insertReservation(Reservation reservation) {
        var index = 0;
        for (var r : reservations) {
            if (r.getCheckIn().isAfter(reservation.getCheckIn())) break;
            index++;
        }
        reservations.add(index, reservation);
    }

    public void removeReservation(Reservation reservation) {
        if (!reservation.getRoom().equals(this)) {
            throw new IllegalArgumentException("Reservation does not belong to this room");
        }
        if (!reservations.contains(reservation)) {
            throw new IllegalStateException("Reservation not found");
        }
        reservations.remove(reservation);
    }

    private int calculatePrice(LocalDate from, LocalDate to) {
        var nights = (int) ChronoUnit.DAYS.between(from, to);
        return nights * price;
    }

    @Override
    public RoomDTO toDTO() {
        var dto = new RoomDTO();
        dto.id = getId();
        dto.localId = localId;
        dto.hotelId = hotel.getId();
        dto.type = type;
        dto.price = price;
        return dto;
    }

    public static Room fromDTO(RoomDTO dto, Hotel hotel) {
        return new Room(dto.id, hotel, dto.localId, dto.type, dto.price);
    }

    public void addReservation(Reservation reservation) {
        if (reservation.getRoom().equals(this)) {
            insertReservation(reservation);
        }
    }

    @Override
    public String toString() {
        return "Room: id=" + getId() + ", hotel=" + getHotel().getId() + ", localId=" + getLocalId() + ", type=" + getType() + ", price=" + getPrice() + ", reservations=" + getReservations().stream().map(IEntity::getId).collect(Collectors.toList());
    }
}
