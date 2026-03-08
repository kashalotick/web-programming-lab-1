package org.example.hotel.core.view;

import java.time.LocalDate;
import java.util.List;

public interface IHotel extends IEntity {
    List<IRoom> getRooms();
    String getName();

    float getOccupancyRate(LocalDate from, LocalDate to);
    int getRevenue(LocalDate from, LocalDate to);
    List<IRoom> findAvailableRooms(LocalDate checkIn, LocalDate checkOut);
    IRoom getRoom(int localId);

}
