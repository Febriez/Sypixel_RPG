// src/main/java/com/febrie/rpg/util/ColorUtil.java
package com.febrie.rpg.util;

import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for custom colors not available in NamedTextColor
 * Provides commonly used colors for the RPG plugin
 *
 * @author Febrie, CoffeeTory
 */
public final class ColorUtil {

    private ColorUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // === Basic Extended Colors ===
    /**
     * Orange color (255, 165, 0)
     */
    public static final TextColor ORANGE = TextColor.color(255, 165, 0);

    /**
     * Pink color (255, 192, 203)
     */
    public static final TextColor PINK = TextColor.color(255, 192, 203);

    /**
     * Brown color (139, 69, 19)
     */
    public static final TextColor BROWN = TextColor.color(139, 69, 19);

    /**
     * Lime color (50, 205, 50)
     */
    public static final TextColor LIME = TextColor.color(50, 205, 50);

    /**
     * Cyan color (0, 255, 255)
     */
    public static final TextColor CYAN = TextColor.color(0, 255, 255);

    // === RPG Theme Colors ===
    /**
     * Legendary item color (255, 215, 0) - Gold
     */
    public static final TextColor LEGENDARY = TextColor.color(255, 215, 0);

    /**
     * Epic item color (163, 53, 238) - Purple
     */
    public static final TextColor EPIC = TextColor.color(163, 53, 238);

    /**
     * Rare item color (0, 112, 221) - Blue
     */
    public static final TextColor RARE = TextColor.color(0, 112, 221);

    /**
     * Uncommon item color (30, 255, 0) - Green
     */
    public static final TextColor UNCOMMON = TextColor.color(30, 255, 0);

    /**
     * Common item color (157, 157, 157) - Gray
     */
    public static final TextColor COMMON = TextColor.color(157, 157, 157);

    // === Status Colors ===
    /**
     * Success color (34, 139, 34) - Forest Green
     */
    public static final TextColor SUCCESS = TextColor.color(34, 139, 34);

    /**
     * Warning color (255, 140, 0) - Dark Orange
     */
    public static final TextColor WARNING = TextColor.color(255, 140, 0);

    /**
     * Error color (220, 20, 60) - Crimson
     */
    public static final TextColor ERROR = TextColor.color(220, 20, 60);

    /**
     * Info color (70, 130, 180) - Steel Blue
     */
    public static final TextColor INFO = TextColor.color(70, 130, 180);

    // === Stat Colors ===
    /**
     * Health color (255, 85, 85) - Light Red
     */
    public static final TextColor HEALTH = TextColor.color(255, 85, 85);

    /**
     * Mana color (85, 85, 255) - Light Blue
     */
    public static final TextColor MANA = TextColor.color(85, 85, 255);

    /**
     * Stamina color (255, 255, 85) - Light Yellow
     */
    public static final TextColor STAMINA = TextColor.color(255, 255, 85);

    /**
     * Experience color (127, 255, 212) - Aquamarine
     */
    public static final TextColor EXPERIENCE = TextColor.color(127, 255, 212);

    // === GUI Colors ===
    /**
     * Positive action color (144, 238, 144) - Light Green
     */
    public static final TextColor POSITIVE = TextColor.color(144, 238, 144);

    /**
     * Negative action color (255, 182, 193) - Light Pink
     */
    public static final TextColor NEGATIVE = TextColor.color(255, 182, 193);

    /**
     * Neutral color (211, 211, 211) - Light Gray
     */
    public static final TextColor NEUTRAL = TextColor.color(211, 211, 211);

    // === Minecraft-inspired Colors ===
    /**
     * Netherite color (68, 58, 59)
     */
    public static final TextColor NETHERITE = TextColor.color(68, 58, 59);

    /**
     * Diamond color (185, 242, 255)
     */
    public static final TextColor DIAMOND = TextColor.color(185, 242, 255);

    /**
     * Emerald color (80, 220, 100)
     */
    public static final TextColor EMERALD = TextColor.color(80, 220, 100);

    /**
     * Copper color (184, 115, 51)
     */
    public static final TextColor COPPER = TextColor.color(184, 115, 51);

    /**
     * Creates a custom color from RGB values
     *
     * @param red   Red component (0-255)
     * @param green Green component (0-255)
     * @param blue  Blue component (0-255)
     * @return TextColor instance
     */
    public static @NotNull TextColor rgb(int red, int green, int blue) {
        return TextColor.color(red, green, blue);
    }

    /**
     * Creates a custom color from hex string
     *
     * @param hex Hex color string (e.g., "#FF5733" or "FF5733")
     * @return TextColor instance
     * @throws IllegalArgumentException if hex string is invalid
     */
    public static @NotNull TextColor hex(@NotNull String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        if (hex.length() != 6) {
            throw new IllegalArgumentException("Hex color must be 6 characters long");
        }

        try {
            int color = Integer.parseInt(hex, 16);
            return TextColor.color(color);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex color: " + hex, e);
        }
    }

    /**
     * Creates a gradient color between two colors
     *
     * @param start Start color
     * @param end   End color
     * @param ratio Ratio between colors (0.0 = start, 1.0 = end)
     * @return Interpolated color
     */
    public static @NotNull TextColor gradient(@NotNull TextColor start, @NotNull TextColor end, double ratio) {
        ratio = Math.max(0.0, Math.min(1.0, ratio)); // Clamp between 0 and 1

        int startRed = start.red();
        int startGreen = start.green();
        int startBlue = start.blue();

        int endRed = end.red();
        int endGreen = end.green();
        int endBlue = end.blue();

        int red = (int) (startRed + (endRed - startRed) * ratio);
        int green = (int) (startGreen + (endGreen - startGreen) * ratio);
        int blue = (int) (startBlue + (endBlue - startBlue) * ratio);

        return TextColor.color(red, green, blue);
    }

    /**
     * Gets a rarity color based on rarity level
     *
     * @param rarity Rarity level (0 = common, 4 = legendary)
     * @return Appropriate rarity color
     */
    public static @NotNull TextColor rarityColor(int rarity) {
        return switch (rarity) {
            case 1 -> UNCOMMON;
            case 2 -> RARE;
            case 3 -> EPIC;
            case 4 -> LEGENDARY;
            default -> COMMON;
        };
    }

    /**
     * Gets a TextColor by name from ColorUtil or NamedTextColor
     * Used for color placeholder parsing (%COLOR_NAME%)
     *
     * @param colorName The color name (case-insensitive)
     * @return TextColor if found, null otherwise
     */
    @org.jetbrains.annotations.Nullable
    public static TextColor getColorByName(@NotNull String colorName) {
        String upperName = colorName.toUpperCase().replace(" ", "_");

        // Try ColorUtil colors first
        try {
            java.lang.reflect.Field field = ColorUtil.class.getDeclaredField(upperName);
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) &&
                    field.getType().equals(TextColor.class)) {
                return (TextColor) field.get(null);
            }
        } catch (Exception ignored) {
            // Field not found or not accessible, continue to NamedTextColor
        }

        // Direct NamedTextColor mapping (더 안전한 방법)
        return switch (upperName) {
            // Basic Minecraft colors
            case "BLACK" -> net.kyori.adventure.text.format.NamedTextColor.BLACK;
            case "DARK_BLUE" -> net.kyori.adventure.text.format.NamedTextColor.DARK_BLUE;
            case "DARK_GREEN" -> net.kyori.adventure.text.format.NamedTextColor.DARK_GREEN;
            case "DARK_AQUA" -> net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA;
            case "DARK_RED" -> net.kyori.adventure.text.format.NamedTextColor.DARK_RED;
            case "DARK_PURPLE" -> net.kyori.adventure.text.format.NamedTextColor.DARK_PURPLE;
            case "GOLD" -> net.kyori.adventure.text.format.NamedTextColor.GOLD;
            case "GRAY", "GREY" -> net.kyori.adventure.text.format.NamedTextColor.GRAY;
            case "DARK_GRAY", "DARK_GREY" -> net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY;
            case "BLUE" -> net.kyori.adventure.text.format.NamedTextColor.BLUE;
            case "GREEN" -> net.kyori.adventure.text.format.NamedTextColor.GREEN;
            case "AQUA" -> net.kyori.adventure.text.format.NamedTextColor.AQUA;
            case "RED" -> net.kyori.adventure.text.format.NamedTextColor.RED;
            case "LIGHT_PURPLE", "PURPLE" -> net.kyori.adventure.text.format.NamedTextColor.LIGHT_PURPLE;
            case "YELLOW" -> net.kyori.adventure.text.format.NamedTextColor.YELLOW;
            case "WHITE", "RESET", "DEFAULT" -> net.kyori.adventure.text.format.NamedTextColor.WHITE;

            // Aliases
            case "LIGHT_BLUE" -> net.kyori.adventure.text.format.NamedTextColor.BLUE;
            case "LIGHT_GREEN" -> net.kyori.adventure.text.format.NamedTextColor.GREEN;
            case "LIGHT_RED" -> net.kyori.adventure.text.format.NamedTextColor.RED;

            default -> null;
        };
    }

    /**
     * Gets all available color names for debugging/help
     *
     * @return Set of available color names
     */
    @NotNull
    public static java.util.Set<String> getAvailableColorNames() {
        java.util.Set<String> colors = new java.util.HashSet<>();

        // Add ColorUtil colors
        java.lang.reflect.Field[] fields = ColorUtil.class.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) &&
                    field.getType().equals(TextColor.class)) {
                colors.add(field.getName());
            }
        }

        // Add NamedTextColor values manually (더 안전한 방법)
        colors.add("BLACK");
        colors.add("DARK_BLUE");
        colors.add("DARK_GREEN");
        colors.add("DARK_AQUA");
        colors.add("DARK_RED");
        colors.add("DARK_PURPLE");
        colors.add("GOLD");
        colors.add("GRAY");
        colors.add("DARK_GRAY");
        colors.add("BLUE");
        colors.add("GREEN");
        colors.add("AQUA");
        colors.add("RED");
        colors.add("LIGHT_PURPLE");
        colors.add("YELLOW");
        colors.add("WHITE");

        return colors;
    }

    /**
     * Gets a health color based on health percentage
     *
     * @param healthPercent Health percentage (0.0 - 1.0)
     * @return Color ranging from red (low) to green (high)
     */
    public static @NotNull TextColor healthColor(double healthPercent) {
        healthPercent = Math.max(0.0, Math.min(1.0, healthPercent));

        if (healthPercent > 0.6) {
            // Green to yellow transition
            return gradient(TextColor.color(255, 255, 0), TextColor.color(0, 255, 0), (healthPercent - 0.6) / 0.4);
        } else if (healthPercent > 0.3) {
            // Yellow to orange transition
            return gradient(TextColor.color(255, 165, 0), TextColor.color(255, 255, 0), (healthPercent - 0.3) / 0.3);
        } else {
            // Red to orange transition
            return gradient(TextColor.color(255, 0, 0), TextColor.color(255, 165, 0), healthPercent / 0.3);
        }
    }
}