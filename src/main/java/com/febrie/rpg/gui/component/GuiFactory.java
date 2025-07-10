package com.febrie.rpg.gui.component;

import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Factory class for creating common GUI components with internationalization support
 * ItemBuilder automatically handles italic prevention, keeping this class clean and simple
 *
 * @author Febrie, CoffeeTory
 */
public final class GuiFactory {

    private GuiFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Creates a close button item with localization support
     */
    public static GuiItem createCloseButton(@NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.BARRIER)
                        .displayName(langManager.getComponent(player, "items.close.name"))
                        .addLore(langManager.getMessage(player, "items.close.lore"), ColorUtil.NEUTRAL)
                        .build(),
                Player::closeInventory
        );
    }

    /**
     * Creates a close button item (legacy support)
     */
    public static GuiItem createCloseButton() {
        return GuiItem.clickable(
                ItemBuilder.of(Material.BARRIER)
                        .displayName(Component.text("닫기", ColorUtil.ERROR))
                        .addLore(Component.text("클릭하여 GUI 닫기", ColorUtil.NEUTRAL))
                        .build(),
                Player::closeInventory
        );
    }

    /**
     * Creates a back button item with GuiManager integration and localization
     */
    public static GuiItem createBackButton(@NotNull GuiManager guiManager, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.ARROW)
                        .displayName(langManager.getComponent(player, "items.back.name"))
                        .addLore(langManager.getMessage(player, "items.back.lore"), ColorUtil.NEUTRAL)
                        .build(),
                clickedPlayer -> {
                    if (!guiManager.goBack(clickedPlayer)) {
                        clickedPlayer.closeInventory();
                    }
                }
        );
    }

    /**
     * Creates a back button item with GuiManager integration (legacy support)
     */
    public static GuiItem createBackButton(@NotNull GuiManager guiManager) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.ARROW)
                        .displayName(Component.text("뒤로가기", ColorUtil.WARNING))
                        .addLore(Component.text("클릭하여 이전 메뉴로", ColorUtil.NEUTRAL))
                        .build(),
                player -> {
                    if (!guiManager.goBack(player)) {
                        player.closeInventory();
                    }
                }
        );
    }

    /**
     * Creates a back button item with custom action and localization
     */
    public static GuiItem createBackButton(@NotNull Consumer<Player> action, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.ARROW)
                        .displayName(langManager.getComponent(player, "items.back.name"))
                        .addLore(langManager.getMessage(player, "items.back.lore"), ColorUtil.NEUTRAL)
                        .build(),
                action
        );
    }

    /**
     * Creates a back button item with custom action (legacy support)
     */
    public static GuiItem createBackButton(@NotNull Consumer<Player> action) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.ARROW)
                        .displayName(Component.text("뒤로가기", ColorUtil.WARNING))
                        .addLore(Component.text("클릭하여 이전 메뉴로", ColorUtil.NEUTRAL))
                        .build(),
                action
        );
    }

    /**
     * Creates a refresh button item with GuiManager integration and localization
     */
    public static GuiItem createRefreshButton(@NotNull GuiManager guiManager, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.LIME_DYE)
                        .displayName(langManager.getComponent(player, "items.refresh.name"))
                        .addLore(langManager.getMessage(player, "items.refresh.lore"), ColorUtil.NEUTRAL)
                        .build(),
                guiManager::refreshCurrentGui
        );
    }

    /**
     * Creates a refresh button item with GuiManager integration (legacy support)
     */
    public static GuiItem createRefreshButton(@NotNull GuiManager guiManager) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.LIME_DYE)
                        .displayName(Component.text("새로고침", ColorUtil.SUCCESS))
                        .addLore(Component.text("클릭하여 정보 갱신", ColorUtil.NEUTRAL))
                        .build(),
                guiManager::refreshCurrentGui
        );
    }

    /**
     * Creates a refresh button item with custom action and localization
     */
    public static GuiItem createRefreshButton(@NotNull Consumer<Player> action, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.LIME_DYE)
                        .displayName(langManager.getComponent(player, "items.refresh.name"))
                        .addLore(langManager.getMessage(player, "items.refresh.lore"), ColorUtil.NEUTRAL)
                        .build(),
                action
        );
    }

    /**
     * Creates a refresh button item with custom action (legacy support)
     */
    public static GuiItem createRefreshButton(@NotNull Consumer<Player> action) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.LIME_DYE)
                        .displayName(Component.text("새로고침", ColorUtil.SUCCESS))
                        .addLore(Component.text("클릭하여 정보 갱신", ColorUtil.NEUTRAL))
                        .build(),
                action
        );
    }

    /**
     * Creates a decoration glass pane (no display name, completely invisible)
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
     * Creates a placeholder item with localization
     */
    public static GuiItem createPlaceholder(@NotNull String textKey, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.display(
                ItemBuilder.of(Material.PAPER)
                        .displayName(langManager.getComponent(player, textKey))
                        .build()
        );
    }

    /**
     * Creates a placeholder item (legacy support)
     */
    public static GuiItem createPlaceholder(@NotNull String text) {
        return GuiItem.display(
                ItemBuilder.of(Material.PAPER)
                        .displayName(Component.text(text, ColorUtil.NEUTRAL))
                        .build()
        );
    }

    /**
     * Creates a navigation button with localization
     */
    public static GuiItem createNavigationButton(@NotNull Material material, @NotNull String nameKey,
                                                 @NotNull Consumer<Player> action, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(material)
                        .displayName(langManager.getComponent(player, nameKey))
                        .addLore(langManager.getMessage(player, "general.click-to-navigate"), ColorUtil.NEUTRAL)
                        .build(),
                action
        );
    }

    /**
     * Creates a navigation button (legacy support)
     */
    public static GuiItem createNavigationButton(@NotNull Material material, @NotNull String name,
                                                 @NotNull Consumer<Player> action) {
        return GuiItem.clickable(
                ItemBuilder.of(material)
                        .displayName(Component.text(name, ColorUtil.INFO))
                        .addLore(Component.text("클릭하여 이동", ColorUtil.NEUTRAL))
                        .build(),
                action
        );
    }

    /**
     * Creates a previous page button with localization
     */
    public static GuiItem createPreviousPageButton(@NotNull Consumer<Player> action, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.ARROW)
                        .displayName(langManager.getComponent(player, "items.previous-page.name"))
                        .addLore(langManager.getMessage(player, "items.previous-page.lore"), ColorUtil.NEUTRAL)
                        .build(),
                action
        );
    }

    /**
     * Creates a previous page button (legacy support)
     */
    public static GuiItem createPreviousPageButton(@NotNull Consumer<Player> action) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.ARROW)
                        .displayName(Component.text("이전 페이지", ColorUtil.WARNING))
                        .addLore(Component.text("클릭하여 이전 페이지로", ColorUtil.NEUTRAL))
                        .build(),
                action
        );
    }

    /**
     * Creates a next page button with localization
     */
    public static GuiItem createNextPageButton(@NotNull Consumer<Player> action, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.ARROW)
                        .displayName(langManager.getComponent(player, "items.next-page.name"))
                        .addLore(langManager.getMessage(player, "items.next-page.lore"), ColorUtil.NEUTRAL)
                        .build(),
                action
        );
    }

    /**
     * Creates a next page button (legacy support)
     */
    public static GuiItem createNextPageButton(@NotNull Consumer<Player> action) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.ARROW)
                        .displayName(Component.text("다음 페이지", ColorUtil.WARNING))
                        .addLore(Component.text("클릭하여 다음 페이지로", ColorUtil.NEUTRAL))
                        .build(),
                action
        );
    }

    /**
     * Creates a page info display item with localization
     */
    public static GuiItem createPageInfo(int currentPage, int totalPages, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.display(
                ItemBuilder.of(Material.PAPER)
                        .displayName(langManager.getComponent(player, "items.page-info.name"))
                        .addLore(langManager.getMessage(player, "items.page-info.lore",
                                "current", String.valueOf(currentPage),
                                "total", String.valueOf(totalPages)), ColorUtil.INFO)
                        .build()
        );
    }

    /**
     * Creates a page info display item (legacy support)
     */
    public static GuiItem createPageInfo(int currentPage, int totalPages) {
        return GuiItem.display(
                ItemBuilder.of(Material.PAPER)
                        .displayName(Component.text("페이지 정보", ColorUtil.ORANGE))
                        .addLore(Component.text(String.format("현재: %d / %d", currentPage, totalPages), ColorUtil.INFO))
                        .build()
        );
    }

    /**
     * Creates a status indicator item with localization
     */
    public static GuiItem createStatusIndicator(@NotNull String statusKey, boolean isOnline,
                                                @NotNull LangManager langManager, @NotNull Player player) {
        Material material = isOnline ? Material.LIME_DYE : Material.RED_DYE;
        String statusValue = langManager.getMessage(player, isOnline ? "status.online" : "status.offline");

        return GuiItem.display(
                ItemBuilder.of(material)
                        .displayName(langManager.getComponent(player, statusKey))
                        .addLore(langManager.getMessage(player, "items.status-indicator.lore", "status", statusValue),
                                isOnline ? ColorUtil.SUCCESS : ColorUtil.ERROR)
                        .build()
        );
    }

    /**
     * Creates a status indicator item (legacy support)
     */
    public static GuiItem createStatusIndicator(@NotNull String status, boolean isOnline) {
        Material material = isOnline ? Material.LIME_DYE : Material.RED_DYE;
        String statusText = isOnline ? "온라인" : "오프라인";

        return GuiItem.display(
                ItemBuilder.of(material)
                        .displayName(Component.text(status, isOnline ? ColorUtil.SUCCESS : ColorUtil.ERROR))
                        .addLore(Component.text("상태: " + statusText, ColorUtil.NEUTRAL))
                        .build()
        );
    }
}