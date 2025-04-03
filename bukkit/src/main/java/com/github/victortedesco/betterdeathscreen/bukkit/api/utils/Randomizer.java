package com.github.victortedesco.betterdeathscreen.bukkit.api.utils;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class Randomizer {

    public <T> T getRandomItemFromList(@NotNull List<T> list) {
        Random random = new Random();
        int item = random.nextInt(list.size());

        return list.get(item);
    }
}
