package org.example.hotel.ui.cli.handler;

import org.example.hotel.core.service.GuestService;
import org.example.hotel.core.view.IGuest;
import org.example.hotel.ui.cli.CLISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuestHandlerTest {

    @Mock
    private GuestService guestService;

    private GuestHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GuestHandler(guestService);
    }

    @Test
    void getCommandName_returnsGuest() {
        assertEquals("guest", handler.getCommandName());
    }

    // --- create ---

    @Test
    void handle_create_callsGuestServiceCreate() {
        var mockGuest = mock(IGuest.class);
        when(guestService.create("John Doe")).thenReturn( mockGuest);

        handler.handle("create John Doe");

        verify(guestService).create("John Doe");
    }

    @Test
    void handle_create_stripsWhitespace() {
        var mockGuest = mock(IGuest.class);
        when(guestService.create("Alice")).thenReturn( mockGuest);

        handler.handle("create   Alice  ");

        verify(guestService).create("Alice");
    }

    // --- get ---

    @Test
    void handle_get_callsGuestServiceGet() {
        var mockGuest = mock(IGuest.class);
        when(guestService.get(42)).thenReturn( mockGuest);

        handler.handle("get 42");

        verify(guestService).get(42);
    }

    @Test
    void handle_get_invalidId_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> handler.handle("get abc"));
    }

    // --- list ---

    @Test
    void handle_list_callsGetAll() {
        when(guestService.getAll()).thenReturn(List.of());

        handler.handle("list");

        verify(guestService).getAll();
    }

    // --- delete ---

    @Test
    void handle_delete_callsGuestServiceDelete() {
        handler.handle("delete 7");

        verify(guestService).delete(7);
    }

    @Test
    void handle_delete_invalidId_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> handler.handle("delete xyz"));
    }

    // --- set-name ---

    @Test
    void handle_setName_callsGuestServiceSetName() {
        var mockGuest = mock(IGuest.class);
        when(guestService.setName(1, "Bob")).thenReturn(mockGuest);

        handler.handle("set-name 1 Bob");

        verify(guestService).setName(1, "Bob");
    }

    @Test
    void handle_setName_missingName_throwsCLISyntaxException() {
        assertThrows(CLISyntaxException.class, () -> handler.handle("set-name 1"));
    }

    @Test
    void handle_setName_invalidId_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> handler.handle("set-name abc Bob"));
    }

    // --- error cases ---

    @Test
    void handle_unknownSubcommand_throwsCLISyntaxException() {
        assertThrows(CLISyntaxException.class, () -> handler.handle("update 1 name"));
    }

    @Test
    void handle_missingArgs_throwsCLISyntaxException() {
        assertThrows(CLISyntaxException.class, () -> handler.handle("create"));
    }

    @Test
    void handle_help_doesNotCallService() {
        handler.handle("help");

        verifyNoInteractions(guestService);
    }
}