package org.example.hotel.core.model;

import java.util.ArrayList;

public class Guest extends Entity {

    public String name;
    public ArrayList<Reservation> reservations = new ArrayList<>();

    public Guest(String name) {
        this.name = name;
    }
}
