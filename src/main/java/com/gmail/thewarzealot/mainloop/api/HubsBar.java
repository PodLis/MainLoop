package com.gmail.thewarzealot.mainloop.api;

import com.gmail.thewarzealot.mainloop.MainLoop;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.Collection;

public class HubsBar implements ToPlayerSendable{

    private BossBar bossBar;

    public HubsBar(MainLoop plugin, String text, BarColor color, BarStyle style, double progress) {
        bossBar = plugin.getServer().createBossBar(text, color, style);
        bossBar.setProgress(progress);
    }

    @Override
    public void send(Collection<? extends Player> players) {
        for (Player player : players) {
            bossBar.addPlayer(player);
        }
    }

    public void clean() {
        bossBar.removeAll();
    }
}
