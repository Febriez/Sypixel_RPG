package com.febrie.rpg.gui.component;

import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

/**
 * GUI 컴포넌트 팩토리 - 올바르게 수정된 버전
 * 자주 사용되는 GUI 아이템 생성을 위한 유틸리티 클래스
 *
 * @author Febrie, CoffeeTory
 */
public class GuiFactory {

    private GuiFactory() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 닫기 버튼 생성
     */
    public static GuiItem createCloseButton(@NotNull Player player) {
        return GuiItem.clickable(
                new ItemBuilder(Material.BARRIER)
                        .displayName(Component.translatable("gui.buttons.close.name"))
                        .addLore(Component.translatable("gui.buttons.close.lore"))
                        .asGuiItem(false)
                        .build(),
                Player::closeInventory
        );
    }


    /**
     * 커스텀 액션이 있는 뒤로가기 버튼 생성
     */
    public static GuiItem createBackButton(@NotNull Consumer<Player> action, @NotNull Player player) {
        return GuiItem.clickable(
                new ItemBuilder(Material.ARROW)
                        .displayName(Component.translatable("gui.buttons.back.name"))
                        .addLore(Component.translatable("gui.buttons.back.lore"))
                        .asGuiItem(false)
                        .build(),
                action
        );
    }
    
    /**
     * 새로고침 버튼 생성
     */
    public static GuiItem createRefreshButton(@NotNull Runnable action, @NotNull Player player) {
        return GuiItem.clickable(
                new ItemBuilder(Material.EMERALD)
                        .displayName(Component.translatable("gui.buttons.refresh.name"))
                        .addLore(Component.translatable("gui.buttons.refresh.lore"))
                        .asGuiItem(false)
                        .build(),
                p -> action.run()
        );
    }
    
    /**
     * 다음 페이지 버튼 생성
     */
    public static GuiItem createNextPageButton(@NotNull Consumer<Player> action, @NotNull Player player) {
        return GuiItem.clickable(
                new ItemBuilder(Material.ARROW)
                        .displayName(Component.translatable("gui.buttons.next-page.name"))
                        .addLore(Component.translatable("gui.buttons.next-page.lore"))
                        .asGuiItem(false)
                        .build(),
                action
        );
    }
    
    /**
     * 이전 페이지 버튼 생성
     */
    public static GuiItem createPreviousPageButton(@NotNull Consumer<Player> action, @NotNull Player player) {
        return GuiItem.clickable(
                new ItemBuilder(Material.ARROW)
                        .displayName(Component.translatable("gui.buttons.previous-page.name"))
                        .addLore(Component.translatable("gui.buttons.previous-page.lore"))
                        .asGuiItem(false)
                        .build(),
                action
        );
    }

    /**
     * 장식용 아이템 생성
     */
    public static GuiItem createDecoration() {
        return createDecoration(Material.BLACK_STAINED_GLASS_PANE);
    }

    /**
     * 특정 재료로 장식용 아이템 생성
     */
    public static GuiItem createDecoration(@NotNull Material material) {
        return GuiItem.display(
                new ItemBuilder(material)
                        .displayName(Component.empty())
                        .build()
        );
    }

    /**
     * 플레이스홀더 아이템 생성
     */
    public static GuiItem createPlaceholder(@NotNull String text, @NotNull Player player) {
        return GuiItem.display(
                new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                        .displayName(Component.translatable(text))
                        .build()
        );
    }

    /**
     * 에러 아이템 생성
     */
    public static GuiItem createErrorItem(@NotNull String message, @NotNull Player player) {
        return GuiItem.display(
                new ItemBuilder(Material.BARRIER)
                        .displayName(Component.translatable("gui.error.title").color(NamedTextColor.RED))
                        .addLore(Component.translatable(message))
                        .build()
        );
    }

    /**
     * 메뉴 버튼 생성 (클릭 가능한 기능 버튼)
     * 
     * @param material 버튼 재료
     * @param nameKey 이름 번역 키
     * @param loreKey 설명 번역 키
     * @param action 클릭 시 동작
     * @param langManager 언어 관리자
     * @param player 플레이어
     * @return 생성된 메뉴 버튼
     */
    public static GuiItem createMenuButton(@NotNull Material material, @NotNull String nameKey, 
                                           @NotNull String loreKey, @NotNull Consumer<Player> action,
                                           @NotNull Player player) {
        return GuiItem.clickable(
                new ItemBuilder(material)
                        .displayName(Component.translatable(nameKey))
                        .addLore(Component.translatable(loreKey))
                        .asGuiItem()
                        .build(),
                action
        );
    }
    
    /**
     * 스탯 아이템 생성 (표시 전용)
     * 
     * @param material 아이템 재료
     * @param name 아이템 이름
     * @param lore 아이템 설명 리스트
     * @return 생성된 스탯 아이템
     */
    public static GuiItem createStatItem(@NotNull Material material, @NotNull Component name, 
                                         @NotNull List<Component> lore) {
        return GuiItem.display(
                new ItemBuilder(material)
                        .displayName(name)
                        .lore(lore)
                        .asGuiItem()
                        .build()
        );
    }
    
    /**
     * 조건부 아이템 생성 (클릭 가능 여부가 조건에 따라 결정)
     * 
     * @param builder 아이템 빌더
     * @param clickable 클릭 가능 여부
     * @param action 클릭 시 동작 (clickable이 true일 때만 사용)
     * @return 생성된 GuiItem
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
     * 정보 아이템 생성
     */
    public static GuiItem createInfoItem(@NotNull String title, @NotNull String lore, @NotNull Player player) {
        return GuiItem.display(
                new ItemBuilder(Material.BOOK)
                        .displayName(Component.translatable(title))
                        .addLore(Component.translatable(lore))
                        .build()
        );
    }

    /**
     * 확인 버튼 생성
     */
    public static GuiItem createConfirmButton(@NotNull Consumer<Player> action, @NotNull Player player) {
        return GuiItem.clickable(
                new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                        .displayName(Component.translatable("gui.buttons.confirm.name"))
                        .addLore(Component.translatable("gui.buttons.confirm.lore"))
                        .build(),
                action
        );
    }

    /**
     * 취소 버튼 생성
     */
    public static GuiItem createCancelButton(@NotNull Consumer<Player> action, @NotNull Player player) {
        return GuiItem.clickable(
                new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                        .displayName(Component.translatable("gui.buttons.cancel.name"))
                        .addLore(Component.translatable("gui.buttons.cancel.lore"))
                        .build(),
                action
        );
    }

    /**
     * 토글 버튼 생성
     */
    public static GuiItem createToggleButton(boolean state, @NotNull String nameKey, @NotNull Consumer<Player> action,
                                             @NotNull Player player) {
        Material material = state ? Material.LIME_DYE : Material.GRAY_DYE;
        String statusKey = state ? "gui.buttons.toggle.enabled" : "gui.buttons.toggle.disabled";

        return GuiItem.clickable(
                new ItemBuilder(material)
                        .displayName(Component.translatable(nameKey))
                        .addLore(Component.translatable(statusKey))
                        .build(),
                action
        );
    }


    /**
     * 페이지 정보 표시 아이템 생성
     */
    public static GuiItem createPageInfo(int currentPage, int totalPages, @NotNull Player player) {
        return GuiItem.display(
                new ItemBuilder(Material.PAPER)
                        .displayName(Component.translatable("gui.buttons.page-info.name"))
                        .addLore(Component.translatable("gui.buttons.page-info.lore", Component.text(currentPage), Component.text(totalPages)))
                        .build()
        );
    }

    /**
     * 상태 표시 아이템 생성
     */
    public static GuiItem createStatusIndicator(@NotNull String status, boolean isOnline, @NotNull Player player) {
        Material material = isOnline ? Material.LIME_DYE : Material.RED_DYE;
        Component statusComponent = Component.translatable(isOnline ? "gui.buttons.status.online" : "gui.buttons.status.offline");

        return GuiItem.display(
                new ItemBuilder(material)
                        .displayName(Component.text(status, isOnline ? UnifiedColorUtil.SUCCESS : UnifiedColorUtil.ERROR))
                        .addLore(statusComponent)
                        .build()
        );
    }
}