package com.github.victortedesco.betterdeathscreen.bukkit.listener.bukkit;

import com.github.victortedesco.betterdeathscreen.bukkit.api.BetterDeathScreenAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.entity.EntityMountEvent;

public class EntityMountListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityMount(@NotNull EntityMountEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Location lastLocation = player.getLocation();

            if (BetterDeathScreenAPI.getPlayerManager().isDead(player)) {
                event.setCancelled(true);
                player.teleport(lastLocation, PlayerTeleportEvent.TeleportCause.UNKNOWN);
            }
        }
    }
}
