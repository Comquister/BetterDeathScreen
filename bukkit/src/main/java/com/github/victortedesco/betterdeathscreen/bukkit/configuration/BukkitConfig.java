package com.github.victortedesco.betterdeathscreen.bukkit.configuration;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

@Getter
public class BukkitConfig extends ConfigurationHandler {

    private String language;
    private int respawnTime;
    private boolean allowedToNotifyUpdates;
    private boolean goingToDropInventoryOnDeath;
    private List<String> deathSounds;
    private List<String> killSounds;
    private List<String> countdownSounds;
    private List<String> respawnSounds;
    private String killMessageType;
    private String killedMessageType;
    private String killedByPlayerMessageType;
    private String countdownMessageType;
    private String instantRespawnPermission;
    private String adminPermission;
    private boolean usingQueueTeleport;
    private boolean allowedToSpectate;
    private boolean forcedToSpectateKillerOnDeath;
    private boolean allowedToFly;
    private List<String> allowedCommands;

    @Override
    public void loadFields() {
        this.createFile("config");
        FileConfiguration config = this.getFileConfiguration("config");

        language = config.getString("misc.language", "en-US");
        respawnTime = config.getInt("misc.respawn-time");
        allowedToNotifyUpdates = config.getBoolean("misc.notify-updates");
        goingToDropInventoryOnDeath = config.getBoolean("misc.drop-player-inventory");
        deathSounds = config.getStringList("sound.death");
        countdownSounds = config.getStringList("sound.countdown");
        killSounds = config.getStringList("sound.kill");
        respawnSounds = config.getStringList("sound.respawn");
        killMessageType = config.getString("message-type.kill");
        killedMessageType = config.getString("message-type.killed");
        killedByPlayerMessageType = config.getString("message-type.killed-by-player");
        countdownMessageType = config.getString("message-type.countdown");
        instantRespawnPermission = config.getString("permissions.instant-respawn");
        adminPermission = config.getString("permissions.admin");
        usingQueueTeleport = config.getBoolean("death-settings.queue-teleport");
        allowedToSpectate = config.getBoolean("death-settings.allow-spectate");
        forcedToSpectateKillerOnDeath = config.getBoolean("death-settings.spectate-killer-on-death");
        allowedToFly = config.getBoolean("death-settings.allow-fly");
        allowedCommands = config.getStringList("death-settings.allowed-commands");
    }
}
