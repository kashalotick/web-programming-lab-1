package org.example.hotel.ui.cli.handler;

import org.example.hotel.core.service.GuestService;
import org.example.hotel.ui.cli.CLISyntaxException;
import org.example.hotel.ui.cli.DisplayHelper;

public class GuestHandler implements ICommandHandler {
    public static final String commandName = "guest";
    private final GuestService guestService;

    public GuestHandler(GuestService guestService) {
        this.guestService = guestService;
    }


    @Override
    public String getCommandName() {
        return commandName;
    }

    @Override
    public String getHelpString() {
        return """
                guest <subcommand> <args>
                      create <name>
                      get <id>
                      list
                      delete <id>
                      set-name <id> <name>
                """;
    }

    @Override
    public void handle(String command) {
        var subcommandSplit = command.split(" ", 2);
        var subcommand = subcommandSplit[0];
        if (subcommand.equals("help")) {
            System.out.println(getHelpString());
        } else if (subcommand.equals("list")) {
            list();
        }
        else {
            if (subcommandSplit.length != 2) {
                throw new CLISyntaxException("Invalid command format. Expected format: guest <subcommand> <args>");
            }
            var args = subcommandSplit[1];
            switch (subcommand) {
                case "create" -> create(args);
                case "get" -> get(args);
                case "delete" -> delete(args);
                case "set-name" -> setName(args);
                default ->
                        throw new CLISyntaxException("Invalid command. Available commands: create, get, list, delete, set-name");
            }
        }
    }

    private void create(String args) {
        var name = args.strip();

        var guest = guestService.create(name);
        DisplayHelper.printGreen("Created guest: " + guest);

    }

    private void get(String args) {
        var id = Integer.parseInt(args.strip());

        var guest = guestService.get(id);
        DisplayHelper.printDefault(guest.toString());
    }

    private void list() {
        var guests = guestService.getAll();
        if (guests.isEmpty()) {
            DisplayHelper.printDefault("Empty");
        }
        else {
            DisplayHelper.printEntityList(guests);
        }    }

    private void delete(String args) {
        var id = Integer.parseInt(args.strip());

        guestService.delete(id);
        DisplayHelper.printGreen("Deleted guest with id: " + id);
    }

    private void setName(String args) {
        var split = args.split(" ", 2);
        if (split.length != 2) {
            throw new CLISyntaxException("Invalid command format. Expected format: guest set-name <id> <name>");
        }
        var id = Integer.parseInt(split[0].strip());
        var name = split[1].strip();

        var guest = guestService.setName(id, name);
        DisplayHelper.printGreen("Updated guest: " + guest);
    }
}
