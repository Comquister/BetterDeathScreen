package com.github.victortedesco.betterdeathscreen.bukkit;

import com.cryptomorin.xseries.ReflectionUtils;
import com.github.victortedesco.betterdeathscreen.bukkit.api.BetterDeathScreenAPI;
import com.github.victortedesco.betterdeathscreen.bukkit.commands.MainCommand;
import com.github.victortedesco.betterdeathscreen.bukkit.commands.MainTabCompleter;
import com.github.victortedesco.betterdeathscreen.bukkit.configuration.BukkitConfig;
import com.github.victortedesco.betterdeathscreen.bukkit.configuration.BukkitMessages;
import com.github.victortedesco.betterdeathscreen.bukkit.listener.betterdeathscreen.PlayerDropInventoryListener;
import com.github.victortedesco.betterdeathscreen.bukkit.listener.bukkit.*;
import com.github.victortedesco.betterdeathscreen.bukkit.listener.packets.LoginPacketListener;
import com.github.victortedesco.betterdeathscreen.bukkit.utils.DeathTasks;
import com.github.victortedesco.betterdeathscreen.bukkit.utils.RespawnTasks;
import com.github.victortedesco.betterdeathscreen.bukkit.utils.updater.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class BetterDeathScreen extends JavaPlugin {

    private static final BukkitConfig CONFIG = new BukkitConfig();
    private static final BukkitMessages MESSAGES = new BukkitMessages();
    private static final DeathTasks DEATH_TASKS = new DeathTasks();
    private static final RespawnTasks RESPAWN_TASKS = new RespawnTasks();

    public static BukkitConfig getConfiguration() {
        return CONFIG;
    }

    public static BukkitMessages getMessages() {
        return MESSAGES;
    }

    public static DeathTasks getDeathTasks() {
        return DEATH_TASKS;
    }

    public static RespawnTasks getRespawnTasks() {
        return RESPAWN_TASKS;
    }

    public static @NotNull BetterDeathScreen getInstance() {
        return getPlugin(BetterDeathScreen.class);
    }

    public static void createAndLoadConfigurationsAndMessages() {
        getConfiguration().loadFields();
        getMessages().loadFields();
    }

    public static void sendConsoleMessage(String message) {
        Bukkit.getConsoleSender().sendMessage("[BetterDeathScreen] " + message);
    }

    @Override
    public void onEnable() {
        boolean forceDisable = false;
        createAndLoadConfigurationsAndMessages();
        try {
            Player.class.getMethod("spigot");
        } catch (NoSuchMethodException exception) {
            forceDisable = true;
        }
        if (ReflectionUtils.MINOR_NUMBER < 8) forceDisable = true;
        if (forceDisable) {
            getMessages().getIncompatible().forEach(BetterDeathScreen::sendConsoleMessage);
            getServer().getScheduler().runTaskLater(this, () -> getServer().getPluginManager().disablePlugin(this), 1L);
            return;
        }
        setupListeners();
        new UpdateChecker();
        getCommand("bds").setExecutor(new MainCommand());
        getCommand("bds").setTabCompleter(new MainTabCompleter());
        new Metrics(this, 17249);
        if (getServer().isHardcore()) {
            getServer().getOnlinePlayers().forEach(player -> {
                if (player.getGameMode() == GameMode.SPECTATOR && !player.hasPermission(getConfiguration().getAdminPermission())) {
                    BetterDeathScreenAPI.getPlayerManager().getDeadPlayers().add(player);
                    BetterDeathScreen.getRespawnTasks().startCountdown(player);
                }
            });
        }
        getMessages().getEnabled().forEach(BetterDeathScreen::sendConsoleMessage);
    }

    @Override
    public void onDisable() {
        getServer().getOnlinePlayers().forEach(player -> {
            BetterDeathScreen.getRespawnTasks().performRespawn(player, false);
        });
        getMessages().getDisabled().forEach(BetterDeathScreen::sendConsoleMessage);
    }

    private void setupListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerDropInventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityMountListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityRegainHealthListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityTargetListener(), this);
        Bukkit.getPluginManager().registerEvents(new FoodLevelChangeListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerCommandPreprocessListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerRespawnListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerTeleportListener(), this);

        if (ReflectionUtils.MINOR_NUMBER > 11)
            Bukkit.getPluginManager().registerEvents(new EntityPickupItemListener(), this);
        else
            Bukkit.getPluginManager().registerEvents(new PlayerPickupItemListener(), this);
        new LoginPacketListener();
    }
}
