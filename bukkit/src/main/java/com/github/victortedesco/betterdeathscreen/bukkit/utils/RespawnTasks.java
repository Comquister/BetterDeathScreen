package com.github.victortedesco.betterdeathscreen.bukkit.utils;

import com.github.victortedesco.betterdeathscreen.bukkit.BetterDeathScreen;
import com.github.victortedesco.betterdeathscreen.bukkit.api.BetterDeathScreenAPI;
import com.github.victortedesco.betterdeathscreen.bukkit.api.manager.PlayerManager;
import com.github.victortedesco.betterdeathscreen.bukkit.api.utils.Randomizer;
import com.github.victortedesco.betterdeathscreen.bukkit.configuration.BukkitConfig;
import com.github.victortedesco.betterdeathscreen.bukkit.configuration.BukkitMessages;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.HashSet;
import java.util.Set;

public final class RespawnTasks implements Listener {

    private final PlayerManager playerManager;
    private final BukkitConfig config;
    private final BukkitMessages messages;
    private final Randomizer randomizer;
    private final Set<Player> respawnCooldownPlayers = new HashSet<>();

    public RespawnTasks() {
        this.playerManager = BetterDeathScreenAPI.getPlayerManager();
        this.config = BetterDeathScreen.getConfiguration();
        this.messages = BetterDeathScreen.getMessages();
        this.randomizer = BetterDeathScreenAPI.getRandomizer();
    }

    public void startCountdown(Player player) {
        final String randomCountdownSound = randomizer.getRandomItemFromList(config.getCountdownSounds());
        final int[] time = { config.getRespawnTime() };
        respawnCooldownPlayers.add(player);

        Bukkit.getRegionScheduler().runAtFixedRate(
                BetterDeathScreen.getInstance(),
                player.getLocation(),
                (ScheduledTask task) -> {
                    time[0]--;

                    if (!player.isOnline() || !BetterDeathScreenAPI.getPlayerManager().isDead(player)) {
                        task.cancel();
                        respawnCooldownPlayers.remove(player);
                        return;
                    }

                    if (!Bukkit.isHardcore()) {
                        if (player.hasPermission(config.getInstantRespawnPermission())) {
                            time[0] = 0;
                        }

                        if (time[0] > 1) {
                            playerManager.sendCustomMessage(player, null, config.getCountdownMessageType(),
                                    messages.getNonHardcoreCountdown().replace("%time%", time[0] + messages.getTimePlural()), 1);
                            playerManager.playSound(player, randomCountdownSound, false);
                        } else if (time[0] == 1) {
                            playerManager.sendCustomMessage(player, null, config.getCountdownMessageType(),
                                    messages.getNonHardcoreCountdown().replace("%time%", time[0] + messages.getTimeSingular()), 1);
                            playerManager.playSound(player, randomCountdownSound, false);
                        }

                        if (time[0] <= 0) {
                            BetterDeathScreen.getRespawnTasks().performRespawn(player, false);
                            task.cancel();
                            respawnCooldownPlayers.remove(player);
                        }

                    } else {
                        if (player.getGameMode() != GameMode.SPECTATOR) {
                            performRespawn(player, true);
                            task.cancel();
                            respawnCooldownPlayers.remove(player);
                            return;
                        }

                        if (config.getCountdownMessageType().equalsIgnoreCase("ACTIONBAR") && time[0] <= 0) {
                            playerManager.sendCustomMessage(player, null, "ACTIONBAR", messages.getHardcoreCountdown(), 0);
                        }

                        if (!config.getCountdownMessageType().equalsIgnoreCase("ACTIONBAR") && time[0] == 0) {
                            playerManager.sendCustomMessage(player, null, config.getCountdownMessageType(),
                                    messages.getHardcoreCountdown(), 86400);
                        }
                    }
                },
                20L,
                20L
        );
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (respawnCooldownPlayers.contains(player)) {
            event.setCancelled(true);
        }
    }

    public void sendPlayerRespawnEvent(@NotNull Player player) {
        PlayerRespawnEvent playerRespawnEvent;
        Location respawnLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
        boolean bedSpawn = false;
        boolean anchorSpawn = false;

        if (player.getBedSpawnLocation() != null) {
            respawnLocation = player.getBedSpawnLocation();
            bedSpawn = true;
            if (player.getBedSpawnLocation().getWorld().getEnvironment() == World.Environment.NETHER) {
                bedSpawn = false;
                anchorSpawn = true;
            }
        }
        playerRespawnEvent = new PlayerRespawnEvent(player, respawnLocation, bedSpawn, anchorSpawn, PlayerRespawnEvent.RespawnReason.DEATH);

        Bukkit.getPluginManager().callEvent(playerRespawnEvent);
    }

    public void performRespawn(Player player, boolean forceRespawn) {
        if (Bukkit.isHardcore() && !forceRespawn) return;
        if (playerManager.isDead(player) || forceRespawn) {
            playerManager.getDeadPlayers().remove(player);
            player.setGameMode(Bukkit.getDefaultGameMode());
            BetterDeathScreen.getDeathTasks().changeAttributes(player);
            sendPlayerRespawnEvent(player);
            playerManager.playSound(player, randomizer.getRandomItemFromList(config.getRespawnSounds()), false);
            respawnCooldownPlayers.remove(player);
            if (!Bukkit.isHardcore()) {
                Bukkit.getOnlinePlayers().forEach(onlinePlayer -> onlinePlayer.showPlayer(player));
            }
        }
    }
}
