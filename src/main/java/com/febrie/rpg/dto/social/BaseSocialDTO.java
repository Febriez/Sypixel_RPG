package com.febrie.rpg.dto.social;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

/**
 * Base class for social DTOs with common utility methods
 * 
 * @author Febrie
 */
public abstract class BaseSocialDTO {
    
    /**
     * Safely parse UUID from map
     */
    @NotNull
    protected static UUID parseUUID(@NotNull Map<String, Object> map, @NotNull String key) {
        String uuidStr = (String) map.get(key);
        if (uuidStr == null) {
            return UUID.randomUUID(); // Default fallback
        }
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            return UUID.randomUUID(); // Fallback on parse error
        }
    }
    
    /**
     * Safely get string from map with default
     */
    @NotNull
    protected static String getString(@NotNull Map<String, Object> map, @NotNull String key, @NotNull String defaultValue) {
        Object value = map.get(key);
        return value != null ? value.toString() : defaultValue;
    }
    
    /**
     * Safely get nullable string from map
     */
    @Nullable
    protected static String getNullableString(@NotNull Map<String, Object> map, @NotNull String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }
    
    /**
     * Safely get boolean from map
     */
    protected static boolean getBoolean(@NotNull Map<String, Object> map, @NotNull String key, boolean defaultValue) {
        Object value = map.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return defaultValue;
    }
    
    /**
     * Add non-null value to map
     */
    protected static void putIfNotNull(@NotNull Map<String, Object> map, @NotNull String key, @Nullable Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }
}