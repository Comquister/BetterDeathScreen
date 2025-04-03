package com.github.victortedesco.betterdeathscreen.bukkit.listener.bukkit;

import com.github.victortedesco.betterdeathscreen.bukkit.BetterDeathScreen;
import com.github.victortedesco.betterdeathscreen.bukkit.api.BetterDeathScreenAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // To avoid bugs, the player will respawn after disconnecting.
        if (BetterDeathScreenAPI.getPlayerManager().isDead(player)) {
            BetterDeathScreen.getRespawnTasks().performRespawn(player, false);
        }
        PlayerRespawnListener.getBedNotFoundMessageSent().remove(player);
    }
}
