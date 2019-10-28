package com.gmail.thewarzealot.mainloop.api;

import org.bukkit.entity.Player;

import java.util.Collection;

public class ChatMessage implements ToPlayerSendable{

    String[] lines;

    public ChatMessage(String[] lines) {
        this.lines = lines;
    }

    @Override
    public void send(Collection<? extends Player> players) {
        for (Player player : players) {
            player.sendMessage(lines);
        }
    }
}
