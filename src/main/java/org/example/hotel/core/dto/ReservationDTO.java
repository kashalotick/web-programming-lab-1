package org.example.hotel.core.dto;

import java.io.Serializable;

public class ReservationDTO implements Serializable {
    public int id;
    public int guestId;
    public int hotelId;
    public int roomId;
    public String checkIn;
    public String checkOut;
    public int grandTotal;
}
