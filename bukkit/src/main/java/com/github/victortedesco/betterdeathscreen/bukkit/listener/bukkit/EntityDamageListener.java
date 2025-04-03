package com.github.victortedesco.betterdeathscreen.bukkit.listener.bukkit;

import com.github.victortedesco.betterdeathscreen.bukkit.BetterDeathScreen;
import com.github.victortedesco.betterdeathscreen.bukkit.api.BetterDeathScreenAPI;
import com.github.victortedesco.betterdeathscreen.bukkit.api.manager.PlayerManager;
import com.github.victortedesco.betterdeathscreen.bukkit.api.utils.Randomizer;
import com.github.victortedesco.betterdeathscreen.bukkit.configuration.BukkitConfig;
import com.github.victortedesco.betterdeathscreen.bukkit.configuration.BukkitMessages;
import com.github.victortedesco.betterdeathscreen.bukkit.utils.DeathTasks;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Statistic;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class EntityDamageListener implements Listener {

    private final PlayerManager playerManager;
    private final BukkitConfig config;
    private final Randomizer randomizer;
    private final BukkitMessages messages;
    private final DeathTasks deathTasks;

    public EntityDamageListener() {
        this.playerManager = BetterDeathScreenAPI.getPlayerManager();
        this.config = BetterDeathScreen.getConfiguration();
        this.randomizer = BetterDeathScreenAPI.getRandomizer();
        this.messages = BetterDeathScreen.getMessages();
        this.deathTasks = BetterDeathScreen.getDeathTasks();
    }

    @EventHandler
    public void onEntityDamageByEntity(@NotNull EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();

            if (playerManager.isDead(damager)) {
                event.setCancelled(true);
                if (config.isAllowedToSpectate()) {
                    damager.setGameMode(GameMode.SPECTATOR);
                    damager.setSpectatorTarget(event.getEntity());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(@NotNull EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (playerManager.isDead(player)) {
                event.setCancelled(true);
                player.setFireTicks(0);
            } else {
                if (isValidDeath(player, event)) {
                    event.setDamage(0);
                    Bukkit.getScheduler().runTaskLater(BetterDeathScreen.getInstance(), () -> {
                        handlePlayerDeath(player, event);
                    }, 1L);
                }
            }
        }
    }

    private void handlePlayerDeath(@NotNull Player player, EntityDamageEvent event) {
        double playerHealth = player.getHealth();
        int time = Bukkit.isHardcore() ? 5 : config.getRespawnTime();
        playerManager.sendCustomMessage(player, player, config.getKilledMessageType(),
                randomizer.getRandomItemFromList(messages.getKilled()), time);
        deathTasks.performDeath(player);
        player.setAllowFlight(config.isAllowedToFly());
        player.setFlying(config.isAllowedToFly());

        if (event instanceof EntityDamageByEntityEvent) {
            Entity killer = ((EntityDamageByEntityEvent) event).getDamager();
            if (killer instanceof Projectile) {
                Projectile projectile = (Projectile) killer;
                if (projectile.getShooter() instanceof Entity)
                    killer = (Entity) projectile.getShooter();
            }
            if (killer instanceof Player) {
                Player playerKiller = (Player) killer;
                playerKiller.incrementStatistic(Statistic.PLAYER_KILLS, 1);
                playerKiller.incrementStatistic(Statistic.DAMAGE_DEALT, (int) Math.max(playerHealth, 1));
                playerManager.playSound(player, randomizer.getRandomItemFromList(config.getKillSounds()), false);
                playerManager.sendCustomMessage(player, playerKiller, config.getKilledByPlayerMessageType(),
                        randomizer.getRandomItemFromList(messages.getKilledByPlayer()), time);
                playerManager.sendCustomMessage(playerKiller, player, config.getKillMessageType(),
                        randomizer.getRandomItemFromList(messages.getKill()), 1);
            }
            if (config.isForcedToSpectateKillerOnDeath()) {
                player.setGameMode(GameMode.SPECTATOR);
                player.setSpectatorTarget(killer);
            }
        }
    }

    private boolean isValidDeath(@NotNull Player player, @NotNull EntityDamageEvent event) {
        return player.getHealth() <= event.getFinalDamage()
                && (!playerManager.isUsingTotem(player)
                || event.getCause() == EntityDamageEvent.DamageCause.SUICIDE
                || event.getCause() == EntityDamageEvent.DamageCause.VOID);
    }
}
