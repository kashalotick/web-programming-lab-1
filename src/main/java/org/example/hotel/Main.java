package org.example.hotel;

import org.example.hotel.ui.cli.CommandLineInterface;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        var scanner = new Scanner(System.in);

        var cli = new CommandLineInterface(scanner);
        cli.run();

        scanner.close();

    }
}