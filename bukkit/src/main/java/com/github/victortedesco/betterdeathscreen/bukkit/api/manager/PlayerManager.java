package com.github.victortedesco.betterdeathscreen.bukkit.api.manager;

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

    int minorVersion = 21;

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
            Sound bukkitSound = Sound.valueOf(sound.toUpperCase());
            player.playSound(player.getLocation(), bukkitSound, volume, pitch);
        } catch (IllegalArgumentException ignored) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public void sendCustomMessage(Player player, Player placeholderTarget, String type, String message, int timeSeconds) {
        try {
            message = ChatColor.translateAlternateColorCodes('&', message);
            MessageType messageType = MessageType.valueOf(type);
            if (messageType == MessageType.NONE) return;
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && placeholderTarget != null)
                message = PlaceholderAPI.setPlaceholders(placeholderTarget, message);
            if (messageType == MessageType.ACTIONBAR) player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, net.md_5.bungee.api.chat.TextComponent.fromLegacyText(message));
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
                player.sendTitle(title, subtitle, 5, 20 * timeSeconds, 5);
            }
            if (messageType == MessageType.CHAT) player.sendMessage(message);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public double getMaxHealth(Player player) {
        return player.getAttribute(Attribute.MAX_HEALTH).getValue();
    }

    public List<ItemStack> getFilteredInventory(Player player) {
        List<ItemStack> inventory = Arrays.stream(player.getInventory().getContents())
                .filter(stack -> !isStackEmpty(stack))
                .collect(Collectors.toList());
        return inventory;
    }

    public boolean isStackEmpty(ItemStack itemStack) {
        return itemStack == null || itemStack.getType() == Material.AIR || itemStack.getAmount() == 0;
    }

    public boolean isUsingTotem(Player player) {
        Material mainHand = player.getInventory().getItemInMainHand().getType();
        Material offHand = player.getInventory().getItemInOffHand().getType();
        return mainHand == Material.TOTEM_OF_UNDYING || offHand == Material.TOTEM_OF_UNDYING;
    }

    public enum MessageType {
        ACTIONBAR,
        TITLE,
        CHAT,
        NONE,
    }
}
