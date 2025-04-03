package com.github.victortedesco.betterdeathscreen.bukkit.configuration;

import com.github.victortedesco.betterdeathscreen.bukkit.BetterDeathScreen;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BukkitMessages extends ConfigurationHandler {

    private List<String> incompatible;
    private List<String> enabled;
    private List<String> disabled;
    private List<String> updateAvailable;
    private String reloaded;
    private String blockedCommand;
    private String withoutPermission;
    private List<String> help;
    private String nonHardcoreCountdown;
    private String hardcoreCountdown;
    private String timeSingular;
    private String timePlural;
    private List<String> killed;
    private List<String> killedByPlayer;
    private List<String> kill;

    @Override
    public void loadFields() {
        FileConfiguration messages;
        try {
            this.createFile("messages_" + BetterDeathScreen.getConfiguration().getLanguage());
            messages = this.getFileConfiguration("messages_" + BetterDeathScreen.getConfiguration().getLanguage());
        } catch (Exception exception) {
            exception.printStackTrace();
            this.createFile("messages_en-US");
            messages = this.getFileConfiguration("messages_en-US");
        }
        incompatible = messages.getStringList("plugin.incompatible");
        enabled = messages.getStringList("plugin.enabled");
        disabled = messages.getStringList("plugin.disabled");
        updateAvailable = messages.getStringList("plugin.update-available");
        blockedCommand = messages.getString("misc.command-blocked");
        reloaded = messages.getString("commands.reloaded");
        withoutPermission = messages.getString("commands.without-permission");
        help = messages.getStringList("commands.help");
        nonHardcoreCountdown = messages.getString("respawn.non-hardcore-countdown");
        hardcoreCountdown = messages.getString("respawn.hardcore-countdown");
        timeSingular = messages.getString("misc.time.singular");
        timePlural = messages.getString("misc.time.plural");
        killed = messages.getStringList("death.killed");
        killedByPlayer = messages.getStringList("death.killed-by-player");
        kill = messages.getStringList("death.kill");
        this.translateAlternateColorCodes();
    }

    private void translateAlternateColorCodes() {
        for (Field field : getClass().getDeclaredFields()) {
            if (field.getType() == String.class) {
                try {
                    String newValue = ChatColor.translateAlternateColorCodes('&', (String) field.get(this));
                    field.setAccessible(true);
                    field.set(this, newValue);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (field.getType() == List.class) {
                ParameterizedType listType = (ParameterizedType) field.getGenericType();
                Class<?> listGenericType = (Class<?>) listType.getActualTypeArguments()[0];

                if (listGenericType == String.class) {
                    try {
                        List<String> newValue = (List<String>) field.get(this);
                        newValue = newValue
                                .stream()
                                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                                .collect(Collectors.toList());
                        field.setAccessible(true);
                        field.set(this, newValue);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
