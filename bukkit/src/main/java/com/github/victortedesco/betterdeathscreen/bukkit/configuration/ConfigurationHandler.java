package com.github.victortedesco.betterdeathscreen.bukkit.configuration;

import com.github.victortedesco.betterdeathscreen.bukkit.BetterDeathScreen;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public abstract class ConfigurationHandler {

    public final void createFile(String file) {
        if (!new File(BetterDeathScreen.getInstance().getDataFolder(), file + ".yml").exists()) {
            BetterDeathScreen.getInstance().saveResource(file + ".yml", false);
        }
    }

    public final @NotNull FileConfiguration getFileConfiguration(String file) {
        final File newFile = new File(BetterDeathScreen.getInstance().getDataFolder() + File.separator + file + ".yml");
        return YamlConfiguration.loadConfiguration(newFile);
    }

    public abstract void loadFields();
}


