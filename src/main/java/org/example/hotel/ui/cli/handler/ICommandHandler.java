package org.example.hotel.ui.cli.handler;

public interface ICommandHandler {
    String getCommandName();
    String getHelpString();
    void handle(String command);
}
