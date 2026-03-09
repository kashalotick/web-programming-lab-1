package org.example.hotel.ui.cli.handler;

import org.example.hotel.core.service.HotelService;
import org.example.hotel.ui.cli.CLISyntaxException;
import org.example.hotel.ui.cli.DisplayHelper;

import java.time.LocalDate;

public class HotelHandler implements ICommandHandler {
    public static final String commandName = "hotel";
    private final HotelService hotelService;

    public HotelHandler(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @Override
    public String getCommandName() {
        return commandName;
    }

    @Override
    public String getHelpString() {
        return """
                hotel <subcommand> <args>
                      create <name>
                      get <id>
                      list
                      delete <id>
                      set-name <id> <name>
                      <hotel-id> <subcommand> <args>
                                  occupancy <date-from>, <date-in>
                                  revenue <date-from>, <date-in>
                      <hotel-id> room <subcommand> <args>
                                       add <in-hotel-room-id>, <type>, <price>
                                       get <in-hotel-room-id>
                                       delete <in-hotel-room-id>
                                       set-price <id> <price>
                                       <in-hotel-room-id> occupancy <date-from>, <date-in>
                                       <in-hotel-room-id> revenue <date-from>, <date-in>
                """;
    }

    @Override
    public void handle(String command) {
        if (command == null || command.isBlank()) {
            throw new CLISyntaxException("Command cannot be empty. Type 'hotel help' for options.");
        }

        var split = command.split(" ", 2);
        var subcommand = split[0];
        var args = split.length == 2 ? split[1] : "";

        // Базові команди без аргументів
        if (subcommand.equals("help")) {
            System.out.println(getHelpString());
            return;
        } else if (subcommand.equals("list")) {
            list();
            return;
        }

        // Базові команди з аргументами
        switch (subcommand) {
            case "create" -> {
                create(args);
                return;
            }
            case "get" -> {
                get(args);
                return;
            }
            case "delete" -> {
                delete(args);
                return;
            }
            case "set-name" -> {
                setName(args);
                return;
            }
        }

        // Якщо це не базова команда, то першим аргументом має бути ID готелю
        int hotelId;
        try {
            hotelId = Integer.parseInt(subcommand);
        } catch (NumberFormatException e) {
            throw new CLISyntaxException("Invalid command or hotel ID. Expected integer but got: " + subcommand);
        }

        handleHotelSpecificCommand(hotelId, args);
    }

    // --- Обробка команд для конкретного готелю (<hotel-id> ...) ---
    private void handleHotelSpecificCommand(int hotelId, String commandArgs) {
        var split = commandArgs.split(" ", 2);
        if (split.length == 0 || split[0].isBlank()) {
            throw new CLISyntaxException("Missing subcommand for hotel " + hotelId);
        }
        var action = split[0];
        var args = split.length == 2 ? split[1] : "";

        switch (action) {
            case "occupancy" -> hotelOccupancy(hotelId, args);
            case "revenue" -> hotelRevenue(hotelId, args);
            case "room" -> handleRoomCommand(hotelId, args);
            default -> throw new CLISyntaxException("Invalid hotel command: " + action);
        }
    }

    // --- Обробка команд для кімнат (<hotel-id> room ...) ---
    private void handleRoomCommand(int hotelId, String commandArgs) {
        var split = commandArgs.split(" ", 2);
        if (split.length == 0 || split[0].isBlank()) {
            throw new CLISyntaxException("Missing room subcommand for hotel " + hotelId);
        }
        var action = split[0];
        var args = split.length == 2 ? split[1] : "";

        switch (action) {
            case "add" -> roomAdd(hotelId, args);
            case "get" -> roomGet(hotelId, args);
            case "delete" -> roomDelete(hotelId, args);
            case "set-price" -> roomSetPrice(hotelId, args);
            default -> {
                // Якщо не add/get/delete, то це може бути <in-hotel-room-id>
                int roomId;
                try {
                    roomId = Integer.parseInt(action);
                } catch (NumberFormatException e) {
                    throw new CLISyntaxException("Invalid room command or room ID: " + action);
                }
                handleRoomSpecificCommand(hotelId, roomId, args);
            }
        }
    }

    // --- Обробка аналітики конкретної кімнати (<hotel-id> room <room-id> ...) ---
    private void handleRoomSpecificCommand(int hotelId, int roomId, String commandArgs) {
        var split = commandArgs.split(" ", 2);
        if (split.length == 0 || split[0].isBlank()) {
            throw new CLISyntaxException("Missing action for room " + roomId);
        }
        var action = split[0];
        var args = split.length == 2 ? split[1] : "";

        switch (action) {
            case "occupancy" -> roomOccupancy(hotelId, roomId, args);
            case "revenue" -> roomRevenue(hotelId, roomId, args);
            default -> throw new CLISyntaxException("Invalid room specific command: " + action);
        }
    }

    // =========================================================================
    // БАЗОВІ МЕТОДИ ГОТЕЛЮ
    // =========================================================================

    private void create(String args) {
        var name = args.strip();
        if (name.isEmpty()) throw new CLISyntaxException("Hotel name cannot be empty");

        var hotel = hotelService.create(name);
        DisplayHelper.printGreen("Created hotel: " + hotel);
    }

    private void get(String args) {
        var id = Integer.parseInt(args.strip());


        var hotel = hotelService.get(id);
        DisplayHelper.printDefault(hotel.toString());
    }

    private void list() {
        var hotels = hotelService.getAll();
        DisplayHelper.printEntityList(hotels);
    }

    private void delete(String args) {
        var id = Integer.parseInt(args.strip());

        hotelService.delete(id);
        DisplayHelper.printGreen("Deleted hotel with id: " + id);
    }

    private void setName(String args) {
        var split = args.split(" ", 2);
        if (split.length != 2) {
            throw new CLISyntaxException("Invalid command format. Expected: hotel set-name <id> <name>");
        }
        var id = Integer.parseInt(split[0].strip());
        var name = split[1].strip();

        var guest = hotelService.setName(id, name);
        DisplayHelper.printGreen("Updated hotel: " + guest);
    }

    // =========================================================================
    // АНАЛІТИКА ГОТЕЛЮ
    // =========================================================================

    private void hotelOccupancy(int hotelId, String args) {
        String[] dates = parseDates(args, "hotel <hotel-id> occupancy <date-from>, <date-in>");
        var dateFrom = LocalDate.parse(dates[0]);
        var dateIn = LocalDate.parse(dates[1]);

        var hotel = hotelService.get(hotelId);
        var occupancy = hotel.getOccupancyRate(dateFrom, dateIn);
        DisplayHelper.printDefault("Occupancy rate in hotel #" + hotel.getId() + ": " + occupancy);

    }

    private void hotelRevenue(int hotelId, String args) {
        String[] dates = parseDates(args, "hotel <hotel-id> revenue <date-from>, <date-in>");
        var dateFrom = LocalDate.parse(dates[0]);
        var dateIn = LocalDate.parse(dates[1]);

        var hotel = hotelService.get(hotelId);
        var revenue = hotel.getRevenue(dateFrom, dateIn);
        DisplayHelper.printDefault("Revenue in hotel #" + hotel.getId() + ": " + revenue);
    }

//    private void hotelAvailable(int hotelId, String args) {
//        String[] dates = parseDates(args, "hotel <hotel-id> available <date-from>, <date-in>");
//        var dateFrom = LocalDate.parse(dates[0]);
//        var dateIn = LocalDate.parse(dates[1]);
//
//        var hotel = hotelService.get(hotelId);
//        var occupancy = hotel.getOccupancyRate(dateFrom, dateIn);
//        DisplayHelper.printDefault("Occupancy rate in" + hotel.getId() + ": " + occupancy);    }

    // =========================================================================
    // CRUD КІМНАТ
    // =========================================================================

    private void roomAdd(int hotelId, String args) {
        var split = args.split(",");
        if (split.length != 3) {
            throw new CLISyntaxException("Invalid format. Expected: hotel <hotel-id> room add <room-id>, <type>, <price>");
        }
        var roomId = Integer.parseInt(split[0].strip());
        var type = split[1].strip();
        var price = Integer.parseInt(split[2].strip());

        var room = hotelService.addRoomToHotel(hotelId, roomId, type, price);
        DisplayHelper.printGreen("Created room in hotel #" + hotelId + ": " + room);
    }

    private void roomGet(int hotelId, String args) {
        var roomId = Integer.parseInt(args.strip());

        var room = hotelService.getRoomInHotel(hotelId, roomId);
        DisplayHelper.printDefault(room.toString());

    }

    private void roomDelete(int hotelId, String args) {
        var roomId = Integer.parseInt(args.strip());

        hotelService.removeRoomFromHotel(hotelId, roomId);
        DisplayHelper.printGreen("Deleted room in hotel #" + hotelId + ": " + roomId);
    }

    private void roomSetPrice(int hotelId, String args) {
        var split = args.split(",");
        if (split.length != 2) {
            throw new CLISyntaxException("Invalid format. Expected: hotel <hotel-id> room add <room-id>, <type>, <price>");
        }
        var roomId = Integer.parseInt(split[0].strip());
        var price = Integer.parseInt(split[1].strip());

        var room = hotelService.setRoomPrice(hotelId, roomId, price);
        DisplayHelper.printGreen("Created room in hotel #" + hotelId + ": " + room);
    }
    // =========================================================================
    // АНАЛІТИКА КІМНАТ
    // =========================================================================

    private void roomOccupancy(int hotelId, int roomId, String args) {
        String[] dates = parseDates(args, "hotel <hotel-id> room <room-id> occupancy <date-from>, <date-in>");
        var dateFrom = LocalDate.parse(dates[0]);
        var dateIn = LocalDate.parse(dates[1]);

        var hotel = hotelService.get(hotelId);
        var room = hotel.getRoom(roomId);
        var occupancy = room.getOccupancyRate(dateFrom, dateIn);
        DisplayHelper.printDefault("Occupancy rate in hotel #" + hotel.getId() + "in room #" + room.getLocalId() + ": " + occupancy);
    }

    private void roomRevenue(int hotelId, int roomId, String args) {
        String[] dates = parseDates(args, "hotel <hotel-id> room <room-id> revenue <date-from>, <date-in>");
        var dateFrom = LocalDate.parse(dates[0]);
        var dateIn = LocalDate.parse(dates[1]);

        var hotel = hotelService.get(hotelId);
        var room = hotel.getRoom(roomId);
        var revenue = room.getRevenue(dateFrom, dateIn);
        DisplayHelper.printDefault("Revenue in hotel #" + hotel.getId() + "in room #" + room.getLocalId() + ": " + revenue);
    }

    // =========================================================================
    // УТИЛІТИ
    // =========================================================================

    private String[] parseDates(String args, String expectedFormat) {
        var split = args.split(",");
        if (split.length != 2) {
            throw new CLISyntaxException("Invalid dates format. Expected: " + expectedFormat);
        }
        return new String[]{split[0].strip(), split[1].strip()};
    }
}
