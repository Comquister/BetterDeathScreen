package com.github.victortedesco.betterdeathscreen.bukkit.commands;

import com.github.victortedesco.betterdeathscreen.bukkit.BetterDeathScreen;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainTabCompleter implements TabCompleter {

    private final String[] FIRST_COMPLETIONS = {"reload", "respawn"};

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender.hasPermission(BetterDeathScreen.getConfiguration().getAdminPermission())) {
            if (args.length == 1) {
                final List<String> completions = new ArrayList<>();

                StringUtil.copyPartialMatches(args[0], Arrays.asList(FIRST_COMPLETIONS), completions);
                Collections.sort(completions);
                return completions;
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("respawn")) return null;
        }
        return Collections.emptyList();
    }
}
