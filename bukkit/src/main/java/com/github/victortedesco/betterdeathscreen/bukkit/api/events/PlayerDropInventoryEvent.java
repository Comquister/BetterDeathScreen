package com.github.victortedesco.betterdeathscreen.bukkit.api.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerDropInventoryEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    @Getter
    private final Player player;

    @Getter
    @Setter
    private List<ItemStack> drops;

    private boolean isCancelled = false;

    public PlayerDropInventoryEvent(Player player, List<ItemStack> drops) {
        this.player = player;
        this.drops = drops;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    public static void handleDropInventory(Plugin plugin, Player player, List<ItemStack> drops) {
        Location location = player.getLocation();
        World world = location.getWorld();

        if (Bukkit.getServer().getName().contains("Folia")) {
            // Executa na thread segura da região do jogador
            Bukkit.getRegionScheduler().execute(plugin, location, () -> processEvent(player, drops));
        } else {
            // Executa na thread principal no Paper/Bukkit
            Bukkit.getScheduler().runTask(plugin, () -> processEvent(player, drops));
        }
    }

    private static void processEvent(Player player, List<ItemStack> drops) {
        PlayerDropInventoryEvent event = new PlayerDropInventoryEvent(player, drops);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            for (ItemStack item : event.getDrops()) {
                Bukkit.getRegionScheduler().execute(Bukkit.getPluginManager().getPlugins()[0], player.getLocation(), () -> {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                });
            }
        }
    }
}
