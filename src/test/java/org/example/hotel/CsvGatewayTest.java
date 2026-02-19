package org.example.hotel;

import org.example.hotel.io.RoomCsvGateway;
import org.example.hotel.io.VisitorCsvGateway;
import org.example.hotel.model.Room;
import org.example.hotel.model.Visitor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CsvGatewayTest {

    @Test
    void shouldExportRoomsSortedByPriceUsingMockWriter() throws IOException {
        RoomCsvGateway gateway = new RoomCsvGateway();
        Writer writer = mock(Writer.class);
        List<Room> rooms = List.of(
                new Room("R2", "LUX", 3000, true),
                new Room("R1", "STANDARD", 1000, true)
        );

        gateway.exportRooms(writer, rooms, Comparator.comparing(Room::getPricePerNight));

        verify(writer).write("id,type,pricePerNight,available\n");
        verify(writer).write("R1,STANDARD,1000.0,true\n");
        verify(writer).write("R2,LUX,3000.0,true\n");
        verify(writer).flush();
    }

    @Test
    void shouldImportRoomsFromReader() throws IOException {
        RoomCsvGateway gateway = new RoomCsvGateway();
        Reader reader = new StringReader("id,type,pricePerNight,available\nR1,STANDARD,1000.0,true\n");

        List<Room> rooms = gateway.importRooms(reader);

        assertEquals(1, rooms.size());
        assertEquals("R1", rooms.get(0).getId());
    }

    @Test
    void shouldThrowWhenRoomCsvRowIsInvalid() {
        RoomCsvGateway gateway = new RoomCsvGateway();
        Reader reader = new StringReader("id,type,pricePerNight,available\nBROKEN_ROW\n");

        assertThrows(IllegalArgumentException.class, () -> gateway.importRooms(reader));
    }

    @Test
    void shouldExportVisitorsUsingMockWriter() throws IOException {
        VisitorCsvGateway gateway = new VisitorCsvGateway();
        Writer writer = mock(Writer.class);

        gateway.exportVisitors(writer,
                List.of(new Visitor("V1", "Anna", "anna@test.com")),
                Comparator.comparing(Visitor::getFullName));

        verify(writer).write("id,fullName,email\n");
        verify(writer).write("V1,Anna,anna@test.com\n");
        verify(writer).flush();
    }
}
