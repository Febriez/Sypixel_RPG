package com.febrie.rpg.gui.util;

import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.impl.job.JobSelectionGui;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.stat.Stat;
import com.febrie.rpg.talent.Talent;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.SoundUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * GUI 관련 유틸리티 클래스
 * ItemBuilder를 활용한 GUI 아이템 생성 도우미
 * GuiService 기능 통합
 *
 * @author Febrie, CoffeeTory
 */
public class GuiUtility {

    private GuiUtility() {
        throw new UnsupportedOperationException("유틸리티 클래스는 인스턴스화할 수 없습니다.");
    }

    /**
     * GUI 아이템 설정 - 핵심 메소드
     * Map과 Inventory 동시 업데이트
     */
    public static void setItem(int slot, @NotNull GuiItem item,
                               @NotNull Map<Integer, GuiItem> items,
                               @NotNull Inventory inventory) {
        if (slot < 0 || slot >= inventory.getSize()) {
            return; // 잘못된 슬롯 번호 무시
        }

        items.put(slot, item);
        inventory.setItem(slot, item.getItemStack());
    }

    /**
     * 여러 슬롯에 같은 아이템 설정
     */
    public static void setItems(@NotNull GuiItem item,
                                @NotNull Map<Integer, GuiItem> items,
                                @NotNull Inventory inventory,
                                int... slots) {
        for (int slot : slots) {
            setItem(slot, item, items, inventory);
        }
    }

    /**
     * 특정 행에 아이템 설정
     */
    public static void setRow(int row, @NotNull GuiItem item,
                              @NotNull Map<Integer, GuiItem> items,
                              @NotNull Inventory inventory) {
        if (row < 0 || row >= 6) return;

        for (int col = 0; col < 9; col++) {
            setItem(row * 9 + col, item, items, inventory);
        }
    }

    /**
     * 특정 열에 아이템 설정
     */
    public static void setColumn(int column, @NotNull GuiItem item,
                                 @NotNull Map<Integer, GuiItem> items,
                                 @NotNull Inventory inventory) {
        if (column < 0 || column >= 9) return;

        for (int row = 0; row < 6; row++) {
            setItem(row * 9 + column, item, items, inventory);
        }
    }

    /**
     * 플레이어 머리 아이템 생성
     */
    @NotNull
    public static ItemStack createPlayerHead(@NotNull Player player, @NotNull com.febrie.rpg.util.LangManager langManager) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        meta.setOwningPlayer(player);
        meta.displayName(com.febrie.rpg.util.LangManager.getMessage(player, "gui.profile.player-info.name",
                "player", player.getName()));

        head.setItemMeta(meta);
        return head;
    }

    /**
     * 스탯 정보 아이템 생성
     */
    @NotNull
    public static GuiItem createStatItem(@NotNull RPGPlayer rpgPlayer, @NotNull Stat stat,
                                         @NotNull com.febrie.rpg.util.LangManager langManager) {
        Player player = rpgPlayer.getPlayer();
        if (player == null) {
            return GuiItem.display(new ItemStack(Material.BARRIER));
        }

        int value = rpgPlayer.getStats().getBaseStat(stat);
        int bonus = rpgPlayer.getStats().getBonusStat(stat);

        ItemBuilder builder = ItemBuilder.of(stat.getIcon())
                .displayName(com.febrie.rpg.util.LangManager.getMessage(player, "stat." + stat.getId().toLowerCase() + ".name"))
                .flags(ItemFlag.values());

        // 설명 추가
        List<Component> lore = new ArrayList<>();
        lore.addAll(com.febrie.rpg.util.LangManager.getComponentList(player, "stat." + stat.getId().toLowerCase() + ".description"));

        // 현재 값 표시
        lore.add(Component.empty());
        lore.add(Component.text("현재 값: ")
                .color(NamedTextColor.GRAY)
                .append(Component.text(String.valueOf(value))
                        .color(NamedTextColor.WHITE)
                        .decoration(TextDecoration.BOLD, true)));

        // 보너스 표시 (있는 경우)
        if (bonus > 0) {
            lore.add(Component.text("보너스: ")
                    .color(NamedTextColor.GRAY)
                    .append(Component.text("+" + bonus)
                            .color(NamedTextColor.GREEN)
                            .decoration(TextDecoration.BOLD, true)));
        }

        return GuiItem.display(builder.lore(lore).build());
    }

    /**
     * 특성 아이템 생성
     */
    @NotNull
    public static GuiItem createTalentItem(@NotNull Talent talent, @NotNull RPGPlayer rpgPlayer,
                                           @NotNull com.febrie.rpg.util.LangManager langManager) {
        Player player = rpgPlayer.getPlayer();
        if (player == null) {
            return GuiItem.display(new ItemStack(Material.BARRIER));
        }

        int currentLevel = rpgPlayer.getTalents().getTalentLevel(talent);
        boolean isLearned = currentLevel > 0;
        boolean canLearn = talent.canActivate(rpgPlayer.getTalents());

        Material material = talent.getIcon();
        ItemBuilder builder = ItemBuilder.of(material)
                .displayName(com.febrie.rpg.util.LangManager.getMessage(player, "talent." + talent.getId() + ".name"))
                .flags(ItemFlag.values());

        // 설명 추가
        List<Component> lore = new ArrayList<>();
        lore.addAll(com.febrie.rpg.util.LangManager.getComponentList(player, "talent." + talent.getId() + ".description"));

        // 현재 레벨 표시
        lore.add(Component.empty());
        lore.add(Component.text("레벨: " + currentLevel + "/" + talent.getMaxLevel())
                .color(NamedTextColor.WHITE));

        // 상태 표시
        if (currentLevel >= talent.getMaxLevel()) {
            lore.add(Component.text("✓ 최대 레벨")
                    .color(NamedTextColor.GREEN)
                    .decoration(TextDecoration.BOLD, true));
        } else if (canLearn) {
            lore.add(Component.text("클릭하여 레벨업")
                    .color(NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, true));
        } else {
            lore.add(Component.text("✗ 레벨업 불가")
                    .color(NamedTextColor.RED)
                    .decoration(TextDecoration.BOLD, true));
        }

        // 필요 조건 표시 (선행 특성)
        Map<Talent, Integer> prerequisites = talent.getPrerequisites();
        if (!prerequisites.isEmpty()) {
            lore.add(Component.empty());
            lore.add(Component.text("필요 조건:")
                    .color(NamedTextColor.GRAY));

            for (Map.Entry<Talent, Integer> entry : prerequisites.entrySet()) {
                Talent prereqTalent = entry.getKey();
                int requiredLevel = entry.getValue();
                int playerLevel = rpgPlayer.getTalents().getTalentLevel(prereqTalent);
                boolean meets = playerLevel >= requiredLevel;

                Component prereqName = com.febrie.rpg.util.LangManager.getMessage(player, "talent." + prereqTalent.getId() + ".name");
                lore.add(Component.text("• ")
                        .append(prereqName)
                        .append(Component.text(" Lv." + requiredLevel))
                        .color(meets ? NamedTextColor.GREEN : NamedTextColor.RED));
            }
        }

        // 특수 효과 표시
        List<String> effects = talent.getEffects();
        if (!effects.isEmpty()) {
            lore.add(Component.empty());
            lore.add(Component.text("효과:")
                    .color(NamedTextColor.AQUA));

            for (String effect : effects) {
                lore.add(Component.text("• " + effect)
                        .color(NamedTextColor.GRAY));
            }
        }

        return GuiItem.display(builder.lore(lore).build());
    }

    /**
     * 직업 선택 아이템 생성
     */
    @NotNull
    public static GuiItem createJobItem(@NotNull JobSelectionGui gui, @NotNull String jobKey,
                                        @NotNull Material material, @NotNull com.febrie.rpg.util.LangManager langManager,
                                        @NotNull Player player, @NotNull RPGPlayerManager playerManager) {
        ItemBuilder builder = ItemBuilder.of(material)
                .displayName(com.febrie.rpg.util.LangManager.getMessage(player, "job." + jobKey + ".name"))
                .flags(ItemFlag.values());

        List<Component> lore = new ArrayList<>();
        lore.addAll(com.febrie.rpg.util.LangManager.getComponentList(player, "job." + jobKey + ".description"));

        return GuiItem.clickable(
                builder.lore(lore).build(),
                clickPlayer -> {
                    // 직업 선택 로직
                    SoundUtil.playSound(clickPlayer, Sound.UI_BUTTON_CLICK);
                    com.febrie.rpg.util.LangManager.sendMessage(clickPlayer, "job.selected", "job",
                            net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(com.febrie.rpg.util.LangManager.getComponent(clickPlayer, "job." + jobKey + ".name")));
                }
        );
    }

    /**
     * 네비게이션 버튼 생성
     */
    @NotNull
    public static GuiItem createBackButton(@NotNull com.febrie.rpg.util.LangManager langManager, @NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.ARROW)
                        .displayName(com.febrie.rpg.util.LangManager.getMessage(player, "gui.buttons.back.name"))
                        .addLore(com.febrie.rpg.util.LangManager.getMessage(player, "gui.buttons.back.lore"))
                        .build(),
                clickPlayer -> SoundUtil.playSound(clickPlayer, Sound.UI_BUTTON_CLICK)
        );
    }

    @NotNull
    public static GuiItem createRefreshButton(@NotNull com.febrie.rpg.util.LangManager langManager, @NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.EMERALD)
                        .displayName(com.febrie.rpg.util.LangManager.getMessage(player, "gui.buttons.refresh.name"))
                        .addLore(com.febrie.rpg.util.LangManager.getMessage(player, "gui.buttons.refresh.lore"))
                        .build(),
                clickPlayer -> SoundUtil.playSound(clickPlayer, Sound.UI_BUTTON_CLICK)
        );
    }

    @NotNull
    public static GuiItem createCloseButton(@NotNull com.febrie.rpg.util.LangManager langManager, @NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.BARRIER)
                        .displayName(com.febrie.rpg.util.LangManager.getMessage(player, "gui.buttons.close.name"))
                        .addLore(com.febrie.rpg.util.LangManager.getMessage(player, "gui.buttons.close.lore"))
                        .build(),
                clickPlayer -> {
                    SoundUtil.playSound(clickPlayer, Sound.UI_BUTTON_CLICK);
                    clickPlayer.closeInventory();
                }
        );
    }

    /**
     * 빈 슬롯 아이템 생성
     */
    @NotNull
    public static GuiItem createEmptySlot() {
        return GuiItem.display(
                ItemBuilder.of(Material.GRAY_STAINED_GLASS_PANE)
                        .displayName(Component.empty())
                        .build()
        );
    }

    /**
     * 장식용 아이템 생성
     */
    @NotNull
    public static GuiItem createDecoration(@NotNull Material material) {
        return GuiItem.display(
                ItemBuilder.of(material)
                        .displayName(Component.empty())
                        .build()
        );
    }

    /**
     * 슬롯 유효성 검사
     */
    public static boolean isValidSlot(int slot, @NotNull Inventory inventory) {
        return slot >= 0 && slot < inventory.getSize();
    }
}