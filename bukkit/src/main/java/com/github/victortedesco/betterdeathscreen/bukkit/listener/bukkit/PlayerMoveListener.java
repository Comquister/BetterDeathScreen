package com.github.victortedesco.betterdeathscreen.bukkit.listener.bukkit;

import com.github.victortedesco.betterdeathscreen.bukkit.BetterDeathScreen;
import com.github.victortedesco.betterdeathscreen.bukkit.api.BetterDeathScreenAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerMoveListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (BetterDeathScreenAPI.getPlayerManager().isDead(player) &&
                (BetterDeathScreen.getConfiguration().isAllowedToSpectate() || BetterDeathScreen.getConfiguration().isForcedToSpectateKillerOnDeath())) {
            if (Bukkit.isHardcore()) return;
            if (player.getSpectatorTarget() == null && player.getGameMode() == GameMode.SPECTATOR)
                player.setGameMode(Bukkit.getDefaultGameMode());
        }
    }
}
