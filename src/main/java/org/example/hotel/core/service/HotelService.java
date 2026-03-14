package org.example.hotel.core.service;

import org.example.hotel.core.model.Entity;
import org.example.hotel.core.model.Hotel;
import org.example.hotel.core.model.Room;
import org.example.hotel.core.repository.HotelRepository;
import org.example.hotel.core.repository.RoomRepository;
import org.example.hotel.core.view.IHotel;
import org.example.hotel.core.view.IRoom;

import java.util.List;

public class HotelService {
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    public HotelService(HotelRepository hotelRepository, RoomRepository roomRepository) {
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
    }

    public IHotel create(String name) {
        var hotel = new Hotel(name);
        hotelRepository.create(hotel);
        return hotel;
    }

    public IHotel get(int id) {
        return getWritable(id);
    }

    public List<IHotel> getAll() {
        return List.copyOf(hotelRepository.readAllWithRooms(roomRepository));
    }

    public IHotel setName(int id, String name) {
        var hotel = getWritable(id);
        hotel.setName(name);
        hotelRepository.update(hotel);
        return hotel;
    }

    public void delete(int id) {
        var hotel = getWritable(id);
        if (!hotel.getRooms().isEmpty()) {
            throw new IllegalArgumentException("Cannot delete hotel that still has rooms");
        }
        hotelRepository.delete(id);
    }

    public IRoom addRoomToHotel(int hotelId, int localId, String type, int price) {
        var hotel = getWritable(hotelId);
        var room = hotel.addRoom(localId, type, price);
        roomRepository.create(room);
        return room;
    }

    public IRoom getRoomInHotel(int hotelId, int localId) {
        var hotel = getWritable(hotelId);
        var room = hotel.getRoom(localId);
        if (room == null) {
            throw new IllegalArgumentException("Room not found");
        }
        return room;
    }

    public void removeRoomFromHotel(int hotelId, int localId) {
        var hotel = getWritable(hotelId);
        var room = (Room) getRoomInHotel(hotelId, localId);

        if (!room.getReservations().isEmpty()) {
            throw new IllegalStateException("Cannot remove room: it has active reservations");
        }
        hotel.removeRoom(localId);
        roomRepository.delete(room.getId());
    }

    public IRoom setRoomPrice(int hotelId, int localId, int price) {
        var hotel = getWritable(hotelId);
        hotel.setRoomPrice(localId, price);
        var room = hotel.getRoom(localId);
        roomRepository.update(room);
        return room;
    }

    private Hotel getWritable(int id) {
        return hotelRepository.readWithRooms(id, roomRepository)
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found"));
    }
}