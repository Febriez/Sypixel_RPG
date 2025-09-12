package com.febrie.rpg.util;

import com.febrie.rpg.util.lang.ILangKey;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.entity.Player;
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
import java.util.stream.Collectors;

/**
 * 통합 언어 관리자 v3.0
 * LangHelper 기능 통합 및 최적화
 */
public class LangManager {
    private static JavaPlugin plugin;
    private static final Map<Locale, Map<String, Component>> singles = new HashMap<>();
    private static final Map<Locale, Map<String, List<Component>>> arrays = new HashMap<>();
    private static Locale defaultLocale = Locale.US;
    private static final String[] SUPPORTED_LOCALES = {"ko_kr", "en_us", "ja_jp"};

    public static void init(@NotNull JavaPlugin plugin) {
        LangManager.plugin = plugin;
        loadAllLanguages();
        validateKeys();
    }

    // ===== Public API Methods =====

    // Support for ILangKey interface
    @NotNull
    public static Component text(@NotNull ILangKey key) {
        return textInternal(key.getKey(), defaultLocale);
    }

    @NotNull
    public static Component text(@NotNull ILangKey key, @NotNull Player player) {
        return textInternal(key.getKey(), player.locale());
    }
    
    // Legacy support for LangKey enum references
    @NotNull
    public static Component text(@NotNull LangKey key) {
        return text((ILangKey) key);
    }

    @NotNull
    public static Component text(@NotNull LangKey key, @NotNull Player player) {
        return text((ILangKey) key, player);
    }

    // ILangKey versions with locale and args
    @NotNull
    public static Component text(@NotNull ILangKey key, @NotNull Locale locale) {
        return textInternal(key.getKey(), locale);
    }

    @NotNull
    public static Component text(@NotNull ILangKey key, @NotNull Locale locale, Object... args) {
        Component base = textInternal(key.getKey(), locale);
        return replacePlaceholders(base, args);
    }

    @NotNull
    public static Component text(@NotNull ILangKey key, @NotNull Player player, Object... args) {
        return text(key, player.locale(), args);
    }
    
    // Legacy LangKey versions
    @NotNull
    public static Component text(@NotNull LangKey key, @NotNull Locale locale) {
        return text((ILangKey) key, locale);
    }

    @NotNull
    public static Component text(@NotNull LangKey key, @NotNull Locale locale, Object... args) {
        return text((ILangKey) key, locale, args);
    }

    @NotNull
    public static Component text(@NotNull LangKey key, @NotNull Player player, Object... args) {
        return text((ILangKey) key, player, args);
    }

    // ILangKey versions of list methods
    @NotNull
    public static List<Component> list(@NotNull ILangKey key) {
        return listInternal(key.getKey(), defaultLocale);
    }

    @NotNull
    public static List<Component> list(@NotNull ILangKey key, @NotNull Player player) {
        return listInternal(key.getKey(), player.locale());
    }

    @NotNull
    public static List<Component> list(@NotNull ILangKey key, @NotNull Locale locale) {
        return listInternal(key.getKey(), locale);
    }

    @NotNull
    public static List<Component> list(@NotNull ILangKey key, @NotNull Locale locale, Object... args) {
        List<Component> base = listInternal(key.getKey(), locale);
        return base.stream().map(comp -> replacePlaceholders(comp, args)).collect(Collectors.toList());
    }

    @NotNull
    public static List<Component> list(@NotNull ILangKey key, @NotNull Player player, Object... args) {
        return list(key, player.locale(), args);
    }
    
    // Legacy LangKey versions
    @NotNull
    public static List<Component> list(@NotNull LangKey key) {
        return list((ILangKey) key);
    }

    @NotNull
    public static List<Component> list(@NotNull LangKey key, @NotNull Player player) {
        return list((ILangKey) key, player);
    }

    @NotNull
    public static List<Component> list(@NotNull LangKey key, @NotNull Locale locale) {
        return list((ILangKey) key, locale);
    }

    @NotNull
    public static List<Component> list(@NotNull LangKey key, @NotNull Locale locale, Object... args) {
        return list((ILangKey) key, locale, args);
    }

    @NotNull
    public static List<Component> list(@NotNull LangKey key, @NotNull Player player, Object... args) {
        return list((ILangKey) key, player, args);
    }

    // ILangKey version of hasKey
    public static boolean hasKey(@NotNull ILangKey key, @NotNull Locale locale) {
        String keyStr = key.getKey();
        Map<String, Component> singleMap = singles.get(locale);
        if (singleMap != null && singleMap.containsKey(keyStr)) return true;
        Map<String, List<Component>> arrayMap = arrays.get(locale);
        return arrayMap != null && arrayMap.containsKey(keyStr);
    }
    
    // Legacy LangKey version
    public static boolean hasKey(@NotNull LangKey key, @NotNull Locale locale) {
        return hasKey((ILangKey) key, locale);
    }
    
    // Internal method that was getting called
    private static boolean hasKeyInternal(String keyStr, Locale locale) {
        String keyString = keyStr;
        Map<String, Component> singleMap = singles.get(locale);
        if (singleMap != null && singleMap.containsKey(keyStr)) return true;
        Map<String, List<Component>> arrayMap = arrays.get(locale);
        return arrayMap != null && arrayMap.containsKey(keyStr);
    }

    public static void setDefaultLocale(@NotNull Locale locale) {
        defaultLocale = locale;
    }

    public static void reload() {
        singles.clear();
        arrays.clear();
        loadAllLanguages();
    }

    // ===== Internal Methods (Public for Direct Migration) =====

    @NotNull
    public static Component textInternal(@NotNull String key, @NotNull Locale locale) {
        Map<String, Component> localeMap = singles.get(locale);
        if (localeMap != null) {
            Component result = localeMap.get(key);
            if (result != null) return result;
        }

        if (locale != defaultLocale) {
            return textInternal(key, defaultLocale);
        }

        logWarning("Missing key: " + key);
        return Component.text(key);
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
    public static Component replacePlaceholders(@NotNull Component component, Object... args) {
        Component result = component;
        for (int i = 0; i < args.length; i++) {
            String placeholder = "{" + i + "}";
            Component replacementComponent;

            if (args[i] instanceof Component) {
                replacementComponent = (Component) args[i];
            } else {
                replacementComponent = Component.text(String.valueOf(args[i]));
            }

            TextReplacementConfig config = TextReplacementConfig.builder().matchLiteral(placeholder)
                    .replacement(replacementComponent).build();
            result = result.replaceText(config);
        }
        return result;
    }

    // ===== Loading Methods =====

    private static void loadAllLanguages() {
        plugin.getLogger().info("[LangManager] ============================================");
        plugin.getLogger().info("[LangManager] Starting language loading...");
        plugin.getLogger().info("[LangManager] Debug mode: ENABLED");
        plugin.getLogger().info("[LangManager] ============================================");
        
        for (String localeStr : SUPPORTED_LOCALES) {
            Locale locale = parseLocale(localeStr);
            plugin.getLogger().info("[LangManager] Loading language: " + localeStr + " -> Locale: " + locale);
            loadLanguage(localeStr, locale);
        }
        
        plugin.getLogger().info("[LangManager] ============================================");
        plugin.getLogger().info("[LangManager] Language loading complete.");
        plugin.getLogger().info("[LangManager] Total loaded locales: " + singles.size());
        for (Map.Entry<Locale, Map<String, Component>> entry : singles.entrySet()) {
            plugin.getLogger().info("[LangManager]   - " + entry.getKey() + ": " + entry.getValue().size() + " single keys");
        }
        for (Map.Entry<Locale, Map<String, List<Component>>> entry : arrays.entrySet()) {
            plugin.getLogger().info("[LangManager]   - " + entry.getKey() + ": " + entry.getValue().size() + " array keys");
        }
        plugin.getLogger().info("[LangManager] ============================================");
    }

    private static void loadLanguage(@NotNull String localeDir, @NotNull Locale locale) {
        Map<String, Component> singleMap = new HashMap<>();
        Map<String, List<Component>> arrayMap = new HashMap<>();

        try {
            File pluginFile = new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());

            if (pluginFile.isFile() && pluginFile.getName().endsWith(".jar")) {
                plugin.getLogger().info("[LangManager] Loading from JAR: " + localeDir);
                loadFromJar(pluginFile, localeDir, singleMap, arrayMap);
            } else {
                plugin.getLogger().info("[LangManager] Loading from filesystem: " + localeDir);
                loadFromFileSystem(localeDir, singleMap, arrayMap);
            }
        } catch (Exception e) {
            logError("Failed to load language: " + localeDir, e);
        }

        singles.put(locale, singleMap);
        arrays.put(locale, arrayMap);
        plugin.getLogger()
                .info("[LangManager] Loaded " + singleMap.size() + " single keys and " + arrayMap.size() + " array keys for " + localeDir);
    }

    private static void loadFromJar(@NotNull File jarFile, @NotNull String localeDir, @NotNull Map<String, Component> singleMap, @NotNull Map<String, List<Component>> arrayMap) {
        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                if (!entry.isDirectory() && entryName.startsWith(localeDir + "/") && entryName.endsWith(".json")) {
                    String relativePath = entryName.substring(localeDir.length() + 1);
                    String prefix = generatePrefix(relativePath);
                    loadJsonResource("/" + entryName, prefix, singleMap, arrayMap);
                }
            }
        } catch (IOException e) {
            logError("Failed to read JAR", e);
        }
    }

    private static void loadFromFileSystem(@NotNull String localeDir, @NotNull Map<String, Component> singleMap, @NotNull Map<String, List<Component>> arrayMap) {
        try {
            java.net.URL resourceUrl = LangManager.class.getClassLoader().getResource(localeDir);
            if (resourceUrl != null) {
                File dir = new File(resourceUrl.toURI());
                if (dir.exists() && dir.isDirectory()) {
                    scanDirectory(dir, localeDir, "", singleMap, arrayMap);
                }
            }
        } catch (Exception e) {
            logError("Failed to load from file system", e);
        }
    }

    private static void scanDirectory(@NotNull File dir, @NotNull String localeDir, @NotNull String subPath, @NotNull Map<String, Component> singleMap, @NotNull Map<String, List<Component>> arrayMap) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            String s = subPath.isEmpty() ? file.getName() : subPath + "/" + file.getName();
            if (file.isDirectory()) {
                scanDirectory(file, localeDir, s, singleMap, arrayMap);
            } else if (file.getName().endsWith(".json")) {
                String resourcePath = "/" + localeDir + "/" + s;
                String prefix = generatePrefix(s);
                loadJsonResource(resourcePath, prefix, singleMap, arrayMap);
            }
        }
    }

    private static void loadJsonResource(@NotNull String resourcePath, @NotNull String prefix, @NotNull Map<String, Component> singleMap, @NotNull Map<String, List<Component>> arrayMap) {
        try (InputStream stream = LangManager.class.getResourceAsStream(resourcePath)) {
            if (stream == null) {
                plugin.getLogger().warning("[LangManager] ❌ Resource not found: " + resourcePath);
                return;
            }

            try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                // Extract filename for better logging
                String fileName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
                
                int beforeSingle = singleMap.size();
                int beforeArray = arrayMap.size();
                processJsonObject(root, prefix, singleMap, arrayMap);
                int addedSingle = singleMap.size() - beforeSingle;
                int addedArray = arrayMap.size() - beforeArray;
                
                if (addedSingle > 0 || addedArray > 0) {
                    plugin.getLogger().info("[LangManager] ✅ " + fileName + ": " + addedSingle + " singles, " + addedArray + " arrays (prefix: " + prefix + ")");
                } else {
                    plugin.getLogger().warning("[LangManager] ⚠️ " + fileName + ": No keys loaded! Check JSON structure.");
                }
            }
        } catch (IOException | JsonSyntaxException e) {
            plugin.getLogger().severe("[LangManager] ❌ Failed to load " + resourcePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void processJsonObject(@NotNull JsonObject obj, @NotNull String prefix, @NotNull Map<String, Component> singleMap, @NotNull Map<String, List<Component>> arrayMap) {
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            String key = prefix + entry.getKey();
            JsonElement value = entry.getValue();

            if (value.isJsonPrimitive()) {
                singleMap.put(key, UnifiedColorUtil.parseComponent(value.getAsString()));
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
                processJsonObject(value.getAsJsonObject(), key + ".", singleMap, arrayMap);
            }
        }
    }

    @NotNull
    private static String generatePrefix(@NotNull String filePath) {
        // Remove .json extension and convert path separators to dots
        String path = filePath.replace(".json", "").replace("/", ".").replace("\\", ".");

        // Replace hyphens with underscores for consistency with LangKey enum
        path = path.replace("-", "_");

        // Handle quest files specially - they should start with "quest" not "quests"
        if (path.startsWith("quests.")) {
            path = "quest." + path.substring(7); // "quests." is 7 chars
        }

        // Log the prefix generation for debugging
        plugin.getLogger().fine("[LangManager] Generated prefix: " + filePath + " -> " + path);

        return path.endsWith(".") ? path : path + ".";
    }

    @NotNull
    private static Locale parseLocale(@NotNull String str) {
        String[] parts = str.split("_");
        if (parts.length == 2) {
            return Locale.of(parts[0], parts[1].toUpperCase());
        }
        return Locale.US;
    }

    private static void validateKeys() {
        if (plugin == null) return;

        plugin.getLogger().info("[LangManager] ============================================");
        plugin.getLogger().info("[LangManager] Starting key validation...");
        
        // Get all ILangKey values from all enums
        List<ILangKey> allKeys = new ArrayList<>();
        allKeys.addAll(Arrays.asList(com.febrie.rpg.util.lang.GeneralLangKey.values()));
        allKeys.addAll(Arrays.asList(com.febrie.rpg.util.lang.GuiLangKey.values()));
        allKeys.addAll(Arrays.asList(com.febrie.rpg.util.lang.quest.QuestCommonLangKey.values()));
        allKeys.addAll(Arrays.asList(com.febrie.rpg.util.lang.ItemLangKey.values()));
        allKeys.addAll(Arrays.asList(com.febrie.rpg.util.lang.MessageLangKey.values()));
        allKeys.addAll(Arrays.asList(com.febrie.rpg.util.lang.SystemLangKey.values()));
        
        plugin.getLogger().info("[LangManager] Total keys to validate: " + allKeys.size());
        plugin.getLogger().info("[LangManager] - GeneralLangKey: " + com.febrie.rpg.util.lang.GeneralLangKey.values().length);
        plugin.getLogger().info("[LangManager] - GuiLangKey: " + com.febrie.rpg.util.lang.GuiLangKey.values().length);
        plugin.getLogger().info("[LangManager] - QuestCommonLangKey: " + com.febrie.rpg.util.lang.quest.QuestCommonLangKey.values().length);
        plugin.getLogger().info("[LangManager] - ItemLangKey: " + com.febrie.rpg.util.lang.ItemLangKey.values().length);
        plugin.getLogger().info("[LangManager] - MessageLangKey: " + com.febrie.rpg.util.lang.MessageLangKey.values().length);
        plugin.getLogger().info("[LangManager] - SystemLangKey: " + com.febrie.rpg.util.lang.SystemLangKey.values().length);

        int missingCount = 0;
        int foundCount = 0;
        List<String> missingKeys = new ArrayList<>();
        Map<String, List<String>> missingByCategory = new HashMap<>();
        Map<String, Integer> foundByCategory = new HashMap<>();

        for (ILangKey key : allKeys) {
            boolean found = false;
            String keyName = key.getKey();
            
            // Check in default locale (en_us)
            Locale defaultLoc = parseLocale("en_us");
            if (hasKey(key, defaultLoc)) {
                found = true;
            } else {
                // Try to check with direct string lookup
                Map<String, Component> singleMap = singles.get(defaultLoc);
                Map<String, List<Component>> arrayMap = arrays.get(defaultLoc);
                
                if (singleMap != null && singleMap.containsKey(keyName)) {
                    found = true;
                } else if (arrayMap != null && arrayMap.containsKey(keyName)) {
                    found = true;
                }
            }
            
            String category = keyName.split("\\.")[0];
            if (!found) {
                missingKeys.add(keyName);
                missingCount++;
                missingByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(keyName);
            } else {
                foundCount++;
                foundByCategory.merge(category, 1, Integer::sum);
            }
        }

        // Report summary
        plugin.getLogger().info("[LangManager] ============================================");
        plugin.getLogger().info("[LangManager] VALIDATION SUMMARY:");
        plugin.getLogger().info("[LangManager] ✅ Successfully loaded: " + foundCount + "/" + allKeys.size() + " keys");
        
        if (foundByCategory.size() > 0) {
            plugin.getLogger().info("[LangManager] Successfully loaded by category:");
            for (Map.Entry<String, Integer> entry : foundByCategory.entrySet()) {
                plugin.getLogger().info("[LangManager]   " + entry.getKey() + ": " + entry.getValue() + " keys loaded");
            }
        }
        
        if (missingCount > 0) {
            plugin.getLogger().warning("[LangManager] ❌ Missing: " + missingCount + "/" + allKeys.size() + " keys");
            plugin.getLogger().warning("[LangManager] Missing keys by category:");
            
            for (Map.Entry<String, List<String>> entry : missingByCategory.entrySet()) {
                plugin.getLogger().warning("[LangManager]   " + entry.getKey() + ": " + entry.getValue().size() + " missing");
                // Only show first 5 missing keys per category to avoid spam
                int shown = 0;
                for (String key : entry.getValue()) {
                    if (shown < 5) {
                        plugin.getLogger().warning("[LangManager]     - " + key);
                        shown++;
                    }
                }
                if (entry.getValue().size() > 5) {
                    plugin.getLogger().warning("[LangManager]     ... and " + (entry.getValue().size() - 5) + " more");
                }
            }
        } else {
            plugin.getLogger().info("[LangManager] ✅ All translation keys validated successfully!");
        }
        
        // Debug: Show loaded keys in each locale
        plugin.getLogger().info("[LangManager] ============================================");
        plugin.getLogger().info("[LangManager] LOADED KEYS PER LOCALE:");
        for (Map.Entry<Locale, Map<String, Component>> entry : singles.entrySet()) {
            plugin.getLogger().info("[LangManager]   " + entry.getKey() + ": " + entry.getValue().size() + " single keys");
        }
        for (Map.Entry<Locale, Map<String, List<Component>>> entry : arrays.entrySet()) {
            plugin.getLogger().info("[LangManager]   " + entry.getKey() + ": " + entry.getValue().size() + " array keys");
        }
        
        plugin.getLogger().info("[LangManager] ============================================");
    }


    // ===== Logging Methods =====

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
}