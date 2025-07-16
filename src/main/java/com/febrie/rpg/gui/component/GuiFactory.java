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
 * GUI 컴포넌트 팩토리 - 올바르게 수정된 버전
 * 자주 사용되는 GUI 아이템 생성을 위한 유틸리티 클래스
 *
 * @author Febrie, CoffeeTory
 */
public class GuiFactory {

    private GuiFactory() {
        throw new UnsupportedOperationException("유틸리티 클래스는 인스턴스화할 수 없습니다.");
    }

    /**
     * 닫기 버튼 생성
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
     * 커스텀 액션이 있는 뒤로가기 버튼 생성
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
    public static GuiItem createPlaceholder(@NotNull String text, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.display(
                new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                        .displayName(langManager.getComponent(player, text))
                        .build()
        );
    }

    /**
     * 에러 아이템 생성
     */
    public static GuiItem createErrorItem(@NotNull String message, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.display(
                new ItemBuilder(Material.BARRIER)
                        .displayName(Component.text("오류", NamedTextColor.RED))
                        .addLore(langManager.getComponent(player, message))
                        .build()
        );
    }

    /**
     * 정보 아이템 생성
     */
    public static GuiItem createInfoItem(@NotNull String title, @NotNull String lore, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.display(
                new ItemBuilder(Material.BOOK)
                        .displayName(langManager.getComponent(player, title))
                        .addLore(langManager.getComponent(player, lore))
                        .build()
        );
    }

    /**
     * 확인 버튼 생성
     */
    public static GuiItem createConfirmButton(@NotNull Consumer<Player> action, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.clickable(
                new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                        .displayName(langManager.getComponent(player, "gui.buttons.confirm.name"))
                        .addLore(langManager.getComponent(player, "gui.buttons.confirm.lore"))
                        .build(),
                action
        );
    }

    /**
     * 취소 버튼 생성
     */
    public static GuiItem createCancelButton(@NotNull Consumer<Player> action, @NotNull LangManager langManager, @NotNull Player player) {
        return GuiItem.clickable(
                new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                        .displayName(langManager.getComponent(player, "gui.buttons.cancel.name"))
                        .addLore(langManager.getComponent(player, "gui.buttons.cancel.lore"))
                        .build(),
                action
        );
    }

    /**
     * 토글 버튼 생성
     */
    public static GuiItem createToggleButton(boolean state, @NotNull String nameKey, @NotNull Consumer<Player> action,
                                             @NotNull LangManager langManager, @NotNull Player player) {
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
     * 이전 페이지 버튼 생성
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
     * 다음 페이지 버튼 생성
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
     * 페이지 정보 표시 아이템 생성
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
     * 상태 표시 아이템 생성
     */
    public static GuiItem createStatusIndicator(@NotNull String status, boolean isOnline, @NotNull LangManager langManager, @NotNull Player player) {
        Material material = isOnline ? Material.LIME_DYE : Material.RED_DYE;
        Component statusComponent = langManager.getComponent(player, isOnline ? "gui.buttons.status.online" : "gui.buttons.status.offline");

        return GuiItem.display(
                new ItemBuilder(material)
                        .displayName(Component.text(status, isOnline ? ColorUtil.SUCCESS : ColorUtil.ERROR))
                        .addLore(statusComponent)
                        .build()
        );
    }
}