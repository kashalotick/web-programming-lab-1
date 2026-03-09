package org.example.hotel.core.service;

import org.example.hotel.core.model.Entity;
import org.example.hotel.core.model.Guest;
import org.example.hotel.core.model.Hotel;
import org.example.hotel.core.model.Room;
import org.example.hotel.core.view.IHotel;
import org.example.hotel.core.view.IRoom;

import java.util.List;

public class HotelService {
    public IHotel create(String name) {
        var hotel = new Hotel(name);
        Entity.create(hotel);
        return hotel;
    }

    public IHotel get(int id) {
        return getWritable(id);
    }


    public List<IHotel> getAll() {
        var hotels = Entity.readAll(Hotel.class);
        return List.copyOf(hotels);
    }

    public IHotel setName(int id, String name) {
        var hotel = getWritable(id);
        hotel.setName(name);
        Entity.update(hotel);
        return hotel;
    }


    public void delete(int id) {
        var hotel = get(id);
        if (!hotel.getRooms().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete hotel that still has rooms");
        }
        Entity.delete(Hotel.class, id);
    }


    public IRoom addRoomToHotel(int hotelId, int localId, String type, int price) {
        var hotel = getWritable(hotelId);
        var room = hotel.addRoom(localId, type, price);
        Entity.create(room);
        Entity.update(hotel);
        return room;
    }

    public IRoom getRoomInHotel(int hotelId, int roomId) {
        var hotel = getWritable(hotelId);
        var room = hotel.getRoom(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Room not found");
        }
        return room;
    }

    public void removeRoomFromHotel(int hotelId, int roomId) {
        var hotel = getWritable(hotelId);
        var room = getRoomInHotel(hotelId, roomId);

        if (!room.getReservations().isEmpty()) {
            throw new IllegalStateException("Cannot remove room: it has active reservations");
        }
        hotel.removeRoom(roomId);
        Entity.update(hotel);
        Entity.delete(Room.class, room.getId());
    }

    public IRoom setRoomPrice(int hotelId, int roomId, int price) {
        var hotel = getWritable(hotelId);
        var room = hotel.getRoom(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Room not found");
        }
        room.setPrice(price);
        Entity.update(room);
        return room;
    }

    private Hotel getWritable(int id) {
        var hotel = Entity.read(Hotel.class, id);
        if (hotel == null) {
            throw new IllegalArgumentException("Hotel not found");
        }
        return hotel;
    }
}
