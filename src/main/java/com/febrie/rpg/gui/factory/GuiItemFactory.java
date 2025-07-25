package com.febrie.rpg.gui.factory;

import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * GUI 아이템 생성 팩토리
 * 자주 사용되는 GUI 아이템들을 생성하는 유틸리티 클래스
 * 
 * @author Febrie, CoffeeTory
 */
public final class GuiItemFactory {
    
    // Private constructor to prevent instantiation
    private GuiItemFactory() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * 뒤로가기 버튼 생성
     */
    @NotNull
    public static ItemStack createBackButton(@NotNull LangManager lang, @NotNull Player player) {
        return new ItemBuilder(Material.ARROW)
                .displayName(lang.getComponent(player, "gui.common.back"))
                .build();
    }
    
    /**
     * 닫기 버튼 생성
     */
    @NotNull
    public static ItemStack createCloseButton(@NotNull LangManager lang, @NotNull Player player) {
        return new ItemBuilder(Material.BARRIER)
                .displayName(lang.getComponent(player, "gui.common.close"))
                .build();
    }
    
    /**
     * 다음 페이지 버튼 생성
     */
    @NotNull
    public static ItemStack createNextPageButton(@NotNull LangManager lang, @NotNull Player player) {
        return new ItemBuilder(Material.ARROW)
                .displayName(lang.getComponent(player, "gui.common.next-page"))
                .build();
    }
    
    /**
     * 이전 페이지 버튼 생성
     */
    @NotNull
    public static ItemStack createPrevPageButton(@NotNull LangManager lang, @NotNull Player player) {
        return new ItemBuilder(Material.ARROW)
                .displayName(lang.getComponent(player, "gui.common.prev-page"))
                .build();
    }
    
    /**
     * 정보 아이템 생성
     */
    @NotNull
    public static ItemStack createInfoItem(@NotNull Material material, @NotNull Component title, 
                                         @NotNull List<Component> lore) {
        return new ItemBuilder(material)
                .displayName(title)
                .lore(lore)
                .build();
    }
    
    /**
     * 테두리 유리판 생성
     */
    @NotNull
    public static ItemStack createBorderGlass(@NotNull DyeColor color) {
        return new ItemBuilder(Material.valueOf(color.name() + "_STAINED_GLASS_PANE"))
                .displayName(Component.empty())
                .build();
    }
    
    /**
     * 기본 테두리 유리판 생성 (검은색)
     */
    @NotNull
    public static ItemStack createBorderGlass() {
        return createBorderGlass(DyeColor.BLACK);
    }
    
    /**
     * 빈 슬롯 필러 생성
     */
    @NotNull
    public static ItemStack createEmptySlot() {
        return new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName(Component.empty())
                .build();
    }
    
    /**
     * 로딩 중 아이템 생성
     */
    @NotNull
    public static ItemStack createLoadingItem(@NotNull LangManager lang, @NotNull Player player) {
        return new ItemBuilder(Material.CLOCK)
                .displayName(lang.getComponent(player, "general.loading"))
                .lore(List.of(lang.getComponent(player, "general.loading-description")))
                .build();
    }
    
    /**
     * 오류 아이템 생성
     */
    @NotNull
    public static ItemStack createErrorItem(@NotNull LangManager lang, @NotNull Player player) {
        return new ItemBuilder(Material.BARRIER)
                .displayName(lang.getComponent(player, "general.error").color(ColorUtil.ERROR))
                .build();
    }
    
    /**
     * 곧 출시 예정 아이템 생성
     */
    @NotNull
    public static ItemStack createComingSoonItem(@NotNull LangManager lang, @NotNull Player player) {
        return new ItemBuilder(Material.GRAY_DYE)
                .displayName(lang.getComponent(player, "general.coming-soon"))
                .build();
    }
    
    /**
     * 확인 버튼 생성
     */
    @NotNull
    public static ItemStack createConfirmButton(@NotNull LangManager lang, @NotNull Player player) {
        return new ItemBuilder(Material.LIME_WOOL)
                .displayName(lang.getComponent(player, "gui.common.confirm")
                        .color(ColorUtil.SUCCESS))
                .build();
    }
    
    /**
     * 취소 버튼 생성
     */
    @NotNull
    public static ItemStack createCancelButton(@NotNull LangManager lang, @NotNull Player player) {
        return new ItemBuilder(Material.RED_WOOL)
                .displayName(lang.getComponent(player, "gui.common.cancel")
                        .color(ColorUtil.ERROR))
                .build();
    }
    
    /**
     * 설정 아이템 생성
     */
    @NotNull
    public static ItemStack createSettingsItem(@NotNull LangManager lang, @NotNull Player player,
                                              @NotNull Material material) {
        return new ItemBuilder(material)
                .displayName(lang.getComponent(player, "gui.common.settings"))
                .build();
    }
    
    /**
     * 활성화/비활성화 토글 아이템 생성
     */
    @NotNull
    public static ItemStack createToggleItem(@NotNull LangManager lang, @NotNull Player player,
                                            boolean enabled, @NotNull String nameKey) {
        Material material = enabled ? Material.LIME_DYE : Material.GRAY_DYE;
        String statusKey = enabled ? "general.enabled" : "general.disabled";
        
        return new ItemBuilder(material)
                .displayName(lang.getComponent(player, nameKey))
                .lore(List.of(
                        Component.empty(),
                        lang.getComponent(player, statusKey)
                                .color(enabled ? ColorUtil.SUCCESS : ColorUtil.ERROR)
                ))
                .build();
    }
}