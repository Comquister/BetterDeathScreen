package com.github.victortedesco.betterdeathscreen.bukkit.utils.updater;

import com.github.victortedesco.betterdeathscreen.bukkit.BetterDeathScreen;
import com.google.gson.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Getter
public class UpdateChecker {
    private static final String GITHUB_LINK = "https://api.github.com/repos/VictorTedesco/BetterDeathScreen/releases";
    private static final Gson SERIALIZER = new GsonBuilder().serializeNulls().create();
    private final String currentVersion = BetterDeathScreen.getInstance().getDescription().getVersion();
    private final BetterDeathScreen plugin;

    @Setter
    private GitHubRelease lastRelease;

    private String response;

    public UpdateChecker(BetterDeathScreen plugin) {
        this.plugin = plugin; // ✅ Agora está correto
        this.check();
        this.scheduleUpdateNotification();

        // Rodar a checagem de atualização de forma assíncrona
        if (isFolia()) {
            Bukkit.getAsyncScheduler().runNow(plugin, scheduledTask -> check());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, this::check);
        }
    }


    private void scheduleUpdateNotification() {
        long ticks = 20L * 60L * 30L; // 30 minutos em ticks
        long millis = ticks * 50; // Converte ticks para milissegundos

        if (isFolia()) {
            // Se for Folia, usar GlobalRegionScheduler com milissegundos
            this.plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(
                    this.plugin,
                    task -> notifyUpdate(),
                    5000, // 5 segundos convertidos para milissegundos
                    millis        // 30 minutos em milissegundos
            );
        } else {
            // Caso contrário, usar BukkitScheduler com ticks
            Bukkit.getScheduler().runTaskTimer(
                    this.plugin,
                    this::notifyUpdate,
                    20L * 5L, // 5 segundos em ticks
                    ticks     // 30 minutos em ticks
            );
        }
    }


    private boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServerInitEvent"); // Verifica se a classe do Folia está presente
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private void notifyUpdate() {
        if (canUpdate() && BetterDeathScreen.getConfiguration().isAllowedToNotifyUpdates()) {
            String[] placeholders = {"%release_link%", "%current_version%", "%latest_version%"};
            String[] replacements = {getLastRelease().getHtml_url(), getCurrentVersion(), getLastRelease().getTag_name()};

            BetterDeathScreen.getMessages().getUpdateAvailable().forEach(message -> {
                Bukkit.getConsoleSender().sendMessage(StringUtils.replaceEach(message, placeholders, replacements));
            });

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission(BetterDeathScreen.getConfiguration().getAdminPermission())) continue;
                BetterDeathScreen.getMessages().getUpdateAvailable().forEach(message -> {
                    player.sendMessage(StringUtils.replaceEach(message, placeholders, replacements));
                });
            }
        }
    }

    public void connect(URL url) {
        StringBuilder responseContent = new StringBuilder();
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) responseContent.append(line);
            reader.close();
        } catch (IOException exception) {
            return;
        }
        response = responseContent.toString();
    }

    public void check() {
        try {
            this.connect(new URL(GITHUB_LINK));
        } catch (MalformedURLException ignored) {
        }
        if (this.response == null) return;

        JsonElement element = JsonParser.parseString(response);
        JsonArray array = element.getAsJsonArray();
        if (array.size() == 0) return;

        GitHubRelease lastRelease = SERIALIZER.fromJson(array.get(0), GitHubRelease.class);

        if (lastRelease != null) setLastRelease(lastRelease);
    }

    public boolean canUpdate() {
        if (lastRelease != null) {
            return !lastRelease.getTag_name().equalsIgnoreCase(currentVersion) && !lastRelease.isPrerelease() && !lastRelease.isDraft();
        }
        return false;
    }
}
