package com.github.victortedesco.betterdeathscreen.bukkit.listener.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.github.victortedesco.betterdeathscreen.bukkit.BetterDeathScreen;
import org.bukkit.Bukkit;

public final class LoginPacketListener {

    public LoginPacketListener() {
        this.setupListener();
    }

    private void setupListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                BetterDeathScreen.getInstance(),
                ListenerPriority.LOW,
                PacketType.Play.Server.LOGIN) {

            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();

                packet.getBooleans().write(0, Bukkit.isHardcore());
            }
        });
    }
}
