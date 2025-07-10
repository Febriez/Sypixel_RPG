package com.febrie.rpg.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Language management utility for multi-language support
 * Handles loading and retrieving localized messages
 *
 * @author Febrie, CoffeeTory
 */
public class LangManager {

    private final Plugin plugin;
    private final Map<String, YamlConfiguration> languageConfigs;
    private final Map<UUID, String> playerLanguages;
    private final String defaultLanguage;

    private static final LegacyComponentSerializer SERIALIZER =
            LegacyComponentSerializer.legacyAmpersand();

    public LangManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.languageConfigs = new HashMap<>();
        this.playerLanguages = new ConcurrentHashMap<>();
        this.defaultLanguage = "ko_KR"; // Default to Korean

        loadLanguages();
    }

    /**
     * Loads all language files from the plugin's lang directory
     */
    private void loadLanguages() {
        // Create lang directory if it doesn't exist
        File langDir = new File(plugin.getDataFolder(), "lang");
        if (!langDir.exists()) {
            langDir.mkdirs();
        }

        // Copy default language files from resources
        copyDefaultLanguageFile("ko_KR.yml");
        copyDefaultLanguageFile("en_US.yml");

        // Load all .yml files in the lang directory
        File[] files = langDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String langCode = file.getName().replace(".yml", "");
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                languageConfigs.put(langCode, config);
                plugin.getLogger().info("Loaded language: " + langCode);
            }
        }

        plugin.getLogger().info("Language system initialized with " +
                languageConfigs.size() + " languages");
    }

    /**
     * Copies default language files from plugin resources
     */
    private void copyDefaultLanguageFile(@NotNull String fileName) {
        File langFile = new File(plugin.getDataFolder(), "lang/" + fileName);

        if (!langFile.exists()) {
            try (InputStream inputStream = plugin.getResource("lang/" + fileName)) {
                if (inputStream != null) {
                    Files.copy(inputStream, langFile.toPath());
                    plugin.getLogger().info("Created default language file: " + fileName);
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to copy language file " + fileName + ": " + e.getMessage());
            }
        }
    }

    /**
     * Gets a message for a player in their preferred language
     */
    @NotNull
    public String getMessage(@NotNull Player player, @NotNull String key, @NotNull String... placeholders) {
        String lang = getPlayerLanguage(player);
        return getMessage(lang, key, placeholders);
    }

    /**
     * Gets a message in the specified language
     */
    @NotNull
    public String getMessage(@NotNull String language, @NotNull String key, @NotNull String... placeholders) {
        YamlConfiguration config = languageConfigs.get(language);

        // Fallback to default language if specified language not found
        if (config == null) {
            config = languageConfigs.get(defaultLanguage);
        }

        // Fallback to English if default not found
        if (config == null) {
            config = languageConfigs.get("en_US");
        }

        // Last resort: return the key itself
        if (config == null) {
            plugin.getLogger().warning("No language configuration found for key: " + key);
            return key;
        }

        String message = config.getString(key);
        if (message == null) {
            // Try to get from default language
            YamlConfiguration defaultConfig = languageConfigs.get(defaultLanguage);
            if (defaultConfig != null) {
                message = defaultConfig.getString(key);
            }

            // Still null? Return key
            if (message == null) {
                plugin.getLogger().warning("Missing translation key: " + key);
                return key;
            }
        }

        // Replace placeholders
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                message = message.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
            }
        }

        return message;
    }

    /**
     * Gets a Component message for a player in their preferred language
     */
    @NotNull
    public Component getComponent(@NotNull Player player, @NotNull String key, @NotNull String... placeholders) {
        String message = getMessage(player, key, placeholders);
        return SERIALIZER.deserialize(message);
    }

    /**
     * Gets a Component message in the specified language
     */
    @NotNull
    public Component getComponent(@NotNull String language, @NotNull String key, @NotNull String... placeholders) {
        String message = getMessage(language, key, placeholders);
        return SERIALIZER.deserialize(message);
    }

    /**
     * Gets the player's preferred language
     */
    @NotNull
    public String getPlayerLanguage(@NotNull Player player) {
        return playerLanguages.getOrDefault(player.getUniqueId(),
                detectPlayerLanguage(player));
    }

    /**
     * Sets a player's preferred language
     */
    public void setPlayerLanguage(@NotNull Player player, @NotNull String language) {
        if (languageConfigs.containsKey(language)) {
            playerLanguages.put(player.getUniqueId(), language);
        }
    }

    /**
     * Detects player's language based on their client locale
     */
    @NotNull
    private String detectPlayerLanguage(@NotNull Player player) {
        String clientLocale = player.locale().toString();

        // Convert client locale to our format
        switch (clientLocale) {
            case "ko_kr", "ko_KR" -> {
                return "ko_KR";
            }
            case "en_us", "en_US", "en_gb", "en_GB" -> {
                return "en_US";
            }
            default -> {
                // Default to Korean for Korean region, English for others
                if (clientLocale.startsWith("ko")) {
                    return "ko_KR";
                }
                return "en_US";
            }
        }
    }

    /**
     * Reloads all language files
     */
    public void reload() {
        languageConfigs.clear();
        loadLanguages();
    }

    /**
     * Gets available languages
     */
    @NotNull
    public java.util.Set<String> getAvailableLanguages() {
        return languageConfigs.keySet();
    }

    /**
     * Removes player data when they logout
     */
    public void onPlayerLogout(@NotNull Player player) {
        playerLanguages.remove(player.getUniqueId());
    }

    /**
     * Checks if a language is available
     */
    public boolean isLanguageAvailable(@NotNull String language) {
        return languageConfigs.containsKey(language);
    }

    /**
     * Gets the default language
     */
    @NotNull
    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    /**
     * Sends a message to a player in their preferred language
     */
    public void sendMessage(@NotNull Player player, @NotNull String key, @NotNull String... placeholders) {
        Component message = getComponent(player, key, placeholders);
        player.sendMessage(message);
    }

    /**
     * Broadcasts a message to all players in their preferred languages
     */
    public void broadcast(@NotNull String key, @NotNull String... placeholders) {
        plugin.getServer().getOnlinePlayers().forEach(player ->
                sendMessage(player, key, placeholders));
    }
}