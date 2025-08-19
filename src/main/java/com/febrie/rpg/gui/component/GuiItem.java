package com.febrie.rpg.gui.component;

import com.febrie.rpg.util.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.kyori.adventure.text.Component;
/**
 * Represents an interactive item in a GUI
 * Supports different actions for different click types
 *
 * @author Febrie, CoffeeTory
 */
public class GuiItem {

    private final ItemStack itemStack;
    private final Map<ClickType, BiConsumer<Player, ClickType>> clickActions;
    private BiConsumer<Player, ClickType> defaultAction;
    private boolean enabled;

    public GuiItem(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack.clone();
        this.clickActions = new EnumMap<>(ClickType.class);
        this.enabled = true;
    }

    public GuiItem(@NotNull ItemBuilder builder) {
        this(builder.build());
    }

    /**
     * Sets an action for a specific click type
     */
    public GuiItem onClick(@NotNull ClickType clickType, @NotNull BiConsumer<Player, ClickType> action) {
        clickActions.put(clickType, action);
        return this;
    }

    /**
     * Sets a default action for unhandled click types
     */
    public GuiItem onAnyClick(@NotNull BiConsumer<Player, ClickType> action) {
        this.defaultAction = action;
        return this;
    }

    /**
     * Sets a simple action that ignores click type
     */
    public GuiItem onAnyClick(@NotNull Consumer<Player> action) {
        this.defaultAction = (player, clickType) -> action.accept(player);
        return this;
    }

    /**
     * Sets action for left click only
     */
    public GuiItem onLeftClick(@NotNull Consumer<Player> action) {
        return onClick(ClickType.LEFT, (player, click) -> action.accept(player));
    }

    /**
     * Sets action for right click only
     */
    public GuiItem onRightClick(@NotNull Consumer<Player> action) {
        return onClick(ClickType.RIGHT, (player, click) -> action.accept(player));
    }

    /**
     * Executes the appropriate action for the given click type
     */
    public void executeAction(@NotNull Player player, @NotNull ClickType clickType) {
        if (!enabled) return;

        BiConsumer<Player, ClickType> action = clickActions.getOrDefault(clickType, defaultAction);
        if (action != null) {
            action.accept(player, clickType);
        }
    }

    /**
     * Checks if this item has any actions defined
     */
    public boolean hasActions() {
        return !clickActions.isEmpty() || defaultAction != null;
    }

    /**
     * Gets the ItemStack for display
     */
    @NotNull
    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    /**
     * Sets whether this item is enabled (can be clicked)
     */
    public GuiItem setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Checks if this item is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    // Static factory methods
    public static GuiItem of(@NotNull ItemStack itemStack) {
        return new GuiItem(itemStack);
    }

    public static GuiItem of(@NotNull ItemBuilder builder) {
        return new GuiItem(builder);
    }

    public static GuiItem clickable(@NotNull ItemStack itemStack, @NotNull Consumer<Player> action) {
        return new GuiItem(itemStack).onAnyClick(action);
    }

    public static GuiItem clickable(@NotNull ItemBuilder builder, @NotNull Consumer<Player> action) {
        return new GuiItem(builder).onAnyClick(action);
    }

    public static GuiItem display(@NotNull ItemStack itemStack) {
        return new GuiItem(itemStack);
    }

    public static GuiItem display(@NotNull ItemBuilder builder) {
        return new GuiItem(builder);
    }
    
    /**
     * Creates an empty GUI item (air)
     */
    public static GuiItem empty() {
        return new GuiItem(new ItemStack(org.bukkit.Material.AIR));
    }
    
    /**
     * Gets the ItemStack
     */
    @NotNull
    public ItemStack getItem() {
        return itemStack;
    }
}