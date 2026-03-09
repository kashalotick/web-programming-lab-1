package org.example.hotel.core.view;

import java.time.LocalDate;
import java.util.List;

public interface IRoom extends IEntity {
    IHotel getHotel();
    int getLocalId();
    String getType();
    int getPrice();
    List<IReservation> getReservations();

//    int getOccupiedNights(LocalDate from, LocalDate to);
    float getOccupancyRate(LocalDate from, LocalDate to);
    int getRevenue(LocalDate from, LocalDate to);
    boolean getIsAvailable(LocalDate checkIn, LocalDate checkOut);
}
