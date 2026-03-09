package org.example.hotel;

import org.example.hotel.core.model.Guest;
import org.example.hotel.core.view.IReservation;
import org.example.hotel.ui.cli.CommandLineInterface;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        var scanner = new Scanner(System.in);

        var cli = new CommandLineInterface(scanner);
        cli.run();

        scanner.close();

    }
}