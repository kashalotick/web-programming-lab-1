package org.example.hotel.ui;

import org.example.hotel.io.RoomCsvGateway;
import org.example.hotel.io.VisitorCsvGateway;
import org.example.hotel.model.Booking;
import org.example.hotel.model.Room;
import org.example.hotel.model.Visitor;
import org.example.hotel.service.BookingService;
import org.example.hotel.service.RoomService;
import org.example.hotel.service.VisitorService;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Scanner;

public class ConsoleMenu {
    private final Scanner scanner;
    private final RoomService roomService;
    private final VisitorService visitorService;
    private final BookingService bookingService;
    private final RoomCsvGateway roomCsvGateway;
    private final VisitorCsvGateway visitorCsvGateway;

    public ConsoleMenu(Scanner scanner,
                       RoomService roomService,
                       VisitorService visitorService,
                       BookingService bookingService,
                       RoomCsvGateway roomCsvGateway,
                       VisitorCsvGateway visitorCsvGateway) {
        this.scanner = scanner;
        this.roomService = roomService;
        this.visitorService = visitorService;
        this.bookingService = bookingService;
        this.roomCsvGateway = roomCsvGateway;
        this.visitorCsvGateway = visitorCsvGateway;
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1" -> createRoom();
                    case "2" -> listRooms();
                    case "3" -> updateRoom();
                    case "4" -> deleteRoom();
                    case "5" -> createVisitor();
                    case "6" -> listVisitors();
                    case "7" -> updateVisitor();
                    case "8" -> deleteVisitor();
                    case "9" -> bookRoom();
                    case "10" -> printStats();
                    case "11" -> exportData();
                    case "12" -> importData();
                    case "0" -> running = false;
                    default -> System.out.println("Невідома команда");
                }
            } catch (Exception ex) {
                System.out.println("Помилка: " + ex.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\n--- Меню готелю ---");
        System.out.println("1. Додати номер");
        System.out.println("2. Показати всі номери");
        System.out.println("3. Оновити номер");
        System.out.println("4. Видалити номер");
        System.out.println("5. Додати відвідувача");
        System.out.println("6. Показати всіх відвідувачів");
        System.out.println("7. Оновити відвідувача");
        System.out.println("8. Видалити відвідувача");
        System.out.println("9. Забронювати номер");
        System.out.println("10. Статистика бронювань");
        System.out.println("11. Експорт у CSV");
        System.out.println("12. Імпорт з CSV");
        System.out.println("0. Вихід");
        System.out.print("Оберіть дію: ");
    }

    private void createRoom() {
        System.out.print("ID номера: ");
        String id = scanner.nextLine();
        System.out.print("Тип: ");
        String type = scanner.nextLine();
        System.out.print("Ціна за ніч: ");
        double price = Double.parseDouble(scanner.nextLine());
        roomService.create(new Room(id, type, price, true));
        System.out.println("Номер додано.");
    }

    private void listRooms() {
        roomService.getAll().forEach(room ->
                System.out.println(room.getId() + " | " + room.getType() + " | " + room.getPricePerNight() + " | available=" + room.isAvailable()));
    }

    private void updateRoom() {
        System.out.print("ID номера для оновлення: ");
        String id = scanner.nextLine();
        System.out.print("Новий тип: ");
        String type = scanner.nextLine();
        System.out.print("Нова ціна за ніч: ");
        double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Доступний (true/false): ");
        boolean available = Boolean.parseBoolean(scanner.nextLine());

        roomService.update(id, new Room(id, type, price, available));
        System.out.println("Номер оновлено.");
    }

    private void deleteRoom() {
        System.out.print("ID номера для видалення: ");
        roomService.delete(scanner.nextLine());
        System.out.println("Номер видалено.");
    }

    private void createVisitor() {
        System.out.print("ID відвідувача: ");
        String id = scanner.nextLine();
        System.out.print("ПІБ: ");
        String fullName = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        visitorService.create(new Visitor(id, fullName, email));
        System.out.println("Відвідувача додано.");
    }

    private void listVisitors() {
        visitorService.getAll().forEach(visitor ->
                System.out.println(visitor.getId() + " | " + visitor.getFullName() + " | " + visitor.getEmail()));
    }

    private void updateVisitor() {
        System.out.print("ID відвідувача для оновлення: ");
        String id = scanner.nextLine();
        System.out.print("Новий ПІБ: ");
        String fullName = scanner.nextLine();
        System.out.print("Новий Email: ");
        String email = scanner.nextLine();

        visitorService.update(id, new Visitor(id, fullName, email));
        System.out.println("Відвідувача оновлено.");
    }

    private void deleteVisitor() {
        System.out.print("ID відвідувача для видалення: ");
        visitorService.delete(scanner.nextLine());
        System.out.println("Відвідувача видалено.");
    }

    private void bookRoom() {
        System.out.print("ID номера: ");
        String roomId = scanner.nextLine();
        System.out.print("ID відвідувача: ");
        String visitorId = scanner.nextLine();
        System.out.print("Дата заїзду (YYYY-MM-DD): ");
        LocalDate checkIn = LocalDate.parse(scanner.nextLine());
        System.out.print("Дата виїзду (YYYY-MM-DD): ");
        LocalDate checkOut = LocalDate.parse(scanner.nextLine());

        Booking booking = bookingService.bookRoom(roomId, visitorId, checkIn, checkOut);
        System.out.println("Бронювання створено. Сума: " + booking.getTotalPrice());
    }

    private void printStats() {
        System.out.println(bookingService.getBookingStats());
    }

    private void exportData() throws IOException {
        roomCsvGateway.exportRooms(Path.of("rooms.csv"), roomService.getAll(), Comparator.comparing(Room::getPricePerNight));
        visitorCsvGateway.exportVisitors(Path.of("visitors.csv"), visitorService.getAll(), Comparator.comparing(Visitor::getFullName));
        System.out.println("Дані експортовано у rooms.csv та visitors.csv");
    }

    private void importData() throws IOException {
        for (Room room : roomCsvGateway.importRooms(Path.of("rooms.csv"))) {
            try {
                roomService.create(room);
            } catch (IllegalArgumentException ignored) {
            }
        }
        for (Visitor visitor : visitorCsvGateway.importVisitors(Path.of("visitors.csv"))) {
            try {
                visitorService.create(visitor);
            } catch (IllegalArgumentException ignored) {
            }
        }
        System.out.println("Дані імпортовано з rooms.csv та visitors.csv");
    }
}
