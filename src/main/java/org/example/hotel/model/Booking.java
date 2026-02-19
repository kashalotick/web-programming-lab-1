package org.example.hotel.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Booking {
    private String id;
    private String roomId;
    private String visitorId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double totalPrice;

    public Booking(String id, String roomId, String visitorId, LocalDate checkInDate, LocalDate checkOutDate, double totalPrice) {
        this.id = id;
        this.roomId = roomId;
        this.visitorId = visitorId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public long getNights() {
        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        if (nights <= 0) {
            throw new IllegalArgumentException("Дата виїзду має бути пізніше за дату заїзду");
        }
        return nights;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking booking)) return false;
        return Double.compare(totalPrice, booking.totalPrice) == 0 &&
                Objects.equals(id, booking.id) &&
                Objects.equals(roomId, booking.roomId) &&
                Objects.equals(visitorId, booking.visitorId) &&
                Objects.equals(checkInDate, booking.checkInDate) &&
                Objects.equals(checkOutDate, booking.checkOutDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roomId, visitorId, checkInDate, checkOutDate, totalPrice);
    }
}
