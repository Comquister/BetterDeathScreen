package com.github.victortedesco.betterdeathscreen.bukkit.listener.bukkit;

import com.github.victortedesco.betterdeathscreen.bukkit.BetterDeathScreen;
import com.github.victortedesco.betterdeathscreen.bukkit.api.events.PlayerDropInventoryEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerDeathListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (!event.getKeepInventory()) {
            PlayerDropInventoryEvent playerDropInventoryEvent = new PlayerDropInventoryEvent(player, event.getDrops());
            if (!BetterDeathScreen.getConfiguration().isGoingToDropInventoryOnDeath())
                playerDropInventoryEvent.setCancelled(true);
            Bukkit.getPluginManager().callEvent(playerDropInventoryEvent);
            player.getInventory().setArmorContents(null);
            player.getInventory().clear();
        }
        if (!event.getKeepLevel()) {
            player.setLevel(0);
            player.setExp(0);
        }
        if (event.getDeathMessage() != null) {
            if (player.getWorld().getGameRuleValue("showDeathMessages").equalsIgnoreCase("true")) {
                Bukkit.getConsoleSender().sendMessage(event.getDeathMessage());
                Bukkit.getOnlinePlayers().forEach(onlinePlayer -> onlinePlayer.sendMessage(event.getDeathMessage()));
            }
        }
    }
}
