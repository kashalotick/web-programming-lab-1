package org.example.hotel.ui.cli.handler;

import org.example.hotel.core.service.HotelService;
import org.example.hotel.core.service.ReservationService;
import org.example.hotel.ui.cli.CLISyntaxException;
import org.example.hotel.ui.cli.DisplayHelper;

import java.time.LocalDate;

public class ReservationHandler implements ICommandHandler {
    public static final String commandName = "reservation";
    private final ReservationService reservationService;

    public ReservationHandler(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Override
    public String getCommandName() {
        return commandName;
    }

    @Override
    public String getHelpString() {
        return """
                reservation <subcommand> <args>
                            available <hotel-id>, <in-date>, <out-date>
                            make <hotel-id>, <in-hotel-room-id>, <guest-id>, <in-date>, <out-date>
                            get <id>
                            cancel <id>
                """;
    }

    @Override
    public void handle(String command) {
        var subcommandSplit = command.split(" ", 2);
        var subcommand = subcommandSplit[0];
        if (subcommand.equals("help")) {
            System.out.println(getHelpString());
        } else {
            if (subcommandSplit.length != 2) {
                throw new CLISyntaxException("Invalid command format. Expected format: reservation <subcommand> <args>");
            }
            var args = subcommandSplit[1];
            switch (subcommand) {
                case "available" -> available(args);
                case "make" -> make(args);
                case "get" -> get(args);
                case "cancel" -> cancel(args);
                default ->
                        throw new CLISyntaxException("Invalid command. Available commands: create, get, list, delete, set-name");
            }
        }
    }

    public void available(String args) {
        var split = args.split(",", 3);
        if (split.length != 3) {
            throw new CLISyntaxException("Invalid command format. Expected format: reservation available <hotel-id>, <in-date>, <out-date>");
        }
        var hotelId = Integer.parseInt(split[0].strip());
        var checkIn = LocalDate.parse(split[1].strip());
        var checkOut = LocalDate.parse(split[2].strip());

        var rooms = reservationService.getAvailableRooms(hotelId, checkIn, checkOut);
        DisplayHelper.printEntityList(rooms);
    }

    public void make(String args) {
        var split = args.split(",", 5);
        if (split.length != 5) {
            throw new CLISyntaxException("Invalid command format. Expected format: make <hotel-id>, <in-hotel-room-id>, <guest-id>, <in-date>, <out-date>");
        }
        var hotelId = Integer.parseInt(split[0].strip());
        var roomId = Integer.parseInt(split[1].strip());
        var guestId = Integer.parseInt(split[2].strip());
        var checkIn = LocalDate.parse(split[3].strip());
        var checkOut = LocalDate.parse(split[4].strip());

        var reservation = reservationService.makeReservation(hotelId, roomId, guestId, checkIn, checkOut);
        DisplayHelper.printGreen("Created reservation: " + reservation);
    }

    public void get(String args) {
        var id = Integer.parseInt(args.strip());

        var reservation = reservationService.getReservation(id);
        DisplayHelper.printDefault(reservation.toString());
    }

    public void cancel(String args) {
        var id = Integer.parseInt(args.strip());

        reservationService.cancelReservation(id);
        DisplayHelper.printGreen("Reservation with id " + id + " canceled");
    }
}
