package com.github.victortedesco.betterdeathscreen.bukkit.listener.bukkit;

import com.cryptomorin.xseries.ReflectionUtils;
import com.cryptomorin.xseries.messages.ActionBar;
import com.cryptomorin.xseries.messages.Titles;
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

        Titles.clearTitle(player);
        ActionBar.clearActionBar(player);

        if (player.getBedSpawnLocation() == null) {
            if (!getBedNotFoundMessageSent().contains(player)) {
                TranslatableComponent noBed = new TranslatableComponent("tile.bed.notValid");
                if (ReflectionUtils.MINOR_NUMBER > 12)
                    noBed = new TranslatableComponent("block.minecraft.bed.not_valid");
                if (ReflectionUtils.MINOR_NUMBER > 15)
                    noBed = new TranslatableComponent("block.minecraft.spawn.not_valid");
                player.spigot().sendMessage(noBed);
                getBedNotFoundMessageSent().add(player);
            }
        } else getBedNotFoundMessageSent().remove(player);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.setAllowFlight(player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR);
        player.teleport(event.getRespawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawnHighestPriority(@NotNull PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (PlayerTeleportListener.getQueueTeleportLocation().containsKey(player)) {
            event.setRespawnLocation(PlayerTeleportListener.getQueueTeleportLocation().remove(player));
        }
    }
}
