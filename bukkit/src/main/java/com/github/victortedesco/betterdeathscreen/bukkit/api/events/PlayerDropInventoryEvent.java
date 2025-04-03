package com.github.victortedesco.betterdeathscreen.bukkit.api.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerDropInventoryEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    /**
     * -- GETTER --
     * Get the drops of the event
     */
    @Getter
    private final Player player;

    /**
     * -- GETTER --
     * Get the drops of the event
     * -- SETTER --
     * Change the drops of the event
     */
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

    /**
     * Whether this event should be cancelled.
     *
     * @param cancel Whether BetterDeathScreen should handle the player's inventory drops.
     */
    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }
}
