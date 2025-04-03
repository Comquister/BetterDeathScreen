package com.github.victortedesco.betterdeathscreen.bukkit.utils;

import com.cryptomorin.xseries.ReflectionUtils;
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
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public final class RespawnTasks {

    private final PlayerManager playerManager;
    private final BukkitConfig config;
    private final BukkitMessages messages;
    private final Randomizer randomizer;

    public RespawnTasks() {
        this.playerManager = BetterDeathScreenAPI.getPlayerManager();
        this.config = BetterDeathScreen.getConfiguration();
        this.messages = BetterDeathScreen.getMessages();
        this.randomizer = BetterDeathScreenAPI.getRandomizer();
    }

    public void startCountdown(Player player) {
        new BukkitRunnable() {
            final String randomCountdownSound = randomizer.getRandomItemFromList(config.getCountdownSounds());
            int time = config.getRespawnTime();

            @Override
            public void run() {
                time--;
                if (!player.isOnline() || !BetterDeathScreenAPI.getPlayerManager().isDead(player)) {
                    cancel();
                    return;
                }
                if (!Bukkit.isHardcore()) {
                    if (player.hasPermission(config.getInstantRespawnPermission())) time = 0;
                    if (time > 1) {
                        playerManager.sendCustomMessage(player, null, config.getCountdownMessageType(), messages.getNonHardcoreCountdown().replace("%time%", time + messages.getTimePlural()), 1);
                        playerManager.playSound(player, randomCountdownSound, false);
                    }
                    if (time == 1) {
                        playerManager.sendCustomMessage(player, null, config.getCountdownMessageType(), messages.getNonHardcoreCountdown().replace("%time%", time + messages.getTimeSingular()), 1);
                        playerManager.playSound(player, randomCountdownSound, false);
                    }
                    if (time <= 0) {
                        BetterDeathScreen.getRespawnTasks().performRespawn(player, false);
                        cancel();
                    }
                } else {
                    if (player.getGameMode() != GameMode.SPECTATOR) {
                        performRespawn(player, true);
                        cancel();
                        return;
                    }
                    if (config.getCountdownMessageType().equalsIgnoreCase("ACTIONBAR") && time <= 0) {
                        playerManager.sendCustomMessage(player, null, "ACTIONBAR", messages.getHardcoreCountdown(), 0);
                    }
                    if (!config.getCountdownMessageType().equalsIgnoreCase("ACTIONBAR") && time == 0) {
                        playerManager.sendCustomMessage(player, null, config.getCountdownMessageType(), messages.getHardcoreCountdown(), 86400);
                    }
                }
            }
        }.runTaskTimer(BetterDeathScreen.getInstance(), 20L, 20L);
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
        playerRespawnEvent = new PlayerRespawnEvent(player, respawnLocation, bedSpawn);
        if (ReflectionUtils.MINOR_NUMBER > 15)
            playerRespawnEvent = new PlayerRespawnEvent(player, respawnLocation, bedSpawn, anchorSpawn);
        if ((ReflectionUtils.MINOR_NUMBER == 19 && ReflectionUtils.PATCH_NUMBER == 4) || ReflectionUtils.MINOR_NUMBER > 19)
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
            if (!Bukkit.isHardcore()) {
                Bukkit.getOnlinePlayers().forEach(onlinePlayer -> onlinePlayer.showPlayer(player));
            }
        }
    }
}
