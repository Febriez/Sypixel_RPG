package com.febrie.rpg.util.lang;

/**
 * Helper class to access all language keys across different categories
 * Provides utility methods for translation and key lookups
 */
public class LangKeyHelper {
    
    /**
     * Get a language key by its string value
     * Searches through all enum categories
     */
    public static ILangKey getKey(String keyString) {
        // Search in GeneralLangKey
        for (GeneralLangKey key : GeneralLangKey.values()) {
            if (key.getKey().equals(keyString)) {
                return key;
            }
        }
        
        // Search in GuiLangKey
        for (GuiLangKey key : GuiLangKey.values()) {
            if (key.getKey().equals(keyString)) {
                return key;
            }
        }
        
        // Search in QuestCommonLangKey
        for (com.febrie.rpg.util.lang.quest.QuestCommonLangKey key : com.febrie.rpg.util.lang.quest.QuestCommonLangKey.values()) {
            if (key.getKey().equals(keyString)) {
                return key;
            }
        }
        
        // Search in ItemLangKey
        for (ItemLangKey key : ItemLangKey.values()) {
            if (key.getKey().equals(keyString)) {
                return key;
            }
        }
        
        // Search in MessageLangKey
        for (MessageLangKey key : MessageLangKey.values()) {
            if (key.getKey().equals(keyString)) {
                return key;
            }
        }
        
        // Search in SystemLangKey
        for (SystemLangKey key : SystemLangKey.values()) {
            if (key.getKey().equals(keyString)) {
                return key;
            }
        }
        
        // Return a default implementation if not found
        return new ILangKey() {
            @Override
            public String getKey() {
                return keyString;
            }
            
            @Override
            public String getDefaultValue() {
                return keyString;
            }
        };
    }
    
    /**
     * Check if a key exists in any category
     */
    public static boolean hasKey(String keyString) {
        // Check GeneralLangKey
        for (GeneralLangKey key : GeneralLangKey.values()) {
            if (key.getKey().equals(keyString)) {
                return true;
            }
        }
        
        // Check GuiLangKey
        for (GuiLangKey key : GuiLangKey.values()) {
            if (key.getKey().equals(keyString)) {
                return true;
            }
        }
        
        // Check QuestCommonLangKey
        for (com.febrie.rpg.util.lang.quest.QuestCommonLangKey key : com.febrie.rpg.util.lang.quest.QuestCommonLangKey.values()) {
            if (key.getKey().equals(keyString)) {
                return true;
            }
        }
        
        // Check ItemLangKey
        for (ItemLangKey key : ItemLangKey.values()) {
            if (key.getKey().equals(keyString)) {
                return true;
            }
        }
        
        // Check MessageLangKey
        for (MessageLangKey key : MessageLangKey.values()) {
            if (key.getKey().equals(keyString)) {
                return true;
            }
        }
        
        // Check SystemLangKey
        for (SystemLangKey key : SystemLangKey.values()) {
            if (key.getKey().equals(keyString)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get all keys from all categories
     */
    public static ILangKey[] getAllKeys() {
        int totalSize = GeneralLangKey.values().length + 
                       GuiLangKey.values().length + 
                       com.febrie.rpg.util.lang.quest.QuestCommonLangKey.values().length +
                       ItemLangKey.values().length +
                       MessageLangKey.values().length +
                       SystemLangKey.values().length;
        
        ILangKey[] allKeys = new ILangKey[totalSize];
        int index = 0;
        
        // Add GeneralLangKey values
        for (GeneralLangKey key : GeneralLangKey.values()) {
            allKeys[index++] = key;
        }
        
        // Add GuiLangKey values
        for (GuiLangKey key : GuiLangKey.values()) {
            allKeys[index++] = key;
        }
        
        // Add QuestCommonLangKey values
        for (com.febrie.rpg.util.lang.quest.QuestCommonLangKey key : com.febrie.rpg.util.lang.quest.QuestCommonLangKey.values()) {
            allKeys[index++] = key;
        }
        
        // Add ItemLangKey values
        for (ItemLangKey key : ItemLangKey.values()) {
            allKeys[index++] = key;
        }
        
        // Add MessageLangKey values
        for (MessageLangKey key : MessageLangKey.values()) {
            allKeys[index++] = key;
        }
        
        // Add SystemLangKey values
        for (SystemLangKey key : SystemLangKey.values()) {
            allKeys[index++] = key;
        }
        
        return allKeys;
    }
}