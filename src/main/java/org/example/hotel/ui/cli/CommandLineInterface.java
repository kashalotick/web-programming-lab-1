package org.example.hotel.ui.cli;

import java.util.Objects;
import java.util.Scanner;

public class CommandLineInterface {
    private final Scanner scanner;
    private boolean isExitRequested;

    public CommandLineInterface(Scanner scanner) {
        this.scanner = scanner;
        isExitRequested = false;
    }

    public void run() {
        do {
            var command = readCommand();
            handleCommand(command);

        } while (!isExitRequested);
    }


    private String readCommand() {
        var line = scanner.nextLine().strip();
        return line;
    }

    private void handleCommand(String command) {
        System.out.println(command);
        if (Objects.equals(command, "exit")) {
            Exit();
        }
    }

    private void Exit() {
        isExitRequested = true;
    }
}
