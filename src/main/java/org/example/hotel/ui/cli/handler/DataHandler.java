package org.example.hotel.ui.cli.handler;

import org.example.hotel.core.service.DataImportExportService;
import org.example.hotel.core.service.GuestService;
import org.example.hotel.ui.cli.CLISyntaxException;
import org.example.hotel.ui.cli.DisplayHelper;

public class DataHandler implements ICommandHandler {

    public static final String commandName = "data";
    private final DataImportExportService dataImportExportService;

    public DataHandler(DataImportExportService dataImportExportService) {
        this.dataImportExportService = dataImportExportService;
    }

    @Override
    public String getCommandName() {
        return commandName;
    }

    @Override
    public String getHelpString() {
        return """
                data <subcommand> <args>
                     import <filename>
                     export <filename>
                """;
    }

    @Override
    public void handle(String command) {
        var subcommandSplit = command.split(" ", 2);
        var subcommand = subcommandSplit[0];
        if (subcommand.equals("help")) {
            System.out.println(getHelpString());
        }
        else {
            if (subcommandSplit.length != 2) {
                throw new CLISyntaxException("Invalid command format. Expected format: data <subcommand> <args>");
            }
            var filename = subcommandSplit[1].strip();
            switch (subcommand) {
                case "import" -> importData(filename);
                case "export" -> exportData(filename);
                default -> throw new CLISyntaxException("Invalid command. Available commands: import, export");
            }
        }
    }

    private void importData(String filename) {
        dataImportExportService.importJSON(filename);
        DisplayHelper.printGreen("Imported from " + filename);

    }
    private void exportData(String filename) {
        dataImportExportService.exportJSON(filename);
        DisplayHelper.printGreen("Exported into " + filename);
    }
}
