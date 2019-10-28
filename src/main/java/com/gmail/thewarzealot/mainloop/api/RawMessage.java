package com.gmail.thewarzealot.mainloop.api;

import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.entity.Player;

import java.util.Collection;

public class RawMessage extends ChatMessage {

    public RawMessage(String[] lines) {
        super(lines);
    }

    @Override
    public void send(Collection<? extends Player> players) {
        for (Player player : players) {
            for (String line : lines) {
                try {
                    player.spigot().sendMessage(ComponentSerializer.parse(line));
                } catch (Throwable e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
}
