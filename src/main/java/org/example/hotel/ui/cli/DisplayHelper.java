package org.example.hotel.ui.cli;

import org.example.hotel.core.model.Entity;
import org.example.hotel.core.view.IEntity;

import java.util.List;

public class DisplayHelper {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";


    public static void printRed(String message) {
        System.out.println(ANSI_RED + message + ANSI_RESET);
    }

    public static void printYellow(String message) {
        System.out.println(ANSI_YELLOW + message + ANSI_RESET);
    }

    public static void printGreen(String message) {
        System.out.println(ANSI_GREEN + message + ANSI_RESET);
    }
    public static void printDefault(String message) {
        System.out.println(ANSI_RESET + message + ANSI_RESET);
    }


    public static void printEntityList(List<? extends IEntity> entities) {
        for (var entity : entities) {
            System.out.println("\t" + entity.toString());
        }
    }
}
