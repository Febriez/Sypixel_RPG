package com.febrie.rpg.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ArrayList;

/**
 * Legacy wrapper for ItemBuilder to support old method names
 * Used by island GUIs that were written with the old API
 *
 * @author Febrie, CoffeeTory
 */
public class LegacyItemBuilder extends ItemBuilder {
    
    public LegacyItemBuilder(Material material) {
        super(material);
    }
    
    public LegacyItemBuilder(Material material, int amount) {
        super(material, amount);
    }
    
    public LegacyItemBuilder(ItemStack itemStack) {
        super(itemStack);
    }
    
    public LegacyItemBuilder(Player targetPlayer) {
        super(targetPlayer);
    }
    
    /**
     * Legacy method - wraps displayName
     */
    public LegacyItemBuilder setDisplayName(@NotNull String name) {
        // Convert legacy color codes and set as display name
        Component component = ColorUtil.parseComponent(name);
        super.displayName(component);
        return this;
    }
    
    /**
     * Legacy method - wraps lore methods
     */
    public LegacyItemBuilder setLore(@NotNull List<String> lore) {
        List<Component> componentLore = new ArrayList<>();
        for (String line : lore) {
            componentLore.add(ColorUtil.parseComponent(line));
        }
        super.lore(componentLore);
        return this;
    }
    
    /**
     * Legacy method - wraps addLore
     */
    public LegacyItemBuilder addLore(@NotNull String... lines) {
        for (String line : lines) {
            super.addLore(ColorUtil.parseComponent(line));
        }
        return this;
    }
    
    /**
     * Legacy method - wraps addLore for single line
     */
    public LegacyItemBuilder addLore(@NotNull String line) {
        super.addLore(ColorUtil.parseComponent(line));
        return this;
    }
    
    /**
     * Legacy method - wraps amount
     */
    public LegacyItemBuilder setAmount(int amount) {
        super.amount(amount);
        return this;
    }
    
    /**
     * Legacy method - wraps glint
     */
    public LegacyItemBuilder setGlowing(boolean glowing) {
        super.glint(glowing);
        return this;
    }
    
    // Override parent methods to return LegacyItemBuilder for chaining
    
    @Override
    public LegacyItemBuilder displayName(Component displayName) {
        super.displayName(displayName);
        return this;
    }
    
    @Override
    public LegacyItemBuilder lore(List<Component> lore) {
        super.lore(lore);
        return this;
    }
    
    @Override
    public LegacyItemBuilder amount(int amount) {
        super.amount(amount);
        return this;
    }
    
    public LegacyItemBuilder glow() {
        super.glint(true);
        return this;
    }
    
    @Override
    public LegacyItemBuilder enchant(org.bukkit.enchantments.Enchantment enchantment, int level) {
        super.enchant(enchantment, level);
        return this;
    }
    
    public LegacyItemBuilder hideFlags() {
        super.hideAllFlags();
        return this;
    }
    
    public LegacyItemBuilder unbreakable() {
        super.unbreakable(true);
        return this;
    }
}