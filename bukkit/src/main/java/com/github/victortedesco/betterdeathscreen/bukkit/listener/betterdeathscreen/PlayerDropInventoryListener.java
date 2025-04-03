package com.github.victortedesco.betterdeathscreen.bukkit.listener.betterdeathscreen;

import com.github.victortedesco.betterdeathscreen.bukkit.api.events.PlayerDropInventoryEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class PlayerDropInventoryListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDropInventory(@NotNull PlayerDropInventoryEvent event) {
        Player player = event.getPlayer();

        event.getDrops().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
    }
}
