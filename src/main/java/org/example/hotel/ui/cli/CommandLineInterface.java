package org.example.hotel.ui.cli;

import org.example.hotel.core.service.DataImportExportService;
import org.example.hotel.core.service.GuestService;
import org.example.hotel.core.service.HotelService;
import org.example.hotel.core.service.ReservationService;
import org.example.hotel.ui.cli.handler.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public class CommandLineInterface {
    private final Scanner scanner;
    private boolean isExitRequested;
    private final TreeMap<String, ICommandHandler> commandHandlers;

    public CommandLineInterface(Scanner scanner) {
        this.scanner = scanner;
        isExitRequested = false;
        commandHandlers = new TreeMap<>();
        commandHandlers.put(GuestHandler.commandName, new GuestHandler(new GuestService()));
        commandHandlers.put(HotelHandler.commandName, new HotelHandler(new HotelService()));
        commandHandlers.put(ReservationHandler.commandName, new ReservationHandler(new ReservationService()));
        commandHandlers.put(DataHandler.commandName, new DataHandler(new DataImportExportService()));

    }

    public void run() {
        do {
            System.out.print("> ");
            var command = readCommand();

            try {
                handleCommand(command);
            } catch (CLISyntaxException e) {
                DisplayHelper.printYellow("Syntax Error: " + e.getMessage());
            }  catch (Exception e) {
                DisplayHelper.printRed("Error: " + e.getMessage());
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String fullTrace = sw.toString();
                DisplayHelper.printRed(fullTrace);

            }

        } while (!isExitRequested);
    }


    private String readCommand() {
        var line = scanner.nextLine().strip();
        return line;
    }

    private void handleCommand(String command) {
        if (Objects.equals(command, "exit")) {
            Exit();
        }
        if (Objects.equals(command, "help")) {
            handleHelp();
            return;
        }
        var tokens = command.split(" ", 2);
        if (tokens.length != 2) {
            throw new CLISyntaxException("Invalid command format. Expected format: <command> <args>");
        }
        var results = commandHandlers.subMap(tokens[0], tokens[0] + Character.MAX_VALUE);
        if (results.size() == 1) {
            var commandName = results.firstKey();
            commandHandlers.get(commandName).handle(tokens[1]);
        } else {
            throw new CLISyntaxException("Invalid command. Choose one of the following commands: " + commandHandlers.keySet());
        }
    }

    private void Exit() {
        isExitRequested = true;
    }
    private void handleHelp() {
        var hs = "exit\nhelp\n";
        for (var ch : commandHandlers.values()) {
            hs += ch.getHelpString();
        }
        System.out.println(hs);
    }
}
