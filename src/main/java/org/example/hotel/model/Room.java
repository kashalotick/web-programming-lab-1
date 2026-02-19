package org.example.hotel.model;

import java.util.Objects;

public class Room {
    private String id;
    private String type;
    private double pricePerNight;
    private boolean available;

    public Room(String id, String type, double pricePerNight, boolean available) {
        this.id = id;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.available = available;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public double calculateStayCost(int nights) {
        if (nights <= 0) {
            throw new IllegalArgumentException("Кількість ночей має бути більшою за 0");
        }
        return pricePerNight * nights;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room room)) return false;
        return Double.compare(pricePerNight, room.pricePerNight) == 0 &&
                available == room.available &&
                Objects.equals(id, room.id) &&
                Objects.equals(type, room.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, pricePerNight, available);
    }
}
