package com.febrie.rpg.gui.component;

import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Factory class for creating common GUI components with full internationalization support
 * All methods require LangManager and Player for proper language support
 * <p>
 * Updated: Removed all legacy methods, only LangManager-based methods remain
 * Updated: 새로고침 기능 완전 제거
 *
 * @author Febrie, CoffeeTory
 */
public final class GuiFactory {

    private GuiFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Creates a close button item
     */
    public static GuiItem createCloseButton(@NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.clickable(
                new ItemBuilder(Material.BARRIER)
                        .displayName(langManager.getComponent(player, "gui.buttons.close.name"))
                        .addLore(langManager.getComponent(player, "gui.buttons.close.lore"))
                        .build(),
                Player::closeInventory
        );
    }

    /**
     * Creates a back button item with GuiManager integration
     */
    public static GuiItem createBackButton(@NotNull GuiManager guiManager, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.clickable(
                new ItemBuilder(Material.ARROW)
                        .displayName(langManager.getComponent(player, "gui.buttons.back.name"))
                        .addLore(langManager.getComponent(player, "gui.buttons.back.lore"))
                        .build(),
                guiManager::navigateBack
        );
    }

    /**
     * Creates a back button item with custom action
     */
    public static GuiItem createBackButton(@NotNull Consumer<Player> action, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.clickable(
                new ItemBuilder(Material.ARROW)
                        .displayName(langManager.getComponent(player, "gui.buttons.back.name"))
                        .addLore(langManager.getComponent(player, "gui.buttons.back.lore"))
                        .build(),
                action
        );
    }

    /**
     * Creates a decorative item
     */
    public static GuiItem createDecoration() {
        return createDecoration(Material.BLACK_STAINED_GLASS_PANE);
    }

    /**
     * Creates a decorative item with specific material
     */
    public static GuiItem createDecoration(@NotNull Material material) {
        return GuiItem.display(
                new ItemBuilder(material)
                        .displayName(Component.empty())
                        .build()
        );
    }

    /**
     * Creates a placeholder item
     */
    public static GuiItem createPlaceholder(@NotNull String text, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.display(
                new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                        .displayName(langManager.getComponent(player, text))
                        .build()
        );
    }

    /**
     * Creates an error item
     */
    public static GuiItem createError(@NotNull String errorKey, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.display(
                new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                        .displayName(langManager.getComponent(player, errorKey).color(NamedTextColor.RED))
                        .build()
        );
    }

    /**
     * Creates a loading item
     */
    public static GuiItem createLoading(@NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.display(
                new ItemBuilder(Material.CLOCK)
                        .displayName(langManager.getComponent(player, "gui.buttons.loading.name"))
                        .addLore(langManager.getComponent(player, "gui.buttons.loading.lore"))
                        .build()
        );
    }

    /**
     * Creates an info item
     */
    public static GuiItem createInfo(@NotNull String titleKey, @NotNull String descKey, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.display(
                new ItemBuilder(Material.PAPER)
                        .displayName(langManager.getComponent(player, titleKey))
                        .addLore(langManager.getComponent(player, descKey))
                        .build()
        );
    }

    /**
     * Creates a toggle button
     */
    public static GuiItem createToggle(boolean state, @NotNull String nameKey, @NotNull Consumer<Player> action, @NotNull LangManager langManager, @NotNull Player player) {
        Material material = state ? Material.LIME_DYE : Material.GRAY_DYE;
        String statusKey = state ? "gui.buttons.toggle.enabled" : "gui.buttons.toggle.disabled";

        return GuiItem.clickable(
                new ItemBuilder(material)
                        .displayName(langManager.getComponent(player, nameKey))
                        .addLore(langManager.getComponent(player, statusKey))
                        .build(),
                action
        );
    }

    /**
     * Creates a previous page button
     */
    public static GuiItem createPreviousPageButton(@NotNull Consumer<Player> action, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.clickable(
                new ItemBuilder(Material.ARROW)
                        .displayName(langManager.getComponent(player, "gui.buttons.previous-page.name"))
                        .addLore(langManager.getComponent(player, "gui.buttons.previous-page.lore"))
                        .build(),
                action
        );
    }

    /**
     * Creates a next page button
     */
    public static GuiItem createNextPageButton(@NotNull Consumer<Player> action, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.clickable(
                new ItemBuilder(Material.ARROW)
                        .displayName(langManager.getComponent(player, "gui.buttons.next-page.name"))
                        .addLore(langManager.getComponent(player, "gui.buttons.next-page.lore"))
                        .build(),
                action
        );
    }

    /**
     * Creates a page info display item
     */
    public static GuiItem createPageInfo(int currentPage, int totalPages, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.display(
                new ItemBuilder(Material.PAPER)
                        .displayName(langManager.getComponent(player, "gui.buttons.page-info.name"))
                        .addLore(langManager.getComponent(player, "gui.buttons.page-info.lore",
                                "current", String.valueOf(currentPage),
                                "total", String.valueOf(totalPages)))
                        .build()
        );
    }

    /**
     * Creates a status indicator item
     */
    public static GuiItem createStatusIndicator(@NotNull String status, boolean isOnline, @NotNull LangManager langManager, @NotNull Player player) {
        Material material = isOnline ? Material.LIME_DYE : Material.RED_DYE;
        Component statusComponent = langManager.getComponent(player, isOnline ? "gui.buttons.status.online" : "gui.buttons.status.offline");

        return GuiItem.display(
                new ItemBuilder(material)
                        .displayName(Component.text(status, isOnline ? ColorUtil.SUCCESS : ColorUtil.ERROR))
                        .addLore(langManager.getComponent(player, "gui.buttons.status.status-text", "status", statusComponent.toString()))
                        .build()
        );
    }

    /**
     * Creates a confirmation button
     */
    public static GuiItem createConfirmButton(@NotNull Consumer<Player> action, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.clickable(
                new ItemBuilder(Material.LIME_WOOL)
                        .displayName(langManager.getComponent(player, "gui.buttons.confirm.name"))
                        .addLore(langManager.getComponent(player, "gui.buttons.confirm.lore"))
                        .build(),
                action
        );
    }

    /**
     * Creates a cancel button
     */
    public static GuiItem createCancelButton(@NotNull Consumer<Player> action, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.clickable(
                new ItemBuilder(Material.RED_WOOL)
                        .displayName(langManager.getComponent(player, "gui.buttons.cancel.name"))
                        .addLore(langManager.getComponent(player, "gui.buttons.cancel.lore"))
                        .build(),
                action
        );
    }
}