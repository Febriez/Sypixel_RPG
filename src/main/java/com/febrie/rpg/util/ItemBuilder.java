package com.febrie.rpg.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fluent interface builder for creating ItemStacks with Adventure API support
 * Enhanced with full color placeholder support for lore
 *
 * @author Febrie, CoffeeTory
 */
public class ItemBuilder {
    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    /**
     * Creates a new ItemBuilder with the specified material
     *
     * @param material The material for the item
     */
    public ItemBuilder(Material material) {
        this(material, 1);
    }

    /**
     * Creates a new ItemBuilder with the specified material and amount
     *
     * @param material The material for the item
     * @param amount   The stack size
     */
    public ItemBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
        this.itemMeta = itemStack.getItemMeta();
    }

    /**
     * Creates a new ItemBuilder from an existing ItemStack
     *
     * @param itemStack The ItemStack to copy
     */
    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemBuilder(Player targetPlayer) {
        this.itemStack = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwningPlayer(targetPlayer);
        this.itemMeta = skullMeta;
    }

    /**
     * Sets the display name using Adventure Component
     *
     * @param displayName The display name component
     * @return This builder
     */
    public ItemBuilder displayName(Component displayName) {
        itemMeta.displayName(displayName.decoration(TextDecoration.ITALIC, false));
        return this;
    }

    /**
     * Sets the display name with color
     *
     * @param text  The text to display
     * @param color The color to apply
     * @return This builder
     */
    public ItemBuilder displayName(String text, TextColor color) {
        return displayName(Component.text(text).color(color).decoration(TextDecoration.ITALIC, false));
    }

    /**
     * Sets the display name with color and decorations
     *
     * @param text        The text to display
     * @param color       The color to apply
     * @param decorations The decorations to apply
     * @return This builder
     */
    public ItemBuilder displayName(String text, TextColor color, TextDecoration @NotNull ... decorations) {
        Component component = Component.text(text).color(color).decoration(TextDecoration.ITALIC, false);
        for (TextDecoration decoration : decorations) {
            component = component.decorate(decoration);
        }
        return displayName(component);
    }

    /**
     * Sets the item name (different from display name, used for data packs)
     *
     * @param itemName The item name component
     * @return This builder
     */
    public ItemBuilder itemName(Component itemName) {
        itemMeta.itemName(itemName.decoration(TextDecoration.ITALIC, false));
        return this;
    }

    /**
     * Sets the lore using Adventure Components (List)
     *
     * @param lore The lore components
     * @return This builder
     */
    public ItemBuilder lore(List<Component> lore) {
        List<Component> nonItalicLore = lore.stream()
                .map(component -> component.decoration(TextDecoration.ITALIC, false))
                .toList();
        itemMeta.lore(nonItalicLore);
        return this;
    }

    /**
     * Sets the lore using varargs
     *
     * @param lines The lore lines as components
     * @return This builder
     */
    public ItemBuilder lore(Component... lines) {
        return lore(Arrays.asList(lines));
    }

    /**
     * Adds a single line to existing lore
     *
     * @param line The line to add
     * @return This builder
     */
    public ItemBuilder addLore(Component line) {
        List<Component> lore = itemMeta.lore();
        if (lore == null) lore = new ArrayList<>();
        lore.add(line.decoration(TextDecoration.ITALIC, false));
        itemMeta.lore(lore);
        return this;
    }

    /**
     * Adds a simple text line to lore with color
     *
     * @param text  The text to add
     * @param color The color to apply
     * @return This builder
     */
    public ItemBuilder addLore(String text, TextColor color) {
        return addLore(Component.text(text).color(color).decoration(TextDecoration.ITALIC, false));
    }

    /**
     * Adds multiple lore lines at once
     *
     * @param lines The lore lines to add
     * @return This builder
     */
    public ItemBuilder addLore(Component... lines) {
        List<Component> lore = itemMeta.lore();
        if (lore == null) lore = new ArrayList<>();
        lore.addAll(Arrays.asList(lines));
        itemMeta.lore(lore);
        return this;
    }

    /**
     * Adds multiple lore lines with the same color
     *
     * @param color The color to apply to all lines
     * @param lines The text lines to add
     * @return This builder
     */
    public ItemBuilder addLore(TextColor color, String... lines) {
        for (String line : lines) {
            addLore(Component.text(line).color(color));
        }
        return this;
    }

    /**
     * Adds an empty line to lore
     *
     * @return This builder
     */
    public ItemBuilder addEmptyLore() {
        return addLore(Component.empty());
    }

    /**
     * Clears the lore
     *
     * @return This builder
     */
    public ItemBuilder clearLore() {
        itemMeta.lore(null);
        return this;
    }

    /**
     * Adds an enchantment
     *
     * @param enchantment The enchantment to add
     * @param level       The enchantment level
     * @return This builder
     */
    public ItemBuilder enchant(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    /**
     * Removes an enchantment
     *
     * @param enchantment The enchantment to remove
     * @return This builder
     */
    public ItemBuilder removeEnchant(Enchantment enchantment) {
        itemMeta.removeEnchant(enchantment);
        return this;
    }

    /**
     * Clears all enchantments
     *
     * @return This builder
     */
    public ItemBuilder clearEnchants() {
        itemMeta.removeEnchantments();
        return this;
    }

    /**
     * Sets the enchantment glint override
     *
     * @param glint Whether to show enchantment glint
     * @return This builder
     */
    public ItemBuilder glint(boolean glint) {
        itemMeta.setEnchantmentGlintOverride(glint);
        return this;
    }

    /**
     * Adds item flags
     *
     * @param flags The flags to add
     * @return This builder
     */
    public ItemBuilder flags(ItemFlag... flags) {
        itemMeta.addItemFlags(flags);
        return this;
    }

    /**
     * Removes item flags
     *
     * @param flags The flags to remove
     * @return This builder
     */
    public ItemBuilder removeFlags(ItemFlag... flags) {
        itemMeta.removeItemFlags(flags);
        return this;
    }

    /**
     * Sets the item as unbreakable
     *
     * @param unbreakable Whether the item is unbreakable
     * @return This builder
     */
    public ItemBuilder unbreakable(boolean unbreakable) {
        itemMeta.setUnbreakable(unbreakable);
        return this;
    }

    /**
     * Sets custom model data using the new component system
     *
     * @param data The custom model data value (will be added as a float)
     * @return This builder
     */
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder customModelData(float data) {
        var component = itemMeta.getCustomModelDataComponent();
        component.getFloats().add(data);
        itemMeta.setCustomModelDataComponent(component);
        return this;
    }

    /**
     * Sets custom model data string
     *
     * @param data The custom model data string
     * @return This builder
     */
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder customModelDataString(String data) {
        var component = itemMeta.getCustomModelDataComponent();
        component.getStrings().add(data);
        itemMeta.setCustomModelDataComponent(component);
        return this;
    }

    /**
     * Sets the max stack size
     *
     * @param size The maximum stack size
     * @return This builder
     */
    public ItemBuilder maxStackSize(int size) {
        itemMeta.setMaxStackSize(size);
        return this;
    }

    /**
     * Hides the tooltip completely
     *
     * @param hide Whether to hide tooltip
     * @return This builder
     */
    public ItemBuilder hideTooltip(boolean hide) {
        itemMeta.setHideTooltip(hide);
        return this;
    }

    /**
     * Adds an attribute modifier
     *
     * @param attribute The attribute
     * @param modifier  The modifier
     * @return This builder
     */
    public ItemBuilder attribute(Attribute attribute, AttributeModifier modifier) {
        itemMeta.addAttributeModifier(attribute, modifier);
        return this;
    }

    /**
     * Removes all attribute modifiers for a specific attribute
     *
     * @param attribute The attribute to clear
     * @return This builder
     */
    public ItemBuilder removeAttribute(Attribute attribute) {
        itemMeta.removeAttributeModifier(attribute);
        return this;
    }

    /**
     * Sets persistent data using Paper's PDC API
     *
     * @param key   The namespaced key
     * @param type  The data type
     * @param value The value to store
     * @param <T>   The type parameter
     * @param <Z>   The complex type parameter
     * @return This builder
     */
    public <T, Z> ItemBuilder persistentData(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        itemMeta.getPersistentDataContainer().set(key, type, value);
        return this;
    }

    /**
     * Removes persistent data
     *
     * @param key The namespaced key to remove
     * @return This builder
     */
    public ItemBuilder removePersistentData(NamespacedKey key) {
        itemMeta.getPersistentDataContainer().remove(key);
        return this;
    }

    /**
     * Modifies the ItemMeta directly
     *
     * @param consumer The consumer to apply to ItemMeta
     * @return This builder
     */
    public ItemBuilder meta(Consumer<ItemMeta> consumer) {
        consumer.accept(itemMeta);
        return this;
    }

    /**
     * Modifies the ItemStack directly
     *
     * @param consumer The consumer to apply to ItemStack
     * @return This builder
     */
    public ItemBuilder stack(Consumer<ItemStack> consumer) {
        consumer.accept(itemStack);
        return this;
    }

    /**
     * Sets the amount
     *
     * @param amount The stack size
     * @return This builder
     */
    public ItemBuilder amount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    /**
     * Builds and returns the ItemStack
     *
     * @return The built ItemStack
     */
    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Static factory method for creating a builder
     *
     * @param material The material
     * @return A new ItemBuilder
     */
    public static ItemBuilder of(Material material) {
        return new ItemBuilder(material);
    }

    /**
     * Static factory method for creating a builder with amount
     *
     * @param material The material
     * @param amount   The amount
     * @return A new ItemBuilder
     */
    public static ItemBuilder of(Material material, int amount) {
        return new ItemBuilder(material, amount);
    }

    /**
     * Static factory method for cloning an existing item
     *
     * @param itemStack The item to clone
     * @return A new ItemBuilder
     */
    public static ItemBuilder from(ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }
}