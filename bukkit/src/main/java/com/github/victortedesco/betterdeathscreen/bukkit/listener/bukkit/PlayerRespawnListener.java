package com.github.victortedesco.betterdeathscreen.bukkit.listener.bukkit;

import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;

public class PlayerRespawnListener implements Listener {

    private static final Set<Player> BED_NOT_FOUND_MESSAGE_SENT = new HashSet<>();

    public static Set<Player> getBedNotFoundMessageSent() {
        return BED_NOT_FOUND_MESSAGE_SENT;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawnMonitorPriority(@NotNull PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (player.getBedSpawnLocation() == null) {
            if (!getBedNotFoundMessageSent().contains(player)) {
                TranslatableComponent noBed = new TranslatableComponent("block.minecraft.spawn.not_valid");
                player.spigot().sendMessage(noBed);
                getBedNotFoundMessageSent().add(player);
            }
        } else {
            getBedNotFoundMessageSent().remove(player);
        }

        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.setAllowFlight(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR);

        // Use teleportAsync() instead of teleport()
        player.teleportAsync(event.getRespawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN)
                .exceptionally(ex -> {
                    Bukkit.getLogger().warning("Failed to teleport player asynchronously: " + ex.getMessage());
                    return null;
                });
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawnHighestPriority(@NotNull PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (PlayerTeleportListener.getQueueTeleportLocation().containsKey(player)) {
            event.setRespawnLocation(PlayerTeleportListener.getQueueTeleportLocation().remove(player));
        }
    }
}
