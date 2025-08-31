package com.febrie.rpg.gui.component;

import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangHelper;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangHelper;
import com.febrie.rpg.util.LangKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

/**
 * GUI Component Factory - Properly Configured Version
 * Utility class for creating frequently used GUI items
 *
 * @author Febrie, CoffeeTory
 */
public class GuiFactory {

    private GuiFactory() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * ?�기 버튼 ?�성
     */
    public static GuiItem createCloseButton(@NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.BARRIER)
                        .displayName(LangManager.text(LangKey.GUI_BUTTONS_CLOSE_NAME, player))
                        .addLore(LangManager.list(LangKey.GUI_BUTTONS_CLOSE_LORE, player))
                        .asGuiItem(false)
                        .build(),
                Player::closeInventory
        );
    }


    /**
     * 커스?� ?�션???�는 ?�로가�?버튼 ?�성
     */
    public static GuiItem createBackButton(@NotNull Consumer<Player> action, @NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.ARROW)
                        .displayName(LangManager.text(LangKey.GUI_BUTTONS_BACK_NAME, player))
                        .addLore(LangManager.list(LangKey.GUI_BUTTONS_BACK_LORE, player))
                        .asGuiItem(false)
                        .build(),
                action
        );
    }
    
    /**
     * ?�로고침 버튼 ?�성
     */
    public static GuiItem createRefreshButton(@NotNull Runnable action, @NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.EMERALD)
                        .displayName(LangManager.text(LangKey.GUI_BUTTONS_REFRESH_NAME, player))
                        .addLore(LangManager.list(LangKey.GUI_BUTTONS_REFRESH_LORE, player))
                        .asGuiItem(false)
                        .build(),
                p -> action.run()
        );
    }
    
    /**
     * ?�음 ?�이지 버튼 ?�성
     */
    public static GuiItem createNextPageButton(@NotNull Consumer<Player> action, @NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.ARROW)
                        .displayName(LangManager.text(LangKey.GUI_BUTTONS_NEXT_PAGE_NAME, player))
                        .addLore(LangManager.list(LangKey.GUI_BUTTONS_NEXT_PAGE_LORE, player))
                        .asGuiItem(false)
                        .build(),
                action
        );
    }
    
    /**
     * ?�전 ?�이지 버튼 ?�성
     */
    public static GuiItem createPreviousPageButton(@NotNull Consumer<Player> action, @NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.ARROW)
                        .displayName(LangManager.text(LangKey.GUI_BUTTONS_PREVIOUS_PAGE_NAME, player))
                        .addLore(LangManager.list(LangKey.GUI_BUTTONS_PREVIOUS_PAGE_LORE, player))
                        .asGuiItem(false)
                        .build(),
                action
        );
    }

    /**
     * ?�식???�이???�성
     */
    public static GuiItem createDecoration() {
        return createDecoration(Material.BLACK_STAINED_GLASS_PANE);
    }

    /**
     * ?�정 ?�료�??�식???�이???�성
     */
    public static GuiItem createDecoration(@NotNull Material material) {
        return GuiItem.display(
                ItemBuilder.of(material)
                        .displayName(Component.empty())
                        .build()
        );
    }

    /**
     * ?�레?�스?�???�이???�성
     */
    public static GuiItem createPlaceholder(@NotNull LangKey textKey, @NotNull Player player) {
        return GuiItem.display(
                ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE)
                        .displayName(LangHelper.text(textKey, player))
                        .build()
        );
    }

    /**
     * ?�러 ?�이???�성
     */
    public static GuiItem createErrorItem(@NotNull LangKey messageKey, @NotNull Player player) {
        return GuiItem.display(
                ItemBuilder.of(Material.BARRIER)
                        .displayName(LangHelper.text(LangKey.ITEMS_GUI_ERROR_TITLE, player))
                        .addLore(LangHelper.text(messageKey, player))
                        .build()
        );
    }

    /**
     * 메뉴 버튼 ?�성 (?�릭 가?�한 기능 버튼)
     * 
     * @param material 버튼 ?�료
     * @param nameKey ?�름 번역 ??     * @param loreKey ?�명 번역 ??     * @param action ?�릭 ???�작
     * @param player ?�레?�어
     * @return ?�성??메뉴 버튼
     */
    public static GuiItem createMenuButton(@NotNull Material material, @NotNull LangKey nameKey, 
                                           @NotNull LangKey loreKey, @NotNull Consumer<Player> action,
                                           @NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(material)
                        .displayName(LangHelper.text(nameKey, player))
                        .addLore(LangHelper.text(loreKey, player))
                        .asGuiItem()
                        .build(),
                action
        );
    }
    
    /**
     * ?�탯 ?�이???�성 (?�시 ?�용)
     * 
     * @param material ?�이???�료
     * @param name ?�이???�름
     * @param lore ?�이???�명 리스??     * @return ?�성???�탯 ?�이??     */
    public static GuiItem createStatItem(@NotNull Material material, @NotNull Component name, 
                                         @NotNull List<Component> lore) {
        return GuiItem.display(
                ItemBuilder.of(material)
                        .displayName(name)
                        .lore(lore)
                        .asGuiItem()
                        .build()
        );
    }
    
    /**
     * 조건부 ?�이???�성 (?�릭 가???��?가 조건???�라 결정)
     * 
     * @param builder ?�이??빌더
     * @param clickable ?�릭 가???��?
     * @param action ?�릭 ???�작 (clickable??true???�만 ?�용)
     * @return ?�성??GuiItem
     */
    public static GuiItem createConditionalItem(@NotNull ItemBuilder builder, boolean clickable, 
                                                @NotNull Consumer<Player> action) {
        builder.asGuiItem();
        
        if (clickable) {
            return GuiItem.clickable(builder.build(), action);
        } else {
            return GuiItem.display(builder.build());
        }
    }
    
    /**
     * ?�보 ?�이???�성
     */
    public static GuiItem createInfoItem(@NotNull LangKey titleKey, @NotNull LangKey loreKey, @NotNull Player player) {
        return GuiItem.display(
                ItemBuilder.of(Material.BOOK)
                        .displayName(LangHelper.text(titleKey, player))
                        .addLore(LangHelper.text(loreKey, player))
                        .build()
        );
    }

    /**
     * ?�인 버튼 ?�성
     */
    public static GuiItem createConfirmButton(@NotNull Consumer<Player> action, @NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.LIME_STAINED_GLASS_PANE)
                        .displayName(LangManager.text(LangKey.ITEMS_GUI_BUTTONS_CONFIRM_NAME, player))
                        .addLore(LangManager.text(LangKey.ITEMS_GUI_BUTTONS_CONFIRM_LORE, player))
                        .build(),
                action
        );
    }

    /**
     * 취소 버튼 ?�성
     */
    public static GuiItem createCancelButton(@NotNull Consumer<Player> action, @NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.RED_STAINED_GLASS_PANE)
                        .displayName(LangManager.text(LangKey.ITEMS_GUI_BUTTONS_CANCEL_NAME, player))
                        .addLore(LangManager.text(LangKey.ITEMS_GUI_BUTTONS_CANCEL_LORE, player))
                        .build(),
                action
        );
    }

    /**
     * ?��? 버튼 ?�성
     */
    public static GuiItem createToggleButton(boolean state, @NotNull LangKey nameKey, @NotNull Consumer<Player> action,
                                             @NotNull Player player) {
        Material material = state ? Material.LIME_DYE : Material.GRAY_DYE;
        LangKey statusKey = state ? LangKey.ITEMS_GUI_BUTTONS_TOGGLE_ENABLED : LangKey.ITEMS_GUI_BUTTONS_TOGGLE_DISABLED;

        return GuiItem.clickable(
                ItemBuilder.of(material)
                        .displayName(LangHelper.text(nameKey, player))
                        .addLore(LangHelper.text(statusKey, player))
                        .build(),
                action
        );
    }


    /**
     * ?�이지 ?�보 ?�시 ?�이???�성
     */
    public static GuiItem createPageInfo(int currentPage, int totalPages, @NotNull Player player) {
        // ?�이지 ?�보???�적 ?�이?��? ?�함?��?�?Component�?직접 처리
        Component pageTitle = LangManager.text(LangKey.ITEMS_GUI_BUTTONS_PAGE_INFO_NAME, player);
        Component pageInfo = LangManager.text(LangKey.GUI_BUTTONS_PAGE_INFO_LORE, player, 
                                                      Component.text(currentPage), Component.text(totalPages));
        
        return GuiItem.display(
                ItemBuilder.of(Material.PAPER)
                        .displayName(pageTitle)
                        .addLore(pageInfo)
                        .build()
        );
    }

    /**
     * ?�태 ?�시 ?�이???�성
     */
    public static GuiItem createStatusIndicator(@NotNull String status, boolean isOnline, @NotNull Player player) {
        Material material = isOnline ? Material.LIME_DYE : Material.RED_DYE;
        LangKey statusKey = isOnline ? LangKey.ITEMS_GUI_BUTTONS_STATUS_ONLINE : LangKey.ITEMS_GUI_BUTTONS_STATUS_OFFLINE;

        return GuiItem.display(
                ItemBuilder.of(material)
                        .displayName(Component.text(status, isOnline ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.ERROR))
                        .addLore(LangHelper.text(statusKey, player))
                        .build()
        );
    }
}
