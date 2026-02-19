package org.example.hotel.io;

import org.example.hotel.model.Room;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RoomCsvGateway {

    public void exportRooms(Path filePath, List<Room> rooms, Comparator<Room> comparator) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            exportRooms(writer, rooms, comparator);
        }
    }

    public void exportRooms(Writer writer, List<Room> rooms, Comparator<Room> comparator) throws IOException {
        List<Room> copy = new ArrayList<>(rooms);
        copy.sort(comparator);

        writer.write("id,type,pricePerNight,available\n");
        for (Room room : copy) {
            writer.write(room.getId() + "," + room.getType() + "," + room.getPricePerNight() + "," + room.isAvailable() + "\n");
        }
        writer.flush();
    }

    public List<Room> importRooms(Path filePath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            return importRooms(reader);
        }
    }

    public List<Room> importRooms(Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        List<Room> result = new ArrayList<>();
        String line;
        boolean isHeader = true;

        while ((line = bufferedReader.readLine()) != null) {
            if (isHeader) {
                isHeader = false;
                continue;
            }
            String[] parts = line.split(",");
            if (parts.length != 4) {
                throw new IllegalArgumentException("Некоректний CSV рядок: " + line);
            }
            result.add(new Room(
                    parts[0],
                    parts[1],
                    Double.parseDouble(parts[2]),
                    Boolean.parseBoolean(parts[3])
            ));
        }
        return result;
    }
}
