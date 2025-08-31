package com.febrie.rpg.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import com.febrie.rpg.util.LangKey;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

/**
 * 간결한 언어 관리자 v2.0
 * Component 객체를 직접 저장하여 런타임 파싱 최소화
 */
public class LangManager {
    private static JavaPlugin plugin;
    private static final Map<Locale, Map<String, Component>> singles = new HashMap<>();
    private static final Map<Locale, Map<String, List<Component>>> arrays = new HashMap<>();
    private static Locale defaultLocale = Locale.US;
    private static boolean debugMode = false;

    // 지원하는 언어 목록
    private static final String[] SUPPORTED_LOCALES = {"ko_kr", "en_us", "ja_jp"};

    // 각 언어별 JSON 파일 목록
    private static final String[] JSON_FILES = {"biome.json", "commands.json", "currency.json", "dialog.json", "general.json", "gui.json", "island.json", "items.json", "job.json", "messages.json", "quest.json", "stat.json", "status.json", "talent.json"};

    public static void init(@NotNull JavaPlugin plugin) {
        LangManager.plugin = plugin;
        debugMode = true; // 초기화 시 로깅 활성화
        plugin.getLogger().info("[LangManager] Starting language initialization...");
        loadAllLanguages();
        debugMode = false; // 로딩 후 비활성화
        
        // 로드된 키 요약 출력
        plugin.getLogger().info("[LangManager] === Language Loading Summary ===");
        for (Locale locale : singles.keySet()) {
            plugin.getLogger().info("[LangManager] Locale: " + locale);
            Map<String, Component> singleMap = singles.get(locale);
            Map<String, List<Component>> arrayMap = arrays.get(locale);
            
            // 카테고리별로 키 개수 카운트
            Map<String, Integer> categoryCounts = new HashMap<>();
            for (String key : singleMap.keySet()) {
                String category = key.split("\\.")[0];
                categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
            }
            for (String key : arrayMap.keySet()) {
                String category = key.split("\\.")[0];
                categoryCounts.put(category + "_array", categoryCounts.getOrDefault(category + "_array", 0) + 1);
            }
            
            for (Map.Entry<String, Integer> catEntry : categoryCounts.entrySet()) {
                plugin.getLogger().info("  - " + catEntry.getKey() + ": " + catEntry.getValue() + " keys");
            }
        }
        plugin.getLogger().info("[LangManager] === End of Summary ===");
        
        // Validate all enum keys
        validateKeys();
    }

    private static void loadAllLanguages() {
        for (String localeStr : SUPPORTED_LOCALES) {
            Locale locale = parseLocale(localeStr);
            loadLanguage(localeStr, locale);
        }
    }

    private static void loadLanguage(@NotNull String localeDir, @NotNull Locale locale) {
        Map<String, Component> singleMap = new HashMap<>();
        Map<String, List<Component>> arrayMap = new HashMap<>();

        for (String jsonFile : JSON_FILES) {
            String resourcePath = "/" + localeDir + "/" + jsonFile;
            String prefix = jsonFile.replace(".json", "") + ".";
            loadJsonResource(resourcePath, prefix, singleMap, arrayMap);
        }

        singles.put(locale, singleMap);
        arrays.put(locale, arrayMap);
        plugin.getLogger()
                .info("Loaded " + singleMap.size() + " texts and " + arrayMap.size() + " lists for " + locale);
    }

    private static void loadJsonResource(@NotNull String resourcePath, @NotNull String prefix, @NotNull Map<String, Component> singleMap, @NotNull Map<String, List<Component>> arrayMap) {
        try (InputStream stream = LangManager.class.getResourceAsStream(resourcePath)) {
            if (stream == null) {
                plugin.getLogger().warning("Resource not found: " + resourcePath);
                return;
            }

            try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                processJsonObject(root, prefix, singleMap, arrayMap);
            }
        } catch (IOException | JsonSyntaxException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to load " + resourcePath, e);
        }
    }

    private static void processJsonObject(@NotNull JsonObject obj, @NotNull String prefix, @NotNull Map<String, Component> singleMap, @NotNull Map<String, List<Component>> arrayMap) {
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            String key = prefix + entry.getKey();
            JsonElement value = entry.getValue();

            if (value.isJsonPrimitive()) {
                Component comp = UnifiedColorUtil.parseComponent(value.getAsString());
                singleMap.put(key, comp);
                if (debugMode) {
                    plugin.getLogger().info("[LangManager] Registered text key: " + key);
                }
            } else if (value.isJsonArray()) {
                List<Component> list = new ArrayList<>();
                for (JsonElement elem : value.getAsJsonArray()) {
                    if (elem.isJsonPrimitive()) {
                        list.add(UnifiedColorUtil.parseComponent(elem.getAsString()));
                    }
                }
                if (!list.isEmpty()) {
                    arrayMap.put(key, list);
                    if (debugMode) {
                        plugin.getLogger().info("[LangManager] Registered array key: " + key + " (size: " + list.size() + ")");
                    }
                }
            } else if (value.isJsonObject()) {
                processJsonObject(value.getAsJsonObject(), key + ".", singleMap, arrayMap);
            }
        }
    }

    @NotNull
    public static Component text(@NotNull LangKey key) {
        return textInternal(key.getKey(), defaultLocale);
    }

    @NotNull
    public static Component text(@NotNull LangKey key, @NotNull org.bukkit.entity.Player player) {
        return textInternal(key.getKey(), player.locale());
    }

    @NotNull
    public static Component text(@NotNull LangKey key, @NotNull Locale locale) {
        return textInternal(key.getKey(), locale);
    }
    
    @NotNull
    public static Component text(@NotNull LangKey key, @NotNull Locale locale, Object... args) {
        return LangHelper.text(key, locale, args);
    }
    
    @NotNull
    public static Component text(@NotNull LangKey key, @NotNull org.bukkit.entity.Player player, Object... args) {
        return LangHelper.text(key, player.locale(), args);
    }

    
    @NotNull
    private static Component textInternal(@NotNull String key, @NotNull Locale locale) {
        Map<String, Component> localeMap = singles.get(locale);
        if (localeMap != null) {
            Component result = localeMap.get(key);
            if (result != null) return result;
        }

        if (locale != defaultLocale) {
            return textInternal(key, defaultLocale);
        }

        // 키가 없을 때 로깅
        if (plugin != null) {
            plugin.getLogger().warning("[LangManager] Missing key: " + key + " for locale: " + locale);
        }
        return Component.text(key);
    }

    @NotNull
    public static List<Component> list(@NotNull LangKey key) {
        return listInternal(key.getKey(), defaultLocale);
    }

    @NotNull
    public static List<Component> list(@NotNull LangKey key, @NotNull org.bukkit.entity.Player player) {
        return listInternal(key.getKey(), player.locale());
    }

    @NotNull
    public static List<Component> list(@NotNull LangKey key, @NotNull Locale locale) {
        return listInternal(key.getKey(), locale);
    }
    
    @NotNull
    public static List<Component> list(@NotNull LangKey key, @NotNull Locale locale, Object... args) {
        return LangHelper.list(key, locale, args);
    }
    
    @NotNull
    public static List<Component> list(@NotNull LangKey key, @NotNull org.bukkit.entity.Player player, Object... args) {
        return LangHelper.list(key, player.locale(), args);
    }

    @NotNull
    private static List<Component> listInternal(@NotNull String key, @NotNull Locale locale) {
        Map<String, List<Component>> localeMap = arrays.get(locale);
        if (localeMap != null) {
            List<Component> result = localeMap.get(key);
            if (result != null) return new ArrayList<>(result);
        }

        if (locale != defaultLocale) {
            return listInternal(key, defaultLocale);
        }

        return Collections.singletonList(Component.text(key));
    }

    @NotNull
    private static Locale parseLocale(@NotNull String str) {
        String[] parts = str.split("_");
        if (parts.length == 2) {
            return Locale.of(parts[0], parts[1].toUpperCase());
        }
        return Locale.US;
    }

    public static void setDefaultLocale(@NotNull Locale locale) {
        defaultLocale = locale;
    }

    public static void reload() {
        singles.clear();
        arrays.clear();
        loadAllLanguages();
    }

    // Key validation method
    public static boolean hasKey(@NotNull LangKey key, @NotNull Locale locale) {
        String keyStr = key.getKey();
        Map<String, Component> singleMap = singles.get(locale);
        if (singleMap != null && singleMap.containsKey(keyStr)) return true;

        Map<String, List<Component>> arrayMap = arrays.get(locale);
        return arrayMap != null && arrayMap.containsKey(keyStr);
    }

    public static boolean toggleDebugMode() {
        debugMode = !debugMode;
        return debugMode;
    }
    
    // Minimal compatibility layer for existing code
    // These should be migrated to use LangKey in the future
    @NotNull
    public static Component getComponent(@NotNull String key, @NotNull Locale locale) {
        return textInternal(key, locale);
    }
    
    @NotNull
    public static Component getComponent(@NotNull String key, @NotNull Locale locale, Object... args) {
        Component base = textInternal(key, locale);
        return LangHelper.replacePlaceholders(base, args);
    }
    
    public static boolean hasKey(@NotNull String key, @NotNull Locale locale) {
        Map<String, Component> singleMap = singles.get(locale);
        if (singleMap != null && singleMap.containsKey(key)) return true;
        Map<String, List<Component>> arrayMap = arrays.get(locale);
        return arrayMap != null && arrayMap.containsKey(key);
    }
    
    @NotNull 
    public static Component get(@NotNull String key, @NotNull org.bukkit.entity.Player player) {
        return textInternal(key, player.locale());
    }
    
    @NotNull
    public static Component get(@NotNull String key, @NotNull org.bukkit.entity.Player player, Object... args) {
        Component base = textInternal(key, player.locale());
        return LangHelper.replacePlaceholders(base, args);
    }
    
    public static Set<String> getAllKeys(@NotNull Locale locale) {
        Set<String> keys = new HashSet<>();
        Map<String, Component> singleMap = singles.get(locale);
        if (singleMap != null) keys.addAll(singleMap.keySet());
        Map<String, List<Component>> arrayMap = arrays.get(locale);
        if (arrayMap != null) keys.addAll(arrayMap.keySet());
        return keys;
    }
    
    /**
     * Validates all enum keys against loaded language files
     */
    public static void validateKeys() {
        if (plugin == null) return;
        
        plugin.getLogger().info("[LangManager] Validating language keys...");
        int missingCount = 0;
        
        for (LangKey key : LangKey.values()) {
            boolean found = false;
            for (Locale locale : singles.keySet()) {
                if (hasKey(key, locale)) {
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                plugin.getLogger().warning("[LangManager] Missing translation for key: " + key.name() + " (" + key.getKey() + ")");
                missingCount++;
            }
        }
        
        if (missingCount == 0) {
            plugin.getLogger().info("[LangManager] All language keys validated successfully!");
        } else {
            plugin.getLogger().warning("[LangManager] Found " + missingCount + " missing keys");
        }
    }
}