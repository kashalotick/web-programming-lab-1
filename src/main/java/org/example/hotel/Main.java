package org.example.hotel;

import org.example.hotel.core.model.Guest;
import org.example.hotel.ui.cli.CommandLineInterface;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
//
//        var scanner = new Scanner(System.in);
//
//        var cli = new CommandLineInterface(scanner);
//        cli.run();
//
//        scanner.close();
        var g = new Guest();
        g.create(g);
        System.out.println(g.read(g.id));
        g.update(g.id, g);
        g.delete(g.id);

    }
}