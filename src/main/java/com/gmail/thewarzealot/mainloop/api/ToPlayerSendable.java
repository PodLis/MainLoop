package com.gmail.thewarzealot.mainloop.api;

import org.bukkit.entity.Player;

import java.util.Collection;

public interface ToPlayerSendable {
    void send(Collection<? extends Player> players);
}
