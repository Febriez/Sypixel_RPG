package com.febrie.rpg.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
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

    // 로딩 통계
    private static class LoadStatistics {
        int totalFiles = 0;
        int successfulLoads = 0;
        int failedLoads = 0;
        Map<String, Integer> keysPerFile = new HashMap<>();
        Set<String> duplicateKeys = new HashSet<>();
        Set<String> allKeys = new HashSet<>();  // Track all keys for duplicate detection

        void report(JavaPlugin plugin) {
            // Only report errors
            if (failedLoads > 0) {
                plugin.getLogger().warning("[LangManager] Failed to load " + failedLoads + " files");
            }
            
            // Report duplicate keys if found
            if (!duplicateKeys.isEmpty()) {
                plugin.getLogger().warning("[LangManager] Duplicate keys found: " + duplicateKeys.size() + " keys");
                duplicateKeys.forEach(key -> 
                    plugin.getLogger().warning("  - Duplicate: " + key));
            }
        }
    }

    public static void init(@NotNull JavaPlugin plugin) {
        LangManager.plugin = plugin;
        debugMode = false; // 디버그 모드 비활성화
        loadAllLanguages();
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
        LoadStatistics stats = new LoadStatistics();

        // Dynamic loading from plugin JAR
        loadLanguageFromPlugin(localeDir, locale, singleMap, arrayMap, stats);

        singles.put(locale, singleMap);
        arrays.put(locale, arrayMap);

        // Only report if there are errors
        if (stats.failedLoads > 0 || !stats.duplicateKeys.isEmpty()) {
            stats.report(plugin);
        }
    }

    // Keep the old loadJsonResource for backward compatibility
    private static void loadJsonResource(@NotNull String resourcePath, @NotNull String prefix, @NotNull Map<String, Component> singleMap, @NotNull Map<String, List<Component>> arrayMap) {
        LoadStatistics dummyStats = new LoadStatistics();
        loadJsonResource(resourcePath, prefix, singleMap, arrayMap, dummyStats);
    }

    private static void processJsonObject(@NotNull JsonObject obj, @NotNull String prefix, 
                                         @NotNull Map<String, Component> singleMap, 
                                         @NotNull Map<String, List<Component>> arrayMap) {
        processJsonObject(obj, prefix, singleMap, arrayMap, null);
    }
    
    private static void processJsonObject(@NotNull JsonObject obj, @NotNull String prefix, 
                                         @NotNull Map<String, Component> singleMap, 
                                         @NotNull Map<String, List<Component>> arrayMap,
                                         LoadStatistics stats) {
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            String key = prefix + entry.getKey();
            JsonElement value = entry.getValue();

            // Check for duplicate keys
            if (stats != null) {
                if (stats.allKeys.contains(key)) {
                    stats.duplicateKeys.add(key);
                }
                stats.allKeys.add(key);
            }

            if (value.isJsonPrimitive()) {
                Component comp = UnifiedColorUtil.parseComponent(value.getAsString());
                singleMap.put(key, comp);
            } else if (value.isJsonArray()) {
                List<Component> list = new ArrayList<>();
                for (JsonElement elem : value.getAsJsonArray()) {
                    if (elem.isJsonPrimitive()) {
                        list.add(UnifiedColorUtil.parseComponent(elem.getAsString()));
                    }
                }
                if (!list.isEmpty()) {
                    arrayMap.put(key, list);
                }
            } else if (value.isJsonObject()) {
                processJsonObject(value.getAsJsonObject(), key + ".", singleMap, arrayMap, stats);
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
        logWarning("Missing key: " + key + " for locale: " + locale);
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
        return hasKey(key.getKey(), locale);
    }

    public static boolean toggleDebugMode() {
        debugMode = !debugMode;
        return debugMode;
    }
    
    // Logging helper methods
    private static void logDebug(String message) {
        if (debugMode && plugin != null) {
            plugin.getLogger().info("[LangManager] " + message);
        }
    }
    
    private static void logWarning(String message) {
        if (plugin != null) {
            plugin.getLogger().warning("[LangManager] " + message);
        }
    }
    
    private static void logError(String message, Exception e) {
        if (plugin != null) {
            plugin.getLogger().log(Level.WARNING, "[LangManager] " + message, e);
        }
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
     * Dynamically load all JSON files from plugin JAR
     */
    private static void loadLanguageFromPlugin(@NotNull String localeDir, @NotNull Locale locale, @NotNull Map<String, Component> singleMap, @NotNull Map<String, List<Component>> arrayMap, @NotNull LoadStatistics stats) {
        try {
            // Get the plugin JAR file
            File pluginFile = new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());

            if (pluginFile.isFile() && pluginFile.getName().endsWith(".jar")) {
                // Production environment - load from JAR
                loadFromJar(pluginFile, localeDir, singleMap, arrayMap, stats);
            } else {
                // Development environment - load from file system
                loadFromFileSystem(localeDir, singleMap, arrayMap, stats);
            }
        } catch (Exception e) {
            logError("Failed to load language files for " + localeDir, e);
        }
    }

    /**
     * Load JSON files from JAR (production)
     */
    private static void loadFromJar(@NotNull File jarFile, @NotNull String localeDir, @NotNull Map<String, Component> singleMap, @NotNull Map<String, List<Component>> arrayMap, @NotNull LoadStatistics stats) {
        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                // Check if this entry is a JSON file in the correct locale directory
                if (!entry.isDirectory() && entryName.startsWith(localeDir + "/") && entryName.endsWith(".json")) {

                    stats.totalFiles++;

                    // Extract relative path and generate prefix
                    String relativePath = entryName.substring(localeDir.length() + 1);
                    String prefix = generatePrefix(relativePath);

                    // Load the JSON file
                    String resourcePath = "/" + entryName;
                    boolean success = loadJsonResource(resourcePath, prefix, singleMap, arrayMap, stats);

                    if (success) {
                        stats.successfulLoads++;
                    } else {
                        stats.failedLoads++;
                    }
                }
            }
        } catch (IOException e) {
            logError("Failed to read JAR file", e);
        }
    }

    /**
     * Load JSON files from file system (development)
     */
    private static void loadFromFileSystem(@NotNull String localeDir, @NotNull Map<String, Component> singleMap, @NotNull Map<String, List<Component>> arrayMap, @NotNull LoadStatistics stats) {
        try {
            // Try to get resource as URL
            java.net.URL resourceUrl = LangManager.class.getClassLoader().getResource(localeDir);
            if (resourceUrl == null) {
                logWarning("Resource directory not found: " + localeDir);
                return;
            }

            File dir = new File(resourceUrl.toURI());
            if (dir.exists() && dir.isDirectory()) {
                scanDirectory(dir, localeDir, "", singleMap, arrayMap, stats);
            }
        } catch (Exception e) {
            logError("Failed to load from file system", e);
        }
    }

    /**
     * Recursively scan directory for JSON files
     */
    private static void scanDirectory(@NotNull File dir, @NotNull String localeDir, @NotNull String subPath, @NotNull Map<String, Component> singleMap, @NotNull Map<String, List<Component>> arrayMap, @NotNull LoadStatistics stats) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                // Recursively scan subdirectory
                String newSubPath = subPath.isEmpty() ? file.getName() : subPath + "/" + file.getName();
                scanDirectory(file, localeDir, newSubPath, singleMap, arrayMap, stats);
            } else if (file.getName().endsWith(".json")) {
                stats.totalFiles++;

                // Build resource path and prefix
                String relativePath = subPath.isEmpty() ? file.getName() : subPath + "/" + file.getName();
                String resourcePath = "/" + localeDir + "/" + relativePath;
                String prefix = generatePrefix(relativePath);

                // Load the JSON file
                boolean success = loadJsonResource(resourcePath, prefix, singleMap, arrayMap, stats);

                if (success) {
                    stats.successfulLoads++;
                } else {
                    stats.failedLoads++;
                }
            }
        }
    }

    /**
     * Generate key prefix from file path
     * Examples:
     * quest.json -> quest.
     * quests/tutorial/first-steps.json -> quest.tutorial.first_steps.
     * quests/daily/mining.json -> quest.daily.mining.
     */
    private static String generatePrefix(@NotNull String filePath) {
        // Remove .json extension
        String path = filePath.replace(".json", "");

        // Replace path separators with dots
        path = path.replace("/", ".");
        path = path.replace("\\", ".");

        // Replace hyphens with underscores
        path = path.replace("-", "_");

        // Convert quests -> quest (plural to singular)
        if (path.startsWith("quests.")) {
            path = "quest" + path.substring(6);
        }

        // Add trailing dot if not present
        if (!path.endsWith(".")) {
            path = path + ".";
        }

        return path;
    }
    
    /**
     * Validate resource path to prevent path traversal attacks
     * @return true if path is safe to use
     */
    private static boolean isValidResourcePath(@NotNull String resourcePath) {
        // Null/empty check
        if (resourcePath == null || resourcePath.isEmpty()) {
            return false;
        }
        
        // Path must start with /
        if (!resourcePath.startsWith("/")) {
            return false;
        }
        
        // Path must not contain any dangerous characters or sequences
        if (resourcePath.contains("..") || 
            resourcePath.contains("~") || 
            resourcePath.contains("\\") ||  // No backslashes
            resourcePath.contains("%") ||   // No URL encoding
            resourcePath.contains(":") ||   // No drive letters
            resourcePath.contains("*") ||   // No wildcards
            resourcePath.contains("?")) {   // No wildcards
            return false;
        }
        
        // Path must end with .json
        if (!resourcePath.toLowerCase().endsWith(".json")) {
            return false;
        }
        
        // Path must be within allowed locale directories
        String path = resourcePath.substring(1); // Remove leading /
        
        // Check against whitelist of allowed locales
        for (String locale : SUPPORTED_LOCALES) {
            if (path.startsWith(locale + "/")) {
                // Additional check: path should only contain alphanumeric, -, _, / and .
                String remaining = path.substring(locale.length() + 1);
                if (remaining.matches("^[a-zA-Z0-9/_.-]+\\.json$")) {
                    return true;
                }
            }
        }
        
        return false;
    }

    /**
     * Modified loadJsonResource to return success status
     */
    private static boolean loadJsonResource(@NotNull String resourcePath, @NotNull String prefix, @NotNull Map<String, Component> singleMap, @NotNull Map<String, List<Component>> arrayMap, @NotNull LoadStatistics stats) {
        // Validate resource path to prevent path traversal
        if (!isValidResourcePath(resourcePath)) {
            logWarning("Invalid resource path: " + resourcePath);
            return false;
        }

        try (InputStream stream = LangManager.class.getResourceAsStream(resourcePath)) {
            if (stream == null) {
                // Only log if it's not a quest file (they're expected to be in subdirectories)
                if (!resourcePath.contains("/quests/")) {
                    logWarning("Resource not found: " + resourcePath);
                }
                return false;
            }

            try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                int beforeSingle = singleMap.size();
                int beforeArray = arrayMap.size();

                processJsonObject(root, prefix, singleMap, arrayMap, stats);

                int keysAdded = (singleMap.size() - beforeSingle) + (arrayMap.size() - beforeArray);
                stats.keysPerFile.put(resourcePath, keysAdded);

                return true;
            }
        } catch (IOException | JsonSyntaxException e) {
            logError("Failed to load " + resourcePath, e);
            return false;
        }
    }

    /**
     * Validates all enum keys against loaded language files
     */
    public static void validateKeys() {
        if (plugin == null) return;

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
                logWarning("Missing translation for key: " + key.name() + " (" + key.getKey() + ")");
                missingCount++;
            }
        }

        if (missingCount > 0) {
            plugin.getLogger().warning("[LangManager] Found " + missingCount + " missing keys");
        }
    }
}