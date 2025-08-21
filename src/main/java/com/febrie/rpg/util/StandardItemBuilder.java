package com.febrie.rpg.util;

import com.febrie.rpg.gui.component.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 표준화된 ItemBuilder 래퍼
 * 일관된 사용 패턴을 강제하는 유틸리티 클래스
 *
 * @author Febrie, CoffeeTory
 */
public class StandardItemBuilder {

    /**
     * 표준 GUI 아이템 생성 (모든 플래그 숨김)
     */
    @NotNull
    public static ItemBuilder guiItem(@NotNull Material material) {
        return ItemBuilder.of(material).hideAllFlags();
    }

    /**
     * 표준 GUI 아이템 생성 (이름 포함)
     */
    @NotNull
    public static ItemBuilder guiItem(@NotNull Material material, @NotNull String name) {
        return guiItem(material).displayName(UnifiedColorUtil.parseComponent(name));
    }

    /**
     * 표준 GUI 아이템 생성 (이름과 설명 포함)
     */
    @NotNull
    public static ItemBuilder guiItem(@NotNull Material material, @NotNull String name, @NotNull String @NotNull ... lore) {
        ItemBuilder builder = guiItem(material, name);
        for (String line : lore)
            builder.addLore(UnifiedColorUtil.parseComponent(line));
        return builder;
    }

    /**
     * 클릭 가능한 GUI 아이템 생성
     */
    @NotNull
    public static GuiItem clickableItem(@NotNull Material material, @NotNull String name, @NotNull Runnable action) {
        return GuiItem.clickable(guiItem(material, name), player -> action.run());
    }

    /**
     * 표시용 GUI 아이템 생성
     */
    @NotNull
    public static GuiItem displayItem(@NotNull Material material, @NotNull String name, @NotNull String... lore) {
        return GuiItem.display(guiItem(material, name, lore));
    }

    /**
     * 빈 슬롯 채우기용 아이템
     */
    @NotNull
    public static ItemBuilder filler(@NotNull Material material) {
        return ItemBuilder.of(material).displayName(Component.empty()).hideAllFlags();
    }

    /**
     * 기본 유리판 필러
     */
    @NotNull
    public static ItemBuilder glassPaneFiller(@NotNull Material glassPaneType) {
        return filler(glassPaneType);
    }

    /**
     * 뒤로 가기 버튼
     */
    @NotNull
    public static ItemBuilder backButton() {
        return guiItem(Material.ARROW, "&f뒤로 가기", "&7이전 메뉴로 돌아갑니다.");
    }

    /**
     * 닫기 버튼
     */
    @NotNull
    public static ItemBuilder closeButton() {
        return guiItem(Material.BARRIER, "&c닫기", "&7메뉴를 닫습니다.");
    }

    /**
     * 다음 페이지 버튼
     */
    @NotNull
    public static ItemBuilder nextPageButton(int currentPage) {
        return guiItem(Material.ARROW, "&f다음 페이지", "&7현재 페이지: &e" + currentPage, "&7클릭하여 다음 페이지로 이동");
    }

    /**
     * 이전 페이지 버튼
     */
    @NotNull
    public static ItemBuilder previousPageButton(int currentPage) {
        return guiItem(Material.ARROW, "&f이전 페이지", "&7현재 페이지: &e" + currentPage, "&7클릭하여 이전 페이지로 이동");
    }

    /**
     * 정보 아이템
     */
    @NotNull
    public static ItemBuilder infoItem(@NotNull String title, @NotNull String... info) {
        return guiItem(Material.BOOK, title, info);
    }

    /**
     * 확인 버튼
     */
    @NotNull
    public static ItemBuilder confirmButton() {
        return guiItem(Material.LIME_DYE, "&a확인", "&7클릭하여 확인");
    }

    /**
     * 취소 버튼
     */
    @NotNull
    public static ItemBuilder cancelButton() {
        return guiItem(Material.RED_DYE, "&c취소", "&7클릭하여 취소");
    }

    /**
     * 설정 버튼
     */
    @NotNull
    public static ItemBuilder settingsButton() {
        return guiItem(Material.COMPARATOR, "&e설정", "&7클릭하여 설정 열기");
    }

    /**
     * ItemStack에서 ItemBuilder 생성 (표준화)
     */
    @NotNull
    public static ItemBuilder from(@NotNull ItemStack item) {
        return ItemBuilder.from(item).hideAllFlags();
    }
}