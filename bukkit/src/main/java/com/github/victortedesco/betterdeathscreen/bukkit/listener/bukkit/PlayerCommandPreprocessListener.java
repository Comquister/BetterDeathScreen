package com.github.victortedesco.betterdeathscreen.bukkit.listener.bukkit;

import com.github.victortedesco.betterdeathscreen.bukkit.BetterDeathScreen;
import com.github.victortedesco.betterdeathscreen.bukkit.api.BetterDeathScreenAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerCommandPreprocessListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(@NotNull PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().split(" ")[0];

        if (BetterDeathScreen.getConfiguration().getAllowedCommands().contains("*")) return;
        if (command.equalsIgnoreCase("/bds") || command.equalsIgnoreCase("/betterdeathscreen")) return;
        if (BetterDeathScreenAPI.getPlayerManager().isDead(player)) {
            if (!BetterDeathScreen.getConfiguration().getAllowedCommands().contains(command)) {
                event.setCancelled(true);
                player.sendMessage(BetterDeathScreen.getMessages().getBlockedCommand());
            }
        }
    }
}
