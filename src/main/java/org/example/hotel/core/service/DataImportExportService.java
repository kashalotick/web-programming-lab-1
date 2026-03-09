package org.example.hotel.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.hotel.core.dto.*;
import org.example.hotel.core.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataImportExportService {
    private final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public void exportJSON(String filename) {
        var serializable = convertToSerializable();
        try {
            mapper.writeValue(new File(filename), serializable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importJSON(String filename) {
        Entity.clear();
        try {
            var rootNode = mapper.readTree(new File(filename));

            // guests
            var guestClazz = GuestDTO.class.getSimpleName();
            if (rootNode.has(guestClazz)) {
                var guestsNode = rootNode.get(guestClazz);
                for (var node : guestsNode) {
                    GuestDTO dto = mapper.treeToValue(node, GuestDTO.class);
                    Entity.create(Guest.fromDTO(dto));
                }
            }
            // hotels
            var hotelClazz = HotelDTO.class.getSimpleName();
            if (rootNode.has(hotelClazz)) {
                var hotelNode = rootNode.get(hotelClazz);
                for (var node : hotelNode) {
                    HotelDTO dto = mapper.treeToValue(node, HotelDTO.class);
                    Entity.create(Hotel.fromDTO(dto));
                }
            }
            // rooms
            var roomClazz = RoomDTO.class.getSimpleName();
            if (rootNode.has(roomClazz)) {
                var roomNode = rootNode.get(roomClazz);
                for (var node : roomNode) {
                    RoomDTO dto = mapper.treeToValue(node, RoomDTO.class);
                    var hotel = Entity.read(Hotel.class, dto.hotelId);
                    var room = Room.fromDTO(dto, hotel);
                    Entity.create(room);
                    if (hotel == null) {
                        System.err.println("Error: Hotel with ID " + dto.hotelId + " not found for room ID" + dto.id);
                        continue;
                    }
                    hotel.addRoom(room);
                    Entity.update(hotel);
                }
            }

            // reservations
            var reservationClazz = ReservationDTO.class.getSimpleName();
            if (rootNode.has(reservationClazz)) {
                var reservationNode = rootNode.get(reservationClazz);
                for (var node : reservationNode) {
                    ReservationDTO dto = mapper.treeToValue(node, ReservationDTO.class);
                    var room = Entity.read(Room.class, dto.roomId);
                    var guest = Entity.read(Guest.class, dto.guestId);
                    var reservation = Reservation.fromDTO(dto, room, guest);
                    if (guest == null) {
                        System.err.println("Error: Guest with ID " + dto.guestId + " not found for reservation ID" + dto.id);
                        continue;
                    }
                    if (room == null) {
                        System.err.println("Error: Room with ID " + dto.roomId + " not found for reservation ID" + dto.id);
                        continue;
                    }
                    guest.addReservation(reservation);
                    room.addReservation(reservation);
                    Entity.create(reservation);
                    Entity.update(room);
                    Entity.update(guest);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Map<String, List<? extends Serializable>> convertToSerializable() {
        HashMap<String, List<? extends Serializable>> serializable = new HashMap<>();

        var guests = Entity.readAll(Guest.class);
        serializable.put(GuestDTO.class.getSimpleName(), convertEntitiesToSerializable(guests));
        var hotels = Entity.readAll(Hotel.class);
        serializable.put(HotelDTO.class.getSimpleName(), convertEntitiesToSerializable(hotels));
        var rooms = Entity.readAll(Room.class);
        serializable.put(RoomDTO.class.getSimpleName(), convertEntitiesToSerializable(rooms));
        var reservations = Entity.readAll(Reservation.class);
        serializable.put(ReservationDTO.class.getSimpleName(), convertEntitiesToSerializable(reservations));


        return serializable;
    }

    private List<Serializable> convertEntitiesToSerializable(List<? extends Entity> entities) {
        List<Serializable> list = new ArrayList<>();
        entities.forEach(entity -> list.add(entity.toDTO()));
        return list;
    }
}
