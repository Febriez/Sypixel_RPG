package com.febrie.rpg.util;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.util.lang.ILangKey;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Language Manager v7.0 - Complete rewrite with resource-based loading
 */
public class LangManager {
    private static final String[] SUPPORTED_LOCALES = {"ko_kr", "en_us", "ja_jp"};
    private static final String DEFAULT_LOCALE = "en_us";

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^}]+)}");
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("%([0-9a-fA-F]{6})_COLOR%");
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();

    private static RPGMain plugin;
    private static final Map<Locale, Map<String, String>> langData = new ConcurrentHashMap<>();
    private static final Set<String> ignoredFiles = Set.of("items_backup", "items_fixed", "commands", "social", "scute", "ectoplasm");

    public static void init(RPGMain pluginInstance) {
        plugin = pluginInstance;
        loadAllLanguages();
        validateLanguageKeys();
    }

    private static void loadAllLanguages() {
        plugin.getLogger().info("[LangManager] ============================================");
        plugin.getLogger().info("[LangManager] Starting language loading (v7.0)...");
        plugin.getLogger().info("[LangManager] ============================================");

        for (String localeStr : SUPPORTED_LOCALES) {
            try {
                Locale locale = parseLocale(localeStr);
                Map<String, String> data = loadLanguageFiles(localeStr);
                langData.put(locale, data);
                plugin.getLogger().info("[LangManager] Loaded " + localeStr + " -> Locale: " + locale.toLanguageTag()
                        .toUpperCase() + " (keys: " + data.size() + ")");
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Failed to load language: " + localeStr, e);
            }
        }

        plugin.getLogger().info("[LangManager] Language loading complete!");
        plugin.getLogger().info("[LangManager] ============================================");
    }

    @NotNull
    private static Map<String, String> loadLanguageFiles(@NotNull String localeDir) {
        Map<String, String> allKeys = new HashMap<>();

        // List of all known language files
        String[] languageFiles = {"achievement.json", "action.json", "actionbar.json", "biome.json", "bossbar.json", "combat.json", "command.json", "craft.json", "damage.json", "dialog.json", "dungeon.json", "economy.json", "effect.json", "enchant.json", "friend.json", "general.json", "gui.json", "guild.json", "hologram.json", "island.json", "items.json", "job.json", "loot.json", "mail.json", "mailbox.json", "messages.json", "minigame.json", "mount.json", "notification.json", "party.json", "permission.json", "pet.json", "placeholder.json", "quest.json", "rank.json", "rarity.json", "settings.json", "shop.json", "skill.json", "stat.json", "status.json", "talent.json", "teleport.json", "title.json", "trade.json", "unit.json", "warzone.json", "whisper.json", "world.json"};

        for (String fileName : languageFiles) {
            loadResourceFile(localeDir, fileName, allKeys);
        }

        return allKeys;
    }

    private static void loadResourceFile(@NotNull String localeDir, @NotNull String fileName, @NotNull Map<String, String> allKeys) {
        String nameWithoutExt = fileName.substring(0, fileName.length() - 5);

        if (shouldIgnoreFile(nameWithoutExt)) {
            return;
        }

        String resourcePath = "/" + localeDir + "/" + fileName;

        try (InputStream is = LangManager.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                // File doesn't exist for this locale, skip it silently
                return;
            }

            try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                String namespace = nameWithoutExt.replace("-", "_");

                // Special handling for merged files
                if (nameWithoutExt.equals("command")) {
                    loadCommandKeys(json, allKeys, localeDir);
                } else {
                    flattenJson("", json, allKeys, namespace);
                }

                int keyCount = countJsonKeys(json);
                if (keyCount > 0) {
                    plugin.getLogger()
                            .info("[LangManager] Loaded " + fileName + " with namespace: " + namespace + " (keys: " + keyCount + ")");
                }
            }
        } catch (Exception e) {
            // Only log actual errors, not missing files
            if (!e.getMessage().contains("Cannot find") && !e.getMessage().contains("null")) {
                plugin.getLogger().warning("[LangManager] Failed to load " + fileName + ": " + e.getMessage());
            }
        }
    }

    private static boolean shouldIgnoreFile(@NotNull String fileName) {
        return ignoredFiles.contains(fileName);
    }

    private static void loadCommandKeys(@NotNull JsonObject json, @NotNull Map<String, String> allKeys, @NotNull String localeDir) {
        // Handle both command and commands namespaces
        flattenJson("", json, allKeys, "command");
        flattenJson("", json, allKeys, "commands");
        plugin.getLogger().info("[LangManager] Loaded merged command/commands keys for " + localeDir);
    }

    private static void flattenJson(@NotNull String prefix, @NotNull JsonElement element, @NotNull Map<String, String> allKeys, @NotNull String namespace) {
        if (element.isJsonPrimitive()) {
            String value = element.getAsString();
            String fullKey = prefix.isEmpty() ? namespace : namespace + "." + prefix;
            allKeys.put(fullKey, value);
        } else if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                String newPrefix = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
                flattenJson(newPrefix, entry.getValue(), allKeys, namespace);
            }
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                String newPrefix = prefix + "[" + i + "]";
                flattenJson(newPrefix, array.get(i), allKeys, namespace);
            }
        }
    }

    private static int countJsonKeys(@NotNull JsonElement element) {
        if (element.isJsonPrimitive()) {
            return 1;
        } else if (element.isJsonObject()) {
            int count = 0;
            JsonObject obj = element.getAsJsonObject();
            for (JsonElement value : obj.asMap().values()) {
                count += countJsonKeys(value);
            }
            return count;
        } else if (element.isJsonArray()) {
            return element.getAsJsonArray().size();
        }
        return 0;
    }

    private static void validateLanguageKeys() {
        plugin.getLogger().info("[LangManager] Validating language keys...");
        // Additional validation logic can be added here
    }

    @NotNull
    private static Locale parseLocale(@NotNull String localeStr) {
        String[] parts = localeStr.split("_");
        if (parts.length == 2) {
            return Locale.of(parts[0], parts[1].toUpperCase());
        }
        return Locale.of(localeStr);
    }

    @NotNull
    private static Locale getLocale(@NotNull CommandSender sender) {
        if (sender instanceof Player player) {
            // Use locale() instead of deprecated getLocale()
            String localeName = player.locale().toString();
            // Convert Minecraft locale format (e.g., "ko_kr") to Java Locale
            String[] parts = localeName.toLowerCase().split("_");
            if (parts.length == 2) {
                Locale locale = Locale.of(parts[0], parts[1].toUpperCase());
                if (langData.containsKey(locale)) {
                    return locale;
                }
            }
        }
        return Locale.of("en", "US");
    }

    @NotNull
    private static String getInternal(@NotNull String key, @NotNull Locale locale) {
        Map<String, String> localeData = langData.get(locale);
        if (localeData == null) {
            localeData = langData.get(Locale.of("en", "US"));
        }

        if (localeData != null) {
            String value = localeData.get(key);
            if (value != null) {
                return value;
            }
        }

        // Fallback to default locale
        Map<String, String> defaultData = langData.get(Locale.of("en", "US"));
        if (defaultData != null) {
            String value = defaultData.get(key);
            if (value != null) {
                return value;
            }
        }

        return key;
    }

    // Single parameter text methods - use default locale
    @NotNull
    public static Component text(@NotNull ILangKey key) {
        Locale defaultLocale = Locale.of("en", "US");
        String text = getInternal(key.key(), defaultLocale);
        return parseText(text);
    }

    @NotNull
    public static Component text(@NotNull ILangKey key, @NotNull Player player) {
        String text = getInternal(key.key(), getLocale(player));
        return parseText(text);
    }


    @NotNull
    public static Component text(@NotNull ILangKey key, @NotNull Player player, Object... replacements) {
        String text = getInternal(key.key(), getLocale(player));

        // Handle Component replacements properly
        Component result = Component.empty();
        String[] parts = text.split("\\{\\d+}");
        java.util.regex.Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);

        int lastEnd = 0;
        while (matcher.find()) {
            // Add text before placeholder
            if (matcher.start() > lastEnd) {
                result = result.append(parseText(text.substring(lastEnd, matcher.start())));
            }

            // Add replacement
            String placeholder = matcher.group(1);
            try {
                int index = Integer.parseInt(placeholder);
                if (index >= 0 && index < replacements.length) {
                    Object value = replacements[index];
                    if (value instanceof Component) {
                        result = result.append((Component) value);
                    } else {
                        result = result.append(Component.text(String.valueOf(value)));
                    }
                }
            } catch (NumberFormatException e) {
                // Handle named placeholders if needed
                result = result.append(parseText(matcher.group()));
            }
            lastEnd = matcher.end();
        }

        // Add remaining text
        if (lastEnd < text.length()) {
            result = result.append(parseText(text.substring(lastEnd)));
        }

        return result;
    }



    // Additional text methods with Locale
    @NotNull
    public static Component text(@NotNull ILangKey key, @NotNull Locale locale) {
        String text = getInternal(key.key(), locale);
        return parseText(text);
    }

    @NotNull
    public static Component text(@NotNull ILangKey key, @NotNull Locale locale, Object... replacements) {
        String text = getInternal(key.key(), locale);
        text = replacePlaceholders(text, replacements);
        return parseText(text);
    }


    // Single parameter list methods - use default locale
    @NotNull
    public static List<Component> list(@NotNull ILangKey key) {
        Locale defaultLocale = Locale.of("en", "US");
        return getLoreInternal(key.key(), defaultLocale);
    }

    @NotNull
    public static List<Component> list(@NotNull ILangKey key, @NotNull Player player) {
        return getLoreInternal(key.key(), getLocale(player));
    }


    @NotNull
    public static List<Component> list(@NotNull ILangKey key, @NotNull Player player, Object... replacements) {
        String text = getInternal(key.key(), getLocale(player));
        text = replacePlaceholders(text, replacements);
        return parseList(text);
    }



    // List methods with Locale
    @NotNull
    public static List<Component> list(@NotNull ILangKey key, @NotNull Locale locale) {
        return getLoreInternal(key.key(), locale);
    }

    @NotNull
    public static List<Component> list(@NotNull ILangKey key, @NotNull Locale locale, Object... replacements) {
        List<String> lines = getListInternal(key.key(), locale);
        List<Component> components = new ArrayList<>();

        for (String line : lines) {
            String replaced = replacePlaceholders(line, replacements);
            components.add(parseText(replaced));
        }

        return components;
    }


    @NotNull
    public static String replacePlaceholders(@NotNull Component component, Object... replacements) {
        // Convert component to plain text for legacy code
        String text = PlainTextComponentSerializer.plainText().serialize(component);
        return replacePlaceholders(text, replacements);
    }

    @NotNull
    public static String replacePlaceholders(@NotNull String text, Object... replacements) {
        if (replacements.length == 0) {
            return text;
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String placeholder = matcher.group(1);
            String replacement = null;

            try {
                int index = Integer.parseInt(placeholder);
                if (index >= 0 && index < replacements.length) {
                    Object value = replacements[index];
                    if (value instanceof Component) {
                        // Convert Component to plain text
                        replacement = PlainTextComponentSerializer.plainText().serialize((Component) value);
                    } else {
                        replacement = String.valueOf(value);
                    }
                }
            } catch (NumberFormatException e) {
                for (int i = 0; i < replacements.length - 1; i += 2) {
                    if (placeholder.equals(String.valueOf(replacements[i]))) {
                        replacement = String.valueOf(replacements[i + 1]);
                        break;
                    }
                }
            }

            if (replacement != null) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            } else {
                matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group()));
            }
        }
        matcher.appendTail(result);

        return result.toString();
    }

    @NotNull
    private static Component parseText(@NotNull String text) {
        // Step 1: Convert HEX color codes (%ff55ff_COLOR% → <color:#ff55ff>)
        text = convertHexColors(text);

        // Step 2: Replace & with § for consistency
        text = text.replace("&", "§");

        // Step 3: Parse with appropriate serializer
        if (text.contains("§")) {
            // Legacy color codes
            return LEGACY_SERIALIZER.deserialize(text);
        } else if (text.contains("<") && text.contains(">")) {
            // MiniMessage format (including converted HEX colors)
            try {
                return miniMessage.deserialize(text);
            } catch (Exception e) {
                return Component.text(text);
            }
        } else {
            return Component.text(text);
        }
    }

    @NotNull
    private static String convertHexColors(@NotNull String text) {
        Matcher matcher = HEX_COLOR_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String hexCode = matcher.group(1);
            matcher.appendReplacement(result, "<color:#" + hexCode + ">");
        }
        matcher.appendTail(result);

        // Also handle closing tags if text contains %RESET_COLOR% or similar
        String resultText = result.toString();
        resultText = resultText.replace("%RESET_COLOR%", "<reset>");

        return resultText;
    }

    @NotNull
    private static List<Component> parseList(@NotNull String text) {
        String[] lines = text.split("\\\\n|\\n");
        List<Component> components = new ArrayList<>();
        for (String line : lines) {
            components.add(parseText(line));
        }
        return components;
    }

    public static void reload() {
        langData.clear();
        loadAllLanguages();
    }

    public static int getTotalKeys() {
        return langData.values().stream().mapToInt(Map::size).sum();
    }

    // Array/List support methods
    @NotNull
    private static List<String> getListInternal(@NotNull String key, @NotNull Locale locale) {
        Map<String, String> localeData = langData.get(locale);
        if (localeData == null) {
            localeData = langData.get(Locale.of("en", "US"));
        }

        List<String> result = new ArrayList<>();

        // Check if it's stored as an array ([0], [1], etc.)
        int index = 0;
        while (true) {
            String arrayKey = key + "[" + index + "]";
            String value = localeData != null ? localeData.get(arrayKey) : null;

            if (value == null && localeData != langData.get(Locale.of("en", "US"))) {
                // Try default locale
                Map<String, String> defaultData = langData.get(Locale.of("en", "US"));
                value = defaultData != null ? defaultData.get(arrayKey) : null;
            }

            if (value != null) {
                result.add(value);
                index++;
            } else {
                break;
            }
        }

        // If no array elements found, try single value
        if (result.isEmpty()) {
            String singleValue = getInternal(key, locale);
            if (!singleValue.equals(key)) {
                result.add(singleValue);
            }
        }

        return result;
    }

    @NotNull
    private static List<Component> getLoreInternal(@NotNull String key, @NotNull Locale locale) {
        List<String> lines = getListInternal(key, locale);
        List<Component> components = new ArrayList<>();

        for (String line : lines) {
            components.add(parseText(line));
        }

        return components;
    }

}