package org.example.hotel.core.view;

import java.time.LocalDate;

public interface IReservation extends IEntity {
    IGuest getGuest();
    IHotel getHotel();
    IRoom getRoom();
    LocalDate getCheckIn();
    LocalDate getCheckOut();
    int getGrandTotal();
    int getDuration();
}
