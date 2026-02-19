package org.example.hotel.core.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;

public class Room extends Entity {
    private LinkedList<Reservation> reservations = new LinkedList<>();

    public String type;
    public int price;

    public Room(String type, int price) {
        this.type = type;
        this.price = price;
    }


    public int calculatePrice(LocalDate from, LocalDate to) {
        var nights = to.getDayOfYear() - from.getDayOfYear();
        return nights * price;
    }

}
