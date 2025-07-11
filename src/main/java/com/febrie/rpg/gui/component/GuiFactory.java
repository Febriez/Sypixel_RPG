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
                ItemBuilder.of(Material.BARRIER)
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
                ItemBuilder.of(Material.ARROW)
                        .displayName(langManager.getComponent(player, "gui.buttons.back.name"))
                        .addLore(langManager.getComponent(player, "gui.buttons.back.lore"))
                        .build(),
                clickedPlayer -> {
                    if (!guiManager.goBack(clickedPlayer)) {
                        clickedPlayer.closeInventory();
                    }
                }
        );
    }

    /**
     * Creates a back button item with custom action
     */
    public static GuiItem createBackButton(@NotNull Consumer<Player> action, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.ARROW)
                        .displayName(langManager.getComponent(player, "gui.buttons.back.name"))
                        .addLore(langManager.getComponent(player, "gui.buttons.back.lore"))
                        .build(),
                action
        );
    }

    /**
     * Creates a refresh button item with GuiManager integration
     */
    public static GuiItem createRefreshButton(@NotNull GuiManager guiManager, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.LIME_DYE)
                        .displayName(langManager.getComponent(player, "gui.buttons.refresh.name"))
                        .addLore(langManager.getComponent(player, "gui.buttons.refresh.lore"))
                        .build(),
                guiManager::refreshCurrentGui
        );
    }

    /**
     * Creates a refresh button item with custom action
     */
    public static GuiItem createRefreshButton(@NotNull Consumer<Player> action, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.LIME_DYE)
                        .displayName(langManager.getComponent(player, "gui.buttons.refresh.name"))
                        .addLore(langManager.getComponent(player, "gui.buttons.refresh.lore"))
                        .build(),
                action
        );
    }

    /**
     * Creates a decoration glass pane
     */
    public static GuiItem createDecoration() {
        return createDecoration(Material.GRAY_STAINED_GLASS_PANE);
    }

    /**
     * Creates a decoration glass pane with specific material
     */
    public static GuiItem createDecoration(@NotNull Material material) {
        return GuiItem.display(
                ItemBuilder.of(material)
                        .displayName(Component.empty())
                        .build()
        );
    }

    /**
     * Creates a filler item for empty slots
     */
    public static GuiItem createFiller() {
        return createDecoration(Material.BLACK_STAINED_GLASS_PANE);
    }

    /**
     * Creates a placeholder item
     */
    public static GuiItem createPlaceholder(@NotNull String text) {
        return GuiItem.display(
                ItemBuilder.of(Material.PAPER)
                        .displayName(Component.text(text, NamedTextColor.GRAY))
                        .build()
        );
    }

    /**
     * Creates a navigation button
     */
    public static GuiItem createNavigationButton(@NotNull Material material, @NotNull Component name,
                                                 @NotNull Component lore, @NotNull Consumer<Player> action) {
        return GuiItem.clickable(
                ItemBuilder.of(material)
                        .displayName(name)
                        .addLore(lore)
                        .build(),
                action
        );
    }

    /**
     * Creates a previous page button
     */
    public static GuiItem createPreviousPageButton(@NotNull Consumer<Player> action, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.ARROW)
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
                ItemBuilder.of(Material.ARROW)
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
                ItemBuilder.of(Material.PAPER)
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
                ItemBuilder.of(material)
                        .displayName(Component.text(status, isOnline ? ColorUtil.SUCCESS : ColorUtil.ERROR))
                        .addLore(langManager.getComponent(player, "gui.buttons.status.status-text", "status", statusComponent.toString()))
                        .build()
        );
    }

    /**
     * Creates a colored action button
     */
    public static GuiItem createActionButton(@NotNull Material material, @NotNull Component name,
                                             @NotNull Component description, @NotNull Consumer<Player> action,
                                             @NotNull net.kyori.adventure.text.format.TextColor nameColor) {
        return GuiItem.clickable(
                ItemBuilder.of(material)
                        .displayName(name.color(nameColor))
                        .addLore(description)
                        .build(),
                action
        );
    }

    /**
     * Creates a confirmation button (green)
     */
    public static GuiItem createConfirmButton(@NotNull Consumer<Player> action, @NotNull LangManager langManager, @NotNull Player player) {
        return createActionButton(
                Material.LIME_DYE,
                langManager.getComponent(player, "gui.buttons.confirm.name"),
                langManager.getComponent(player, "gui.buttons.confirm.lore"),
                action,
                ColorUtil.SUCCESS
        );
    }

    /**
     * Creates a cancel button (red)
     */
    public static GuiItem createCancelButton(@NotNull Consumer<Player> action, @NotNull LangManager langManager, @NotNull Player player) {
        return createActionButton(
                Material.RED_DYE,
                langManager.getComponent(player, "gui.buttons.cancel.name"),
                langManager.getComponent(player, "gui.buttons.cancel.lore"),
                action,
                ColorUtil.ERROR
        );
    }

    /**
     * Creates a warning button (orange)
     */
    public static GuiItem createWarningButton(@NotNull Component name, @NotNull Component description,
                                              @NotNull Consumer<Player> action) {
        return createActionButton(
                Material.ORANGE_DYE,
                name,
                description,
                action,
                ColorUtil.WARNING
        );
    }

    /**
     * Creates an info button (blue)
     */
    public static GuiItem createInfoButton(@NotNull Component name, @NotNull Component description,
                                           @NotNull Consumer<Player> action) {
        return createActionButton(
                Material.LIGHT_BLUE_DYE,
                name,
                description,
                action,
                ColorUtil.INFO
        );
    }
}