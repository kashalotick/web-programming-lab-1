package org.example.hotel.core.service;

import org.example.hotel.core.model.Entity;
import org.example.hotel.core.model.Guest;
import org.example.hotel.core.model.Hotel;
import org.example.hotel.core.model.Room;
import org.example.hotel.core.view.IHotel;
import org.example.hotel.core.view.IRoom;

import java.util.List;

public class HotelService {
    public static IHotel create(String name) {
        var hotel = new Hotel(name);
        Entity.create(hotel);
        return hotel;
    }

    public static IHotel get(int id) {
        return getWritable(id);
    }


    public static List<IHotel> getAll() {
        var hotels = Entity.readAll(Hotel.class);
        if (hotels == null) {
            throw new IllegalArgumentException("No hotels found");
        }
        return List.copyOf(hotels);
    }

    public static Hotel setName(int id, String name) {
        var hotel = getWritable(id);
        hotel.setName(name);
        Entity.update(hotel);
        return hotel;
    }


    public static void delete(int id) {
        var hotel = get(id);
        if (!hotel.getRooms().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete guest with reservations");
        }
        Entity.delete(Hotel.class, id);
    }


    public static IRoom addRoomToHotel(int hotelId, int localId, String type, int price) {
        var hotel = getWritable(hotelId);
        var room = hotel.addRoom(localId, type, price);
        Entity.create(room);
        Entity.update(hotel);
        return room;
    }

    public static IRoom getRoomInHotel(int hotelId, int roomId) {
        var hotel = getWritable(hotelId);
        var room = hotel.getRoom(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Room not found");
        }
        if (!room.getReservations().isEmpty()) {
            throw new IllegalArgumentException("Room is not empty");
        }
        return room;
    }

    public static void removeRoomFromHotel(int hotelId, int roomId) {
        var hotel = getWritable(hotelId);
        var room = getRoomInHotel(hotelId, roomId);
        hotel.removeRoom(roomId);
        Entity.update(hotel);
        Entity.delete(Room.class, room.getId());
    }

    private static Hotel getWritable(int id) {
        var hotel = Entity.read(Hotel.class, id);
        if (hotel == null) {
            throw new IllegalArgumentException("Hotel not found");
        }
        return hotel;
    }
}
