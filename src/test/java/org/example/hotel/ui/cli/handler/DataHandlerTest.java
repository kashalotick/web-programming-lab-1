package org.example.hotel.ui.cli.handler;

import org.example.hotel.core.service.DataImportExportService;
import org.example.hotel.ui.cli.CLISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataHandlerTest {

    @Mock
    private DataImportExportService dataImportExportService;

    private DataHandler handler;

    @BeforeEach
    void setUp() {
        handler = new DataHandler(dataImportExportService);
    }

    @Test
    void getCommandName_returnsData() {
        assertEquals("data", handler.getCommandName());
    }

    @Test
    void handle_help_printsHelpString() {
        var out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        handler.handle("help");

        assertTrue(out.toString().contains("import"));
        assertTrue(out.toString().contains("export"));
        System.setOut(System.out);
    }

    @Test
    void handle_import_callsImportJSON() {
        handler.handle("import data.json");

        verify(dataImportExportService).importJSON("data.json");
    }

    @Test
    void handle_import_withSpaces_stripsAndCalls() {
        handler.handle("import  myfile.json  ");

        verify(dataImportExportService).importJSON("myfile.json");
    }

    @Test
    void handle_export_callsExportJSON() {
        handler.handle("export backup.json");

        verify(dataImportExportService).exportJSON("backup.json");
    }

    @Test
    void handle_export_withSpaces_stripsAndCalls() {
        handler.handle("export   output.json  ");

        verify(dataImportExportService).exportJSON("output.json");
    }

    @Test
    void handle_unknownSubcommand_throwsCLISyntaxException() {
        assertThrows(CLISyntaxException.class, () -> handler.handle("delete data.json"));
    }

    @Test
    void handle_missingArgs_throwsCLISyntaxException() {
        assertThrows(CLISyntaxException.class, () -> handler.handle("import"));
    }

    @Test
    void handle_import_doesNotCallExport() {
        handler.handle("import file.json");

        verify(dataImportExportService, never()).exportJSON(any());
    }

    @Test
    void handle_export_doesNotCallImport() {
        handler.handle("export file.json");

        verify(dataImportExportService, never()).importJSON(any());
    }
}