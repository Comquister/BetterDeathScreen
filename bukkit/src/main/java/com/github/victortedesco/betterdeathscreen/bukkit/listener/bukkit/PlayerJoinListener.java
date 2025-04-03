package com.github.victortedesco.betterdeathscreen.bukkit.listener.bukkit;

import com.github.victortedesco.betterdeathscreen.bukkit.BetterDeathScreen;
import com.github.victortedesco.betterdeathscreen.bukkit.api.BetterDeathScreenAPI;
import com.github.victortedesco.betterdeathscreen.bukkit.configuration.BukkitConfig;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        BukkitConfig config = BetterDeathScreen.getConfiguration();

        if (Bukkit.isHardcore()) {
            if ((player.getStatistic(Statistic.DEATHS) >= 1 && !player.hasPermission(config.getAdminPermission())
                    || BetterDeathScreenAPI.getPlayerManager().isDead(player))) {
                Bukkit.getScheduler().runTaskLater(BetterDeathScreen.getInstance(),
                        () -> player.setGameMode(GameMode.SPECTATOR), 2L); //MV Compatibility
                BetterDeathScreenAPI.getPlayerManager().getDeadPlayers().add(player);
                BetterDeathScreen.getRespawnTasks().startCountdown(player);
            }
        }
    }
}
