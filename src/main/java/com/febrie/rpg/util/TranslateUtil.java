package com.febrie.rpg.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for translation and localization operations using Adventure API
 *
 * @author Febrie, CoffeeTory
 */
public final class TranslateUtil {

    private TranslateUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Creates a translatable component for an item
     *
     * @param material The material to translate
     * @return TranslatableComponent for the item
     */
    public static @NotNull TranslatableComponent translateItem(@NotNull Material material) {
        String key = material.translationKey();
        return Component.translatable(key);
    }

    /**
     * Creates a translatable component for an item with style
     *
     * @param material The material to translate
     * @param style    The style to apply
     * @return Styled TranslatableComponent
     */
    public static TranslatableComponent translateItem(Material material, Style style) {
        return translateItem(material).style(style);
    }

    /**
     * Creates a translatable component for an item with color
     *
     * @param material The material to translate
     * @param color    The color to apply
     * @return Colored TranslatableComponent
     */
    public static @NotNull TranslatableComponent translateItem(Material material, TextColor color) {
        return translateItem(material).color(color);
    }

    /**
     * Creates a translatable component for an entity
     *
     * @param entityType The entity type to translate
     * @return TranslatableComponent for the entity
     */
    public static @NotNull TranslatableComponent translateEntity(@NotNull EntityType entityType) {
        return Component.translatable(entityType);
    }

    /**
     * Creates a translatable component for an enchantment
     *
     * @param enchantment The enchantment to translate
     * @return TranslatableComponent for the enchantment
     */
    public static @NotNull TranslatableComponent translateEnchantment(@NotNull Enchantment enchantment) {
        return Component.translatable(enchantment);
    }

    /**
     * Creates a translatable component for a potion effect
     *
     * @param effectType The potion effect type to translate
     * @return TranslatableComponent for the effect
     */
    public static @NotNull TranslatableComponent translatePotionEffect(@NotNull PotionEffectType effectType) {
        String key = effectType.translationKey();
        return Component.translatable(key);
    }

    /**
     * Creates a custom translatable component with arguments
     *
     * @param key  The translation key
     * @param args The arguments for the translation
     * @return TranslatableComponent with arguments
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull TranslatableComponent translate(String key, Component... args) {
        return Component.translatable(key, args);
    }

    /**
     * Creates a custom translatable component with style
     *
     * @param key   The translation key
     * @param style The style to apply
     * @param args  The arguments for the translation
     * @return Styled TranslatableComponent
     */
    public static @NotNull TranslatableComponent translate(String key, Style style, Component... args) {
        return Component.translatable(key, args).style(style);
    }

    /**
     * Creates a death message component
     *
     * @param victim The victim's name component
     * @param killer The killer's name component (optional)
     * @param weapon The weapon component (optional)
     * @return Death message component
     */
    public static @NotNull Component createDeathMessage(Component victim, Component killer, Component weapon) {
        if (killer == null) {
            return Component.translatable("death.attack.generic", victim);
        } else if (weapon == null) {
            return Component.translatable("death.attack.player", victim, killer);
        } else {
            return Component.translatable("death.attack.player.item", victim, killer, weapon);
        }
    }

    /**
     * Creates an advancement message component
     *
     * @param player      The player's name component
     * @param advancement The advancement name component
     * @return Advancement message component
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull Component createAdvancementMessage(Component player, Component advancement) {
        return Component.translatable("chat.type.advancement.task", player, advancement);
    }

    /**
     * Creates a command feedback message
     *
     * @param key  The translation key for the feedback
     * @param args Arguments for the message
     * @return Feedback component
     */
    public static @NotNull Component commandFeedback(String key, Component... args) {
        return Component.translatable("commands." + key, args).color(NamedTextColor.GRAY);
    }

    /**
     * Creates an error message component
     *
     * @param key  The translation key for the error
     * @param args Arguments for the message
     * @return Error component in red
     */
    public static @NotNull Component errorMessage(String key, Component... args) {
        return Component.translatable(key, args).color(NamedTextColor.RED);
    }

    /**
     * Creates a success message component
     *
     * @param key  The translation key
     * @param args Arguments for the message
     * @return Success component in green
     */
    public static @NotNull Component successMessage(String key, Component... args) {
        return Component.translatable(key, args).color(NamedTextColor.GREEN);
    }

    /**
     * Creates a GUI title component with translation
     *
     * @param key The translation key
     * @return GUI title component
     */
    public static @NotNull Component guiTitle(String key) {
        return Component.translatable(key).decoration(TextDecoration.ITALIC, false);
    }

    /**
     * Creates a formatted number component (e.g., for stats)
     *
     * @param key   The translation key
     * @param value The numeric value
     * @return Formatted component
     */
    @Contract("_, _ -> new")
    public static @NotNull Component formatNumber(String key, int value) {
        return Component.translatable(key, Component.text(value));
    }

    /**
     * Creates a percentage component
     *
     * @param key        The translation key
     * @param percentage The percentage value (0-100)
     * @return Formatted percentage component
     */
    @Contract("_, _ -> new")
    public static @NotNull Component formatPercentage(String key, double percentage) {
        return Component.translatable(key, Component.text(String.format("%.1f%%", percentage)));
    }

    /**
     * Creates a time duration component
     *
     * @param key     The translation key
     * @param seconds The duration in seconds
     * @return Formatted duration component
     */
    public static @NotNull Component formatDuration(String key, long seconds) {
        Component timeComponent = Component.text(TimeUtil.formatTime(seconds * 1000));
        return Component.translatable(key, timeComponent);
    }

    /**
     * Creates a plural-aware translatable component
     *
     * @param singularKey The key for singular form
     * @param pluralKey   The key for plural form
     * @param count       The count to check
     * @param args        Additional arguments
     * @return Appropriate translatable component
     */
    public static @NotNull TranslatableComponent plural(String singularKey, String pluralKey, int count, Component @NotNull ... args) {
        String key = count == 1 ? singularKey : pluralKey;
        Component[] allArgs = new Component[args.length + 1];
        allArgs[0] = Component.text(count);
        System.arraycopy(args, 0, allArgs, 1, args.length);
        return Component.translatable(key, allArgs);
    }

    /**
     * Creates a keybind component
     *
     * @param key The keybind key (e.g., "key.jump")
     * @return Keybind component
     */
    public static @NotNull Component keybind(String key) {
        return Component.keybind(key).color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true);
    }

    /**
     * Creates an instruction message with a keybind
     *
     * @param actionKey  The translation key for the action
     * @param keybindKey The keybind key
     * @return Instruction component
     */
    @Contract("_, _ -> new")
    public static @NotNull Component keybindInstruction(String actionKey, String keybindKey) {
        return Component.translatable(actionKey, Component.keybind(keybindKey).color(NamedTextColor.YELLOW)
                .decoration(TextDecoration.BOLD, true));
    }

    /**
     * Creates a scoreboard objective display name
     *
     * @param key  The translation key
     * @param args Arguments for the translation
     * @return Formatted objective name
     */
    public static @NotNull Component scoreboardObjective(String key, Component... args) {
        return Component.translatable(key, args).decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true);
    }

    /**
     * Checks if a translation key exists in vanilla Minecraft
     *
     * @param key The translation key to check
     * @return true if the key is a valid vanilla translation
     */
    public static boolean isVanillaKey(@NotNull String key) {
        // Common vanilla translation key prefixes
        return key.startsWith("block.") || key.startsWith("item.") || key.startsWith("entity.") || key.startsWith("effect.") || key.startsWith("enchantment.") || key.startsWith("death.") || key.startsWith("commands.") || key.startsWith("chat.") || key.startsWith("gui.") || key.startsWith("options.") || key.startsWith("key.") || key.startsWith("selectWorld.") || key.startsWith("multiplayer.") || key.startsWith("narrator.");
    }

    /**
     * Creates a translatable component with fallback text
     *
     * @param key      The translation key
     * @param fallback The fallback text if translation is not found
     * @return TranslatableComponent with fallback
     */
    @Contract("_, _ -> new")
    public static @NotNull TranslatableComponent withFallback(String key, String fallback) {
        return Component.translatable().key(key).fallback(fallback).build();
    }
}