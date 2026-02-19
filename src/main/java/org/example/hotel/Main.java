package org.example.hotel;

import org.example.hotel.io.RoomCsvGateway;
import org.example.hotel.io.VisitorCsvGateway;
import org.example.hotel.model.Hotel;
import org.example.hotel.service.BookingService;
import org.example.hotel.service.RoomService;
import org.example.hotel.service.VisitorService;
import org.example.hotel.ui.ConsoleMenu;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Hotel hotel = new Hotel("Dnipro Hotel", 100);
        RoomService roomService = new RoomService();
        VisitorService visitorService = new VisitorService();
        BookingService bookingService = new BookingService(hotel, roomService, visitorService);

        ConsoleMenu menu = new ConsoleMenu(
                new Scanner(System.in),
                roomService,
                visitorService,
                bookingService,
                new RoomCsvGateway(),
                new VisitorCsvGateway()
        );
        menu.start();
    }
}
