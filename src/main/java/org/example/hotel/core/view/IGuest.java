package org.example.hotel.core.view;

import java.util.List;

public interface IGuest extends IEntity {
    String getName();
    List<IReservation> getReservations();
}
