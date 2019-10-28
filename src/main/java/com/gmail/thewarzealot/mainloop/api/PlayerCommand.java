package com.gmail.thewarzealot.mainloop.api;

import org.bukkit.entity.Player;

import java.util.Collection;

public class PlayerCommand implements ToPlayerSendable {

    private String command;

    public PlayerCommand(String command) {
        this.command = command;
    }

    @Override
    public void send(Collection<? extends Player> players) {
        for (Player player : players) {
            player.performCommand(command);
        }
    }
}
