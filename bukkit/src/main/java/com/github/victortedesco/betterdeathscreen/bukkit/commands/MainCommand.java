package com.github.victortedesco.betterdeathscreen.bukkit.commands;

import com.github.victortedesco.betterdeathscreen.bukkit.BetterDeathScreen;
import com.github.victortedesco.betterdeathscreen.bukkit.configuration.BukkitConfig;
import com.github.victortedesco.betterdeathscreen.bukkit.configuration.BukkitMessages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        BukkitConfig config = BetterDeathScreen.getConfiguration();
        BukkitMessages messages = BetterDeathScreen.getMessages();

        if (!sender.hasPermission(config.getAdminPermission())) {
            sender.sendMessage(messages.getWithoutPermission());
            return true;
        }
        if (args.length != 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                BetterDeathScreen.createAndLoadConfigurationsAndMessages();
                sender.sendMessage(messages.getReloaded());
            } else if (args[0].equalsIgnoreCase("respawn")) {
                Player target = null;
                try {
                    if (sender instanceof Player) {
                        if (args.length == 1) target = (Player) sender;
                        else target = Bukkit.getPlayer(args[1]);
                    } else {
                        if (args.length > 1) target = Bukkit.getPlayer(args[1]);
                    }
                } catch (Exception ignored) {

                }
                BetterDeathScreen.getRespawnTasks().performRespawn(target, true);
            } else messages.getHelp().forEach(sender::sendMessage);
        } else messages.getHelp().forEach(sender::sendMessage);

        return true;
    }
}