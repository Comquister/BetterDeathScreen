package com.github.victortedesco.betterdeathscreen.bukkit.listener.bukkit;

import com.github.victortedesco.betterdeathscreen.bukkit.BetterDeathScreen;
import com.github.victortedesco.betterdeathscreen.bukkit.api.BetterDeathScreenAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PlayerTeleportListener implements Listener {

    private static final Map<Player, Location> QUEUE_TELEPORT_LOCATION = new HashMap<>();

    public static Map<Player, Location> getQueueTeleportLocation() {
        return QUEUE_TELEPORT_LOCATION;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTeleport(@NotNull PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (BetterDeathScreen.getConfiguration().isUsingQueueTeleport()) {
            if (event.getCause() == PlayerTeleportEvent.TeleportCause.UNKNOWN || event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE)
                return;
            if (BetterDeathScreenAPI.getPlayerManager().isDead(player)) {
                getQueueTeleportLocation().put(player, event.getTo());
                event.setCancelled(true);
            }
        }
    }
}
