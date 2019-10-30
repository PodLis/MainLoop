package com.gmail.thewarzealot.mainloop.api;

import net.minecraft.server.v1_14_R1.ChatMessageType;
import net.minecraft.server.v1_14_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_14_R1.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ActionBar implements ToPlayerSendable{

    private PacketPlayOutChat packet;

    private int displayTime;

    public int getExtraDisplayTime() {
        return displayTime - 3;
    }

    public ActionBar(String message, int displayTime) {
        packet = new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + message + "\"}"), ChatMessageType.GAME_INFO);
        this.displayTime = Math.max(displayTime, 3);
    }

    @Override
    public void send(Collection<? extends Player> players) {
        for (Player player : players) {
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
        }
    }
}
