package com.febrie.rpg.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Clean and optimized language management utility using JSON and Gson
 * Handles loading and retrieving localized messages with color placeholder support
 *
 * @author Febrie, CoffeeTory
 */
public class LangManager {

    private final Plugin plugin;
    private final Gson gson;
    private final Map<String, JsonObject> languageConfigs;
    private final Map<UUID, String> playerLanguages;
    private final String defaultLanguage;

    private static final LegacyComponentSerializer SERIALIZER =
            LegacyComponentSerializer.legacyAmpersand();

    public LangManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.gson = new Gson();
        this.languageConfigs = new HashMap<>();
        this.playerLanguages = new ConcurrentHashMap<>();
        this.defaultLanguage = "ko_KR";

        loadLanguages();
    }

    /**
     * Loads all language files from the plugin's lang directory
     */
    private void loadLanguages() {
        File langDir = new File(plugin.getDataFolder(), "lang");
        if (!langDir.exists()) {
            langDir.mkdirs();
        }

        // Copy and load default language files
        copyAndLoadLanguageFile("ko_KR.json");
        copyAndLoadLanguageFile("en_US.json");

        plugin.getLogger().info("Language system initialized with " + languageConfigs.size() + " languages: " + languageConfigs.keySet());
    }

    /**
     * Copies default language file from resources and loads it
     */
    private void copyAndLoadLanguageFile(@NotNull String fileName) {
        File langFile = new File(plugin.getDataFolder(), "lang/" + fileName);

        // Copy file if it doesn't exist
        if (!langFile.exists()) {
            try (InputStream inputStream = plugin.getResource("lang/" + fileName)) {
                if (inputStream != null) {
                    Files.copy(inputStream, langFile.toPath());
                    plugin.getLogger().info("Created language file: " + fileName);
                } else {
                    plugin.getLogger().warning("Resource not found: lang/" + fileName);
                    return;
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to copy language file " + fileName + ": " + e.getMessage());
                return;
            }
        }

        // Load the file
        try (InputStreamReader reader = new InputStreamReader(
                Files.newInputStream(langFile.toPath()), StandardCharsets.UTF_8)) {

            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            String langCode = fileName.replace(".json", "");
            languageConfigs.put(langCode, jsonObject);

            plugin.getLogger().info("Loaded language: " + langCode);

        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load language file " + fileName + ": " + e.getMessage());
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
        String message = getValueFromJson(language, key);

        if (message == null) {
            plugin.getLogger().warning("Missing translation key: " + key + " (language: " + language + ")");
            return key;
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
        return parseColorPlaceholders(message);
    }

    /**
     * Gets a Component message in the specified language
     */
    @NotNull
    public Component getComponent(@NotNull String language, @NotNull String key, @NotNull String... placeholders) {
        String message = getMessage(language, key, placeholders);
        return parseColorPlaceholders(message);
    }

    /**
     * Gets a list of Component messages for a player in their preferred language
     */
    @NotNull
    public java.util.List<Component> getComponentList(@NotNull Player player, @NotNull String key, @NotNull String... placeholders) {
        String lang = getPlayerLanguage(player);
        return getComponentList(lang, key, placeholders);
    }

    /**
     * Gets a list of Component messages in the specified language
     */
    @NotNull
    public java.util.List<Component> getComponentList(@NotNull String language, @NotNull String key, @NotNull String... placeholders) {
        JsonElement element = getElementFromJson(language, key);

        if (element == null || !element.isJsonArray()) {
            plugin.getLogger().warning("Missing or invalid translation key list: " + key + " (language: " + language + ")");
            return java.util.List.of(Component.text(key));
        }

        JsonArray jsonArray = element.getAsJsonArray();
        java.util.List<Component> components = new java.util.ArrayList<>();

        for (JsonElement arrayElement : jsonArray) {
            if (arrayElement.isJsonPrimitive()) {
                String message = arrayElement.getAsString();

                // Replace placeholders
                for (int i = 0; i < placeholders.length; i += 2) {
                    if (i + 1 < placeholders.length) {
                        message = message.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
                    }
                }

                components.add(parseColorPlaceholders(message));
            }
        }

        return components;
    }

    /**
     * Gets a string value from JSON using dot notation key
     */
    private String getValueFromJson(@NotNull String language, @NotNull String key) {
        JsonElement element = getElementFromJson(language, key);
        return element != null && element.isJsonPrimitive() ? element.getAsString() : null;
    }

    /**
     * Gets a JsonElement from JSON using dot notation key with fallback logic
     */
    private JsonElement getElementFromJson(@NotNull String language, @NotNull String key) {
        JsonElement element = navigateJsonPath(languageConfigs.get(language), key);

        // Try default language if not found
        if (element == null && !language.equals(defaultLanguage)) {
            element = navigateJsonPath(languageConfigs.get(defaultLanguage), key);
        }

        // Try English as final fallback
        if (element == null && !language.equals("en_US")) {
            element = navigateJsonPath(languageConfigs.get("en_US"), key);
        }

        return element;
    }

    /**
     * Navigates JSON object using dot notation path
     */
    private JsonElement navigateJsonPath(JsonObject jsonObject, @NotNull String path) {
        if (jsonObject == null) {
            return null;
        }

        String[] parts = path.split("\\.");
        JsonElement current = jsonObject;

        for (String part : parts) {
            if (current == null || !current.isJsonObject()) {
                return null;
            }
            current = current.getAsJsonObject().get(part);
        }

        return current;
    }

    /**
     * Parses color placeholders (%COLOR_NAME%) in text and converts to Component
     */
    @NotNull
    private Component parseColorPlaceholders(@NotNull String text) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("%COLOR_([A-Z_]+)%");
        java.util.regex.Matcher matcher = pattern.matcher(text);

        if (!matcher.find()) {
            return SERIALIZER.deserialize(text);
        }

        matcher.reset();
        Component result = Component.empty();
        int lastEnd = 0;
        net.kyori.adventure.text.format.TextColor currentColor = net.kyori.adventure.text.format.NamedTextColor.WHITE;

        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                String beforeText = text.substring(lastEnd, matcher.start());
                if (!beforeText.isEmpty()) {
                    result = result.append(Component.text(beforeText, currentColor));
                }
            }

            String colorName = matcher.group(1);
            net.kyori.adventure.text.format.TextColor foundColor = ColorUtil.getColorByName(colorName);

            if (foundColor != null) {
                currentColor = foundColor;
            } else {
                result = result.append(Component.text(matcher.group(0), currentColor));
            }

            lastEnd = matcher.end();
        }

        if (lastEnd < text.length()) {
            String remainingText = text.substring(lastEnd);
            if (!remainingText.isEmpty()) {
                result = result.append(Component.text(remainingText, currentColor));
            }
        }

        return result;
    }

    /**
     * Gets the player's preferred language
     */
    @NotNull
    public String getPlayerLanguage(@NotNull Player player) {
        return playerLanguages.computeIfAbsent(player.getUniqueId(),
                uuid -> detectPlayerLanguage(player));
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
        String clientLocale = player.locale().toString().toLowerCase();
        return clientLocale.startsWith("ko") ? "ko_KR" : "en_US";
    }

    /**
     * Reloads all language files
     */
    public void reload() {
        languageConfigs.clear();
        playerLanguages.clear();
        loadLanguages();
        plugin.getLogger().info("Language system reloaded");
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