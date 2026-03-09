package org.example.hotel.ui.cli.handler;

import org.example.hotel.core.service.HotelService;
import org.example.hotel.core.view.IHotel;
import org.example.hotel.core.view.IRoom;
import org.example.hotel.ui.cli.CLISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelHandlerTest {

    @Mock
    private HotelService hotelService;

    @Mock
    private IHotel mockHotel;

    @Mock
    private IRoom mockRoom;

    private HotelHandler handler;

    private static final LocalDate DATE_FROM = LocalDate.of(2025, 1, 1);
    private static final LocalDate DATE_TO   = LocalDate.of(2025, 1, 31);

    @BeforeEach
    void setUp() {
        handler = new HotelHandler(hotelService);
    }

    // =========================================================================
    // getCommandName
    // =========================================================================

    @Test
    void getCommandName_returnsHotel() {
        assertEquals("hotel", handler.getCommandName());
    }

    // =========================================================================
    // Базові команди
    // =========================================================================

    @Test
    void handle_help_doesNotCallService() {
        handler.handle("help");
        verifyNoInteractions(hotelService);
    }

    @Test
    void handle_list_callsGetAll() {
        when(hotelService.getAll()).thenReturn(List.of());
        handler.handle("list");
        verify(hotelService).getAll();
    }

    @Test
    void handle_create_callsServiceCreate() {
        when(hotelService.create("Grand")).thenReturn(mockHotel);
        handler.handle("create Grand");
        verify(hotelService).create("Grand");
    }

    @Test
    void handle_create_emptyName_throwsCLISyntaxException() {
        assertThrows(CLISyntaxException.class, () -> handler.handle("create   "));
    }

    @Test
    void handle_get_callsServiceGet() {
        when(hotelService.get(5)).thenReturn(mockHotel);
        when(mockHotel.toString()).thenReturn("Hotel#5");
        handler.handle("get 5");
        verify(hotelService).get(5);
    }

    @Test
    void handle_get_invalidId_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> handler.handle("get abc"));
    }

    @Test
    void handle_delete_callsServiceDelete() {
        handler.handle("delete 3");
        verify(hotelService).delete(3);
    }

    @Test
    void handle_delete_invalidId_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> handler.handle("delete abc"));
    }

    @Test
    void handle_setName_callsServiceSetName() {
        when(hotelService.setName(2, "Ritz")).thenReturn(mockHotel);
        handler.handle("set-name 2 Ritz");
        verify(hotelService).setName(2, "Ritz");
    }

    @Test
    void handle_setName_missingName_throwsCLISyntaxException() {
        assertThrows(CLISyntaxException.class, () -> handler.handle("set-name 2"));
    }

    @Test
    void handle_setName_invalidId_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> handler.handle("set-name abc Ritz"));
    }

    // =========================================================================
    // Невалідні команди верхнього рівня
    // =========================================================================

    @Test
    void handle_nullCommand_throwsCLISyntaxException() {
        assertThrows(CLISyntaxException.class, () -> handler.handle(null));
    }

    @Test
    void handle_blankCommand_throwsCLISyntaxException() {
        assertThrows(CLISyntaxException.class, () -> handler.handle("   "));
    }

    @Test
    void handle_unknownTextSubcommand_throwsCLISyntaxException() {
        assertThrows(CLISyntaxException.class, () -> handler.handle("archive 1"));
    }

    // =========================================================================
    // Аналітика готелю: <hotel-id> occupancy / revenue
    // =========================================================================

    @Test
    void handle_hotelOccupancy_callsGetOccupancyRate() {
        when(hotelService.get(1)).thenReturn(mockHotel);
        when(mockHotel.getId()).thenReturn(1);
        when(mockHotel.getOccupancyRate(DATE_FROM, DATE_TO)).thenReturn(0.75f);

        handler.handle("1 occupancy 2025-01-01, 2025-01-31");

        verify(mockHotel).getOccupancyRate(DATE_FROM, DATE_TO);
    }

    @Test
    void handle_hotelRevenue_callsGetRevenue() {
        when(hotelService.get(1)).thenReturn(mockHotel);
        when(mockHotel.getId()).thenReturn(1);
        when(mockHotel.getRevenue(DATE_FROM, DATE_TO)).thenReturn(5000);

        handler.handle("1 revenue 2025-01-01, 2025-01-31");

        verify(mockHotel).getRevenue(DATE_FROM, DATE_TO);
    }

    @Test
    void handle_hotelOccupancy_missingDate_throwsCLISyntaxException() {
        assertThrows(CLISyntaxException.class, () -> handler.handle("1 occupancy 2025-01-01"));
    }

    @Test
    void handle_hotelOccupancy_invalidDate_throwsException() {
        assertThrows(Exception.class, () -> handler.handle("1 occupancy not-a-date, 2025-01-31"));
    }

    @Test
    void handle_hotelSpecificCommand_unknownAction_throwsCLISyntaxException() {
        assertThrows(CLISyntaxException.class, () -> handler.handle("1 stats 2025-01-01, 2025-01-31"));
    }

    // =========================================================================
    // Кімнати: <hotel-id> room add / get / delete / set-price
    // =========================================================================

    @Test
    void handle_roomAdd_callsAddRoomToHotel() {
        when(hotelService.addRoomToHotel(1, 101, "SINGLE", 200)).thenReturn(mockRoom);

        handler.handle("1 room add 101, SINGLE, 200");

        verify(hotelService).addRoomToHotel(1, 101, "SINGLE", 200);
    }

    @Test
    void handle_roomAdd_missingArgs_throwsCLISyntaxException() {
        assertThrows(CLISyntaxException.class, () -> handler.handle("1 room add 101, SINGLE"));
    }

    @Test
    void handle_roomAdd_invalidRoomId_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> handler.handle("1 room add abc, SINGLE, 200"));
    }

    @Test
    void handle_roomGet_callsGetRoomInHotel() {
        when(hotelService.getRoomInHotel(1, 101)).thenReturn(mockRoom);
        when(mockRoom.toString()).thenReturn("Room#101");

        handler.handle("1 room get 101");

        verify(hotelService).getRoomInHotel(1, 101);
    }

    @Test
    void handle_roomGet_invalidId_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> handler.handle("1 room get abc"));
    }

    @Test
    void handle_roomDelete_callsRemoveRoomFromHotel() {
        handler.handle("1 room delete 101");

        verify(hotelService).removeRoomFromHotel(1, 101);
    }

    @Test
    void handle_roomDelete_invalidId_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> handler.handle("1 room delete abc"));
    }

    @Test
    void handle_roomSetPrice_callsSetRoomPrice() {
        when(hotelService.setRoomPrice(1, 101, 350)).thenReturn(mockRoom);

        handler.handle("1 room set-price 101, 350");

        verify(hotelService).setRoomPrice(1, 101, 350);
    }

    @Test
    void handle_roomSetPrice_missingPrice_throwsCLISyntaxException() {
        assertThrows(CLISyntaxException.class, () -> handler.handle("1 room set-price 101"));
    }

    @Test
    void handle_roomCommand_unknownAction_throwsCLISyntaxException() {
        assertThrows(CLISyntaxException.class, () -> handler.handle("1 room list"));
    }

    // =========================================================================
    // Аналітика кімнати: <hotel-id> room <room-id> occupancy / revenue
    // =========================================================================

    @Test
    void handle_roomOccupancy_callsGetOccupancyRate() {
        when(hotelService.get(1)).thenReturn(mockHotel);
        when(mockHotel.getId()).thenReturn(1);
        when(mockHotel.getRoom(101)).thenReturn(mockRoom);
        when(mockRoom.getLocalId()).thenReturn(101);
        when(mockRoom.getOccupancyRate(DATE_FROM, DATE_TO)).thenReturn(0.5f);

        handler.handle("1 room 101 occupancy 2025-01-01, 2025-01-31");

        verify(mockRoom).getOccupancyRate(DATE_FROM, DATE_TO);
    }

    @Test
    void handle_roomRevenue_callsGetRevenue() {
        when(hotelService.get(1)).thenReturn(mockHotel);
        when(mockHotel.getId()).thenReturn(1);
        when(mockHotel.getRoom(101)).thenReturn(mockRoom);
        when(mockRoom.getLocalId()).thenReturn(101);
        when(mockRoom.getRevenue(DATE_FROM, DATE_TO)).thenReturn(1500);

        handler.handle("1 room 101 revenue 2025-01-01, 2025-01-31");

        verify(mockRoom).getRevenue(DATE_FROM, DATE_TO);
    }

    @Test
    void handle_roomOccupancy_missingDate_throwsCLISyntaxException() {
        assertThrows(CLISyntaxException.class, () -> handler.handle("1 room 101 occupancy 2025-01-01"));
    }

    @Test
    void handle_roomSpecificCommand_unknownAction_throwsCLISyntaxException() {
        assertThrows(CLISyntaxException.class, () -> handler.handle("1 room 101 stats 2025-01-01, 2025-01-31"));
    }

    @Test
    void handle_roomOccupancy_invalidDate_throwsException() {
        assertThrows(Exception.class, () -> handler.handle("1 room 101 occupancy bad-date, 2025-01-31"));
    }
}