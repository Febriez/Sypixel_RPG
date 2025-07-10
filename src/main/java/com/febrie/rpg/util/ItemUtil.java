package com.febrie.rpg.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for ItemStack operations using Adventure API
 *
 * @author Febrie, CoffeeTory
 */
public final class ItemUtil {

    private ItemUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Checks if an ItemStack is null or air
     *
     * @param item The item to check
     * @return true if the item is null or air
     */
    public static boolean isNullOrAir(ItemStack item) {
        return item == null || item.getType() == Material.AIR || item.getAmount() <= 0;
    }

    /**
     * Gets the display name of an item
     *
     * @param item The item
     * @return The display name component, or translated item name if no custom name
     */
    public static Component getDisplayName(ItemStack item) {
        if (isNullOrAir(item)) {
            return Component.empty();
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return meta.displayName();
        }

        return TranslateUtil.translateItem(item.getType());
    }

    /**
     * Gets the display name as plain text
     *
     * @param item The item
     * @return Plain text display name
     */
    public static String getDisplayNamePlain(ItemStack item) {
        return PlainTextComponentSerializer.plainText().serialize(getDisplayName(item));
    }

    /**
     * Sets the display name with color
     *
     * @param item  The item to modify
     * @param name  The name to set
     * @param color The color to apply
     */
    public static void setDisplayName(ItemStack item, String name, TextColor color) {
        if (isNullOrAir(item)) return;

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(name, color)
                    .decoration(TextDecoration.ITALIC, false));
            item.setItemMeta(meta);
        }
    }

    /**
     * Adds lore lines to an item
     *
     * @param item  The item
     * @param lines The lines to add
     */
    public static void addLore(ItemStack item, Component... lines) {
        if (isNullOrAir(item)) return;

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<Component> lore = meta.lore();
            if (lore == null) lore = new ArrayList<>();

            lore.addAll(Arrays.asList(lines));
            meta.lore(lore);
            item.setItemMeta(meta);
        }
    }

    /**
     * Adds lore lines with specific color
     *
     * @param item  The item
     * @param color The color to apply
     * @param lines The text lines to add
     */
    public static void addLore(ItemStack item, TextColor color, String... lines) {
        Component[] components = Arrays.stream(lines)
                .map(line -> Component.text(line, color)
                        .decoration(TextDecoration.ITALIC, false))
                .toArray(Component[]::new);
        addLore(item, components);
    }

    /**
     * Clears all lore from an item
     *
     * @param item The item
     */
    public static void clearLore(ItemStack item) {
        if (isNullOrAir(item)) return;

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.lore(null);
            item.setItemMeta(meta);
        }
    }

    /**
     * Gets the lore of an item
     *
     * @param item The item
     * @return List of lore components, or empty list if none
     */
    public static List<Component> getLore(ItemStack item) {
        if (isNullOrAir(item)) return Collections.emptyList();

        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasLore()) {
            return meta.lore();
        }

        return Collections.emptyList();
    }

    /**
     * Checks if an item has a specific lore line (plain text comparison)
     *
     * @param item The item
     * @param text The text to search for
     * @return true if the lore contains the text
     */
    public static boolean hasLoreLine(ItemStack item, String text) {
        return getLore(item).stream()
                .map(component -> PlainTextComponentSerializer.plainText().serialize(component))
                .anyMatch(line -> line.contains(text));
    }

    /**
     * Gets the damage value of an item
     *
     * @param item The item
     * @return The damage value, or 0 if not damageable
     */
    public static int getDamage(ItemStack item) {
        if (isNullOrAir(item)) return 0;

        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable damageable) {
            return damageable.getDamage();
        }

        return 0;
    }

    /**
     * Sets the damage value of an item
     *
     * @param item   The item
     * @param damage The damage value to set
     */
    public static void setDamage(ItemStack item, int damage) {
        if (isNullOrAir(item)) return;

        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable damageable) {
            damageable.setDamage(damage);
            item.setItemMeta(meta);
        }
    }

    /**
     * Gets the durability percentage of an item
     *
     * @param item The item
     * @return Durability percentage (0-100), or -1 if not damageable
     */
    public static double getDurabilityPercentage(ItemStack item) {
        if (isNullOrAir(item)) return -1;

        Material material = item.getType();
        short maxDurability = material.getMaxDurability();

        if (maxDurability == 0) return -1;

        int damage = getDamage(item);
        int remaining = maxDurability - damage;

        return (remaining / (double) maxDurability) * 100;
    }

    /**
     * Creates a durability bar component
     *
     * @param item      The item
     * @param barLength The length of the bar
     * @return Component showing durability bar
     */
    public static Component createDurabilityBar(ItemStack item, int barLength) {
        double percentage = getDurabilityPercentage(item);

        if (percentage < 0) {
            return Component.text("N/A", NamedTextColor.GRAY);
        }

        int filled = (int) Math.round((percentage / 100.0) * barLength);
        int empty = barLength - filled;

        TextColor color;
        if (percentage > 60) {
            color = NamedTextColor.GREEN;
        } else if (percentage > 30) {
            color = NamedTextColor.YELLOW;
        } else {
            color = NamedTextColor.RED;
        }

        return Component.text("▮".repeat(Math.max(0, filled)), color)
                .append(Component.text("▯".repeat(Math.max(0, empty)), NamedTextColor.GRAY));
    }

    /**
     * Checks if an item is enchanted
     *
     * @param item The item
     * @return true if the item has enchantments
     */
    public static boolean isEnchanted(ItemStack item) {
        if (isNullOrAir(item)) return false;

        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasEnchants();
    }

    /**
     * Gets all enchantments on an item as formatted components
     *
     * @param item The item
     * @return List of enchantment components
     */
    public static List<Component> getEnchantmentComponents(ItemStack item) {
        if (isNullOrAir(item)) return Collections.emptyList();

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasEnchants()) return Collections.emptyList();

        return meta.getEnchants().entrySet().stream()
                .map(entry -> {
                    Component enchantName = TranslateUtil.translateEnchantment(entry.getKey());
                    Component level = Component.text(" " + entry.getValue());
                    return enchantName.append(level).color(NamedTextColor.GRAY);
                })
                .collect(Collectors.toList());
    }

    /**
     * Checks if an item has a specific flag
     *
     * @param item The item
     * @param flag The flag to check
     * @return true if the item has the flag
     */
    public static boolean hasItemFlag(ItemStack item, ItemFlag flag) {
        if (isNullOrAir(item)) return false;

        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasItemFlag(flag);
    }

    /**
     * Makes an item glow without showing enchantments
     *
     * @param item The item
     */
    public static void addGlow(ItemStack item) {
        if (isNullOrAir(item)) return;

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setEnchantmentGlintOverride(true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }
    }

    /**
     * Removes glow effect from an item
     *
     * @param item The item
     */
    public static void removeGlow(ItemStack item) {
        if (isNullOrAir(item)) return;

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setEnchantmentGlintOverride(false);
            item.setItemMeta(meta);
        }
    }

    /**
     * Creates a formatted item tooltip component
     *
     * @param item The item
     * @return Formatted tooltip component
     */
    public static Component createTooltip(ItemStack item) {
        if (isNullOrAir(item)) {
            return Component.text("Empty", NamedTextColor.GRAY);
        }

        Component tooltip = getDisplayName(item);

        if (item.getAmount() > 1) {
            tooltip = tooltip.append(Component.text(" x" + item.getAmount(), NamedTextColor.YELLOW));
        }

        List<Component> lore = getLore(item);
        if (!lore.isEmpty()) {
            for (Component line : lore) {
                tooltip = tooltip.append(Component.newline()).append(line);
            }
        }

        return tooltip;
    }

    /**
     * Checks if two items are similar (ignoring amount)
     *
     * @param item1 First item
     * @param item2 Second item
     * @return true if items are similar
     */
    public static boolean isSimilar(ItemStack item1, ItemStack item2) {
        if (item1 == null && item2 == null) return true;
        if (item1 == null || item2 == null) return false;

        return item1.isSimilar(item2);
    }

    /**
     * Gets persistent data from an item
     *
     * @param item The item
     * @param key  The namespaced key
     * @param type The data type
     * @param <T>  The primitive type
     * @param <Z>  The complex type
     * @return The data value, or null if not found
     */
    public static <T, Z> @Nullable Z getPersistentData(ItemStack item, NamespacedKey key,
                                                       PersistentDataType<T, Z> type) {
        if (isNullOrAir(item)) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.get(key, type);
    }

    /**
     * Sets persistent data on an item
     *
     * @param item  The item
     * @param key   The namespaced key
     * @param type  The data type
     * @param value The value to set
     * @param <T>   The primitive type
     * @param <Z>   The complex type
     */
    public static <T, Z> void setPersistentData(ItemStack item, NamespacedKey key,
                                                PersistentDataType<T, Z> type, Z value) {
        if (isNullOrAir(item)) return;

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(key, type, value);
            item.setItemMeta(meta);
        }
    }

    /**
     * Creates a separator lore component
     *
     * @param length The length of the separator
     * @param color  The color to use
     * @return Separator component
     */
    public static @NotNull Component createSeparator(int length, TextColor color) {
        return Component.text("─".repeat(length), color)
                .decoration(TextDecoration.ITALIC, false);
    }

    /**
     * Adds stats lore to an item
     *
     * @param item  The item
     * @param stats Map of stat names to values
     */
    public static void addStatsLore(ItemStack item, @NotNull Map<String, Object> stats) {
        List<Component> loreLines = new ArrayList<>();

        loreLines.add(createSeparator(30, NamedTextColor.DARK_GRAY));
        loreLines.add(Component.text("Stats:", NamedTextColor.GOLD)
                .decoration(TextDecoration.BOLD, true));

        stats.forEach((stat, value) -> {
            Component statLine = Component.text("  " + stat + ": ", NamedTextColor.GRAY)
                    .append(Component.text(value.toString(), NamedTextColor.WHITE))
                    .decoration(TextDecoration.ITALIC, false);
            loreLines.add(statLine);
        });

        loreLines.add(createSeparator(30, NamedTextColor.DARK_GRAY));

        addLore(item, loreLines.toArray(new Component[0]));
    }

    /**
     * Checks if an item is a weapon
     *
     * @param item The item to check
     * @return true if the item is a weapon
     */
    public static boolean isWeapon(ItemStack item) {
        if (isNullOrAir(item)) return false;

        Material type = item.getType();
        return type.name().endsWith("_SWORD") ||
                type.name().endsWith("_AXE") ||
                type.name().endsWith("_TRIDENT") ||
                type.name().endsWith("_BOW") ||
                type.name().endsWith("_CROSSBOW") ||
                type == Material.MACE;
    }

    /**
     * Checks if an item is armor
     *
     * @param item The item to check
     * @return true if the item is armor
     */
    public static boolean isArmor(ItemStack item) {
        if (isNullOrAir(item)) return false;

        Material type = item.getType();
        return type.name().endsWith("_HELMET") ||
                type.name().endsWith("_CHESTPLATE") ||
                type.name().endsWith("_LEGGINGS") ||
                type.name().endsWith("_BOOTS") ||
                type == Material.ELYTRA ||
                type == Material.SHIELD;
    }

    /**
     * Checks if an item is a tool
     *
     * @param item The item to check
     * @return true if the item is a tool
     */
    public static boolean isTool(ItemStack item) {
        if (isNullOrAir(item)) return false;

        Material type = item.getType();
        return type.name().endsWith("_PICKAXE") ||
                type.name().endsWith("_SHOVEL") ||
                type.name().endsWith("_HOE") ||
                type == Material.FISHING_ROD ||
                type == Material.SHEARS ||
                type == Material.FLINT_AND_STEEL;
    }
}