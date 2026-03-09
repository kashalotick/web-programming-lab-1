package org.example.hotel.ui.cli.handler;

import org.example.hotel.core.service.ReservationService;
import org.example.hotel.core.view.IReservation;
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
class ReservationHandlerTest {

    @Mock
    private ReservationService reservationService;

    private ReservationHandler handler;

    private static final LocalDate CHECK_IN  = LocalDate.of(2025, 6, 1);
    private static final LocalDate CHECK_OUT = LocalDate.of(2025, 6, 7);

    @BeforeEach
    void setUp() {
        handler = new ReservationHandler(reservationService);
    }

    @Test
    void getCommandName_returnsReservation() {
        assertEquals("reservation", handler.getCommandName());
    }

    // --- available ---

    @Test
    void handle_available_callsGetAvailableRooms() {
        when(reservationService.getAvailableRooms(1, CHECK_IN, CHECK_OUT)).thenReturn(List.of());

        handler.handle("available 1, 2025-06-01, 2025-06-07");

        verify(reservationService).getAvailableRooms(1, CHECK_IN, CHECK_OUT);
    }

    @Test
    void handle_available_missingArgs_throwsCLISyntaxException() {
        assertThrows(CLISyntaxException.class, () -> handler.handle("available 1, 2025-06-01"));
    }

    @Test
    void handle_available_invalidDate_throwsException() {
        assertThrows(Exception.class, () -> handler.handle("available 1, not-a-date, 2025-06-07"));
    }

    @Test
    void handle_available_invalidHotelId_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () ->
                handler.handle("available abc, 2025-06-01, 2025-06-07"));
    }

    // --- make ---

    @Test
    void handle_make_callsMakeReservation() {
        var mockReservation = mock(IReservation.class);
        when(reservationService.makeReservation(1, 101, 5, CHECK_IN, CHECK_OUT))
                .thenReturn( mockReservation);

        handler.handle("make 1, 101, 5, 2025-06-01, 2025-06-07");

        verify(reservationService).makeReservation(1, 101, 5, CHECK_IN, CHECK_OUT);
    }

    @Test
    void handle_make_missingArgs_throwsCLISyntaxException() {
        assertThrows(CLISyntaxException.class, () ->
                handler.handle("make 1, 101, 5, 2025-06-01"));
    }

    @Test
    void handle_make_invalidRoomId_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () ->
                handler.handle("make 1, abc, 5, 2025-06-01, 2025-06-07"));
    }

    // --- get ---

    @Test
    void handle_get_callsGetReservation() {
        var mockReservation = mock(IReservation.class);
        when(mockReservation.toString()).thenReturn("Reservation#99");
        when(reservationService.getReservation(99)).thenReturn( mockReservation);

        handler.handle("get 99");

        verify(reservationService).getReservation(99);
    }

    @Test
    void handle_get_invalidId_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> handler.handle("get xyz"));
    }

    // --- cancel ---

    @Test
    void handle_cancel_callsCancelReservation() {
        handler.handle("cancel 12");

        verify(reservationService).cancelReservation(12);
    }

    @Test
    void handle_cancel_invalidId_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> handler.handle("cancel abc"));
    }

    // --- error cases ---

    @Test
    void handle_unknownSubcommand_throwsCLISyntaxException() {
        assertThrows(CLISyntaxException.class, () -> handler.handle("delete 1"));
    }

    @Test
    void handle_missingSubcommandArgs_throwsCLISyntaxException() {
        assertThrows(CLISyntaxException.class, () -> handler.handle("available"));
    }

    @Test
    void handle_help_doesNotCallService() {
        handler.handle("help");

        verifyNoInteractions(reservationService);
    }
}