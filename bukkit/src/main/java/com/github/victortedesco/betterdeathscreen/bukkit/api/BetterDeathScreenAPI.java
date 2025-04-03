package com.github.victortedesco.betterdeathscreen.bukkit.api;

import com.github.victortedesco.betterdeathscreen.bukkit.api.manager.PlayerManager;
import com.github.victortedesco.betterdeathscreen.bukkit.api.utils.Randomizer;

public class BetterDeathScreenAPI {

    private static final Randomizer RANDOMIZER = new Randomizer();
    private static final PlayerManager PLAYER_MANAGER = new PlayerManager();

    public static PlayerManager getPlayerManager() {
        return PLAYER_MANAGER;
    }

    public static Randomizer getRandomizer() {
        return RANDOMIZER;
    }
}
