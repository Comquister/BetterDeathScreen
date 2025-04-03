package com.github.victortedesco.betterdeathscreen.bukkit.api.manager;

import com.cryptomorin.xseries.ReflectionUtils;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.ActionBar;
import com.cryptomorin.xseries.messages.Titles;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class PlayerManager {

    private final Set<Player> deadPlayers = new HashSet<>();

    public boolean isDead(Player player) {
        return getDeadPlayers().contains(player);
    }

    public void playSound(Player player, @NotNull String string, boolean silent) {
        if (string.isEmpty()) return;
        String[] array = string.split(";");
        String sound = array[0];
        float volume = 0;
        float pitch = 0;
        try {
            volume = Float.parseFloat(array[1]);
            pitch = Float.parseFloat(array[2]);
        } catch (NumberFormatException numberFormatException) {
            numberFormatException.printStackTrace();
        }
        if (silent) volume = 0;

        try {
            Sound bukkitSound = XSound.matchXSound(sound).orElse(null).parseSound();
            player.playSound(player.getLocation(), bukkitSound, volume, pitch);
            return;
        } catch (NullPointerException ignored) {
        }
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public void sendCustomMessage(Player player, Player placeholderTarget, String type, String message, int timeSeconds) {
        try {
            message = ChatColor.translateAlternateColorCodes('&', message);
            MessageType messageType = MessageType.valueOf(type);
            if (messageType == MessageType.NONE) return;
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && placeholderTarget != null)
                message = PlaceholderAPI.setPlaceholders(placeholderTarget, message);
            if (messageType == MessageType.ACTIONBAR) ActionBar.sendActionBar(player, message);
            if (messageType == MessageType.TITLE) {
                String[] array = message.split("\n");
                String title = null;
                String subtitle = null;
                if (array.length == 1) {
                    subtitle = array[0];
                } else if (array.length != 0) {
                    title = array[0];
                    subtitle = array[1];
                }
                Titles.sendTitle(player, 5, 20 * timeSeconds, 5, title, subtitle);
            }
            if (messageType == MessageType.CHAT) player.sendMessage(message);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Get the max health of the player, returns the GENERIC_MAX_HEALTH attribute on versions newer than 1.8
     */
    public double getMaxHealth(Player player) {
        if (ReflectionUtils.MINOR_NUMBER > 8)
            return player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        return player.getMaxHealth();
    }

    /**
     * Get the inventory of the player without null stacks
     */
    public List<ItemStack> getFilteredInventory(Player player) {
        List<ItemStack> inventory = Arrays.stream(player.getInventory().getContents())
                .filter(stack -> !isStackEmpty(stack))
                .collect(Collectors.toList());
        if (ReflectionUtils.MINOR_NUMBER <= 8) {
            List<ItemStack> armor = Arrays.stream(player.getInventory().getArmorContents())
                    .filter(stack -> !isStackEmpty(stack))
                    .collect(Collectors.toList());
            inventory.addAll(armor);
        }
        return inventory;
    }

    public boolean isStackEmpty(ItemStack itemStack) {
        return itemStack == null || itemStack.getType() == Material.AIR || itemStack.getAmount() == 0;
    }

    /**
     * Get if the player is using a Totem of Undying in the main hand or in the offhand
     */
    public boolean isUsingTotem(Player player) {
        if (ReflectionUtils.MINOR_NUMBER >= 11) {
            Material mainHand = player.getInventory().getItemInMainHand().getType();
            Material offHand = player.getInventory().getItemInOffHand().getType();

            return mainHand.toString().contains("TOTEM") || offHand.toString().contains("TOTEM");
        }
        return false;
    }

    public enum MessageType {
        ACTIONBAR,
        TITLE,
        CHAT,
        NONE,
    }
}
