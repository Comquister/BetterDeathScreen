package com.github.victortedesco.betterdeathscreen.bukkit.utils;

import com.github.victortedesco.betterdeathscreen.bukkit.BetterDeathScreen;
import com.github.victortedesco.betterdeathscreen.bukkit.api.BetterDeathScreenAPI;
import com.github.victortedesco.betterdeathscreen.bukkit.api.manager.PlayerManager;
import com.github.victortedesco.betterdeathscreen.bukkit.api.utils.Randomizer;
import com.github.victortedesco.betterdeathscreen.bukkit.configuration.BukkitConfig;
import org.bukkit.*;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.bukkit.damage.DamageType;
import org.bukkit.damage.DamageSource;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.text.Component;

public final class DeathTasks {

    private final PlayerManager playerManager;
    private final BukkitConfig config;
    private final Randomizer randomizer;

    public DeathTasks() {
        this.playerManager = BetterDeathScreenAPI.getPlayerManager();
        this.config = BetterDeathScreen.getConfiguration();
        this.randomizer = BetterDeathScreenAPI.getRandomizer();
    }

    public void sendPlayerDeathEvent(Player player) {
        DamageType genericDamageType = Registry.DAMAGE_TYPE.get(new NamespacedKey("minecraft", "generic"));
        if (genericDamageType == null) {
            throw new IllegalStateException("DamageType 'generic' não encontrado!");
        }

        DamageSource damageSource = DamageSource.builder(genericDamageType).build();
        List<ItemStack> drops = Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        PlayerDeathEvent playerDeathEvent = new PlayerDeathEvent(
                player,
                damageSource,
                drops,
                Math.min(100, player.getLevel() * 7),
                Component.empty());

        if (!Boolean.TRUE.equals(player.getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY))) {
            playerDeathEvent.setKeepInventory(false);
            playerDeathEvent.setKeepLevel(false);
            playerDeathEvent.setNewExp(0);
            playerDeathEvent.setNewLevel(0);
        } else {
            playerDeathEvent.setKeepInventory(true);
            playerDeathEvent.setDroppedExp(0);
            playerDeathEvent.setKeepLevel(true);
            playerDeathEvent.setNewExp((int) player.getExp());
            playerDeathEvent.setNewLevel(player.getLevel());
        }

        Bukkit.getPluginManager().callEvent(playerDeathEvent);
    }

    public void performDeath(@NotNull Player player) {
        player.closeInventory();
        player.incrementStatistic(Statistic.DEATHS, 1);
        player.incrementStatistic(Statistic.DAMAGE_TAKEN, (int) Math.max(1, player.getHealth()));
        player.setStatistic(Statistic.TIME_SINCE_DEATH, 0);
        playerManager.getDeadPlayers().add(player);

        Bukkit.getRegionScheduler().execute(BetterDeathScreen.getInstance(), player.getLocation(), () -> {
            sendPlayerDeathEvent(player);
            changeAttributes(player);
            player.getActivePotionEffects().forEach(potion -> player.removePotionEffect(potion.getType()));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 30000, 0, false, false));
            BetterDeathScreen.getRespawnTasks().startCountdown(player);
            playerManager.playSound(player, randomizer.getRandomItemFromList(config.getDeathSounds()), false);

            if (Bukkit.isHardcore()) {
                player.setGameMode(GameMode.SPECTATOR);
            } else {
                Bukkit.getOnlinePlayers().forEach(onlinePlayer -> onlinePlayer.hidePlayer(player));
            }
        });
    }

    public void changeAttributes(@NotNull Player player) {
        Bukkit.getRegionScheduler().execute(BetterDeathScreen.getInstance(), player.getLocation(), () -> {
            player.setHealth(playerManager.getMaxHealth(player));
            player.setRemainingAir(player.getMaximumAir());
            player.setFireTicks(0);
            player.setFoodLevel(20);
            player.eject();

            for (Entity entity : player.getWorld().getEntities()) {
                if (entity.getPassengers().contains(player)) entity.removePassenger(player);
                if (entity instanceof Creature) {
                    Creature creature = (Creature) entity;
                    if (creature.getTarget() == player) creature.setTarget(null);
                }
            }
        });
    }
}
