package com.febrie.rpg.gui.component;

import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Factory class for creating common GUI components
 * Provides utility methods for consistent GUI elements
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
    public static GuiItem createCloseButton() {
        return GuiItem.clickable(
                ItemBuilder.of(Material.BARRIER)
                        .displayName(Component.text("닫기", NamedTextColor.RED))
                        .addLore("클릭하여 GUI 닫기", NamedTextColor.GRAY)
                        .build(),
                Player::closeInventory
        );
    }

    /**
     * Creates a back button item with GuiManager integration
     */
    public static GuiItem createBackButton(@NotNull GuiManager guiManager) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.ARROW)
                        .displayName(Component.text("뒤로가기", NamedTextColor.YELLOW))
                        .addLore("클릭하여 이전 메뉴로", NamedTextColor.GRAY)
                        .build(),
                player -> {
                    if (!guiManager.goBack(player)) {
                        player.closeInventory();
                    }
                }
        );
    }

    /**
     * Creates a back button item with custom action
     */
    public static GuiItem createBackButton(@NotNull Consumer<Player> action) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.ARROW)
                        .displayName(Component.text("뒤로가기", NamedTextColor.YELLOW))
                        .addLore("클릭하여 이전 메뉴로", NamedTextColor.GRAY)
                        .build(),
                action
        );
    }

    /**
     * Creates a refresh button item with GuiManager integration
     */
    public static GuiItem createRefreshButton(@NotNull GuiManager guiManager) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.LIME_DYE)
                        .displayName(Component.text("새로고침", NamedTextColor.GREEN))
                        .addLore("클릭하여 정보 갱신", NamedTextColor.GRAY)
                        .build(),
                guiManager::refreshCurrentGui
        );
    }

    /**
     * Creates a refresh button item with custom action
     */
    public static GuiItem createRefreshButton(@NotNull Consumer<Player> action) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.LIME_DYE)
                        .displayName(Component.text("새로고침", NamedTextColor.GREEN))
                        .addLore("클릭하여 정보 갱신", NamedTextColor.GRAY)
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
    public static GuiItem createNavigationButton(@NotNull Material material, @NotNull String name,
                                                 @NotNull Consumer<Player> action) {
        return GuiItem.clickable(
                ItemBuilder.of(material)
                        .displayName(Component.text(name, NamedTextColor.WHITE))
                        .addLore("클릭하여 이동", NamedTextColor.GRAY)
                        .build(),
                action
        );
    }

    /**
     * Creates a previous page button
     */
    public static GuiItem createPreviousPageButton(@NotNull Consumer<Player> action) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.ARROW)
                        .displayName(Component.text("이전 페이지", NamedTextColor.YELLOW))
                        .addLore("클릭하여 이전 페이지로", NamedTextColor.GRAY)
                        .build(),
                action
        );
    }

    /**
     * Creates a next page button
     */
    public static GuiItem createNextPageButton(@NotNull Consumer<Player> action) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.ARROW)
                        .displayName(Component.text("다음 페이지", NamedTextColor.YELLOW))
                        .addLore("클릭하여 다음 페이지로", NamedTextColor.GRAY)
                        .build(),
                action
        );
    }

    /**
     * Creates a page info display item
     */
    public static GuiItem createPageInfo(int currentPage, int totalPages) {
        return GuiItem.display(
                ItemBuilder.of(Material.PAPER)
                        .displayName(Component.text("페이지 정보", ColorUtil.ORANGE))
                        .addLore(String.format("현재: %d / %d", currentPage, totalPages), NamedTextColor.WHITE)
                        .build()
        );
    }

    /**
     * Creates a status indicator item
     */
    public static GuiItem createStatusIndicator(@NotNull String status, boolean isOnline) {
        Material material = isOnline ? Material.LIME_DYE : Material.RED_DYE;
        String statusText = isOnline ? "온라인" : "오프라인";

        return GuiItem.display(
                ItemBuilder.of(material)
                        .displayName(Component.text(status, isOnline ? ColorUtil.SUCCESS : ColorUtil.ERROR))
                        .addLore("상태: " + statusText, NamedTextColor.GRAY)
                        .build()
        );
    }
}