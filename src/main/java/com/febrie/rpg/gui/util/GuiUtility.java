package com.febrie.rpg.gui.util;

import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.impl.JobSelectionGui;
import com.febrie.rpg.gui.manager.GuiManager;
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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
     * 플레이어 머리 아이템 생성
     */
    @NotNull
    public static ItemStack createPlayerHead(@NotNull Player player, @NotNull LangManager langManager) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        meta.setOwningPlayer(player);
        meta.displayName(langManager.getComponent(player, "gui.profile.player-info.name",
                "player", player.getName()));

        head.setItemMeta(meta);
        return head;
    }

    /**
     * 스탯 정보 아이템 생성
     */
    @NotNull
    public static GuiItem createStatItem(@NotNull RPGPlayer rpgPlayer, @NotNull Stat stat,
                                         @NotNull LangManager langManager) {
        Player player = rpgPlayer.getPlayer();
        if (player == null) {
            return GuiItem.display(new ItemStack(Material.BARRIER));
        }

        int value = rpgPlayer.getStats().getBaseStat(stat);
        int bonus = rpgPlayer.getStats().getBonusStat(stat);

        ItemBuilder builder = ItemBuilder.of(stat.getIcon())
                .displayName(langManager.getComponent(player, "stat." + stat.getId().toLowerCase() + ".name"))
                .flags(ItemFlag.values());

        // 설명 추가
        List<Component> lore = new ArrayList<>();
        lore.addAll(langManager.getComponentList(player, "stat." + stat.getId().toLowerCase() + ".description"));

        lore.add(Component.empty());
        lore.add(Component.text()
                .append(Component.text("현재 값: ", NamedTextColor.GRAY))
                .append(Component.text(value, NamedTextColor.WHITE))
                .build());

        if (bonus > 0) {
            lore.add(Component.text()
                    .append(Component.text("보너스: ", NamedTextColor.GRAY))
                    .append(Component.text("+" + bonus, NamedTextColor.GREEN))
                    .build());
        }

        builder.lore(lore);
        return GuiItem.display(builder.build());
    }

    /**
     * 특성 아이템 생성
     */
    @NotNull
    public static GuiItem createTalentItem(@NotNull RPGPlayer rpgPlayer, @NotNull Talent talent,
                                           @NotNull LangManager langManager) {
        Player player = rpgPlayer.getPlayer();
        if (player == null) {
            return GuiItem.display(new ItemStack(Material.BARRIER));
        }

        int currentLevel = rpgPlayer.getTalents().getTalentLevel(talent);
        int maxLevel = talent.getMaxLevel();
        boolean canLearn = talent.canActivate(rpgPlayer.getTalents());

        Material material = currentLevel > 0 ? Material.ENCHANTED_BOOK : Material.BOOK;

        ItemBuilder builder = ItemBuilder.of(material)
                .displayName(Component.text()
                        .append(langManager.getComponent(player,
                                "talent." + talent.getId() + ".name"))
                        .append(Component.text(" "))
                        .append(Component.text("[" + currentLevel + "/" + maxLevel + "]",
                                currentLevel >= maxLevel ? NamedTextColor.GOLD : NamedTextColor.GRAY))
                        .build())
                .flags(ItemFlag.values());

        List<Component> lore = new ArrayList<>();
        lore.addAll(langManager.getComponentList(player,
                "talent." + talent.getId() + ".description"));

        lore.add(Component.empty());

        // 현재 효과
        if (currentLevel > 0) {
            lore.add(Component.text("현재 효과:", NamedTextColor.YELLOW));
            for (String effect : talent.getEffects()) {
                String effectKey = "talent." + talent.getId() + ".effect." + effect;
                lore.add(Component.text("  • ", NamedTextColor.GRAY)
                        .append(langManager.getComponent(player, effectKey,
                                "level", String.valueOf(currentLevel))));
            }
            lore.add(Component.empty());
        }

        // 필요 포인트
        if (currentLevel < maxLevel) {
            lore.add(Component.text("필요 특성 포인트: " + talent.getRequiredPoints(),
                    canLearn ? NamedTextColor.GREEN : NamedTextColor.RED));
            lore.add(Component.text("보유 특성 포인트: " + rpgPlayer.getTalents().getAvailablePoints(),
                    NamedTextColor.GRAY));
        } else {
            lore.add(Component.text("최대 레벨!", NamedTextColor.GOLD));
        }

        builder.lore(lore);

        if (currentLevel > 0) {
            builder.glint(true);
        }

        return GuiItem.display(builder.build());
    }

    /**
     * 직업 선택 GUI 열기
     */
    public static void openJobSelectionGui(@NotNull Player player, @NotNull GuiManager guiManager,
                                           @NotNull LangManager langManager, @NotNull RPGPlayerManager playerManager) {
        RPGPlayer rpgPlayer = playerManager.getPlayer(player);
        if (rpgPlayer == null) {
            player.sendMessage(Component.text("플레이어 데이터를 불러올 수 없습니다!", NamedTextColor.RED));
            return;
        }

        if (rpgPlayer.hasJob()) {
            player.sendMessage(Component.text("이미 직업을 선택했습니다!", NamedTextColor.RED));
            return;
        }

        JobSelectionGui gui = new JobSelectionGui(guiManager, langManager, player, rpgPlayer);
        guiManager.openGui(player, gui);
    }

    /**
     * 간단한 진행도 바 생성
     */
    @NotNull
    public static Component createProgressBar(double progress, int length,
                                              @NotNull String filledChar, @NotNull String emptyChar,
                                              @NotNull NamedTextColor filledColor,
                                              @NotNull NamedTextColor emptyColor) {
        int filled = (int) (progress * length);
        int empty = length - filled;

        return Component.text()
                .append(Component.text(filledChar.repeat(filled), filledColor))
                .append(Component.text(emptyChar.repeat(empty), emptyColor))
                .build();
    }

    /**
     * 심플한 진행도 바 (기본값 사용)
     */
    @NotNull
    public static Component createSimpleProgressBar(double progress, int length) {
        return createProgressBar(progress, length, "█", "█",
                progress >= 1.0 ? NamedTextColor.GREEN : NamedTextColor.YELLOW,
                NamedTextColor.GRAY
        );
    }

    /**
     * 레벨 진행도 표시 컴포넌트
     */
    @NotNull
    public static Component createLevelProgress(@NotNull RPGPlayer rpgPlayer, @NotNull LangManager langManager) {
        double progress = rpgPlayer.getLevelProgress();
        int level = rpgPlayer.getLevel();
        long currentExp = rpgPlayer.getExperience();
        long requiredExp = rpgPlayer.getRequiredExperience();

        return Component.text()
                .append(Component.text("Level " + level + " ", NamedTextColor.GOLD))
                .append(createSimpleProgressBar(progress, 10))
                .append(Component.text(" " + currentExp + "/" + requiredExp + " EXP", NamedTextColor.GRAY))
                .build();
    }

    /**
     * 에러 메시지와 함께 아이템 생성
     */
    @NotNull
    public static GuiItem createErrorItem(@NotNull Material material, @NotNull Component title,
                                          @NotNull List<Component> errorMessages) {
        ItemBuilder builder = ItemBuilder.of(material)
                .displayName(title)
                .lore(errorMessages)
                .addLore(Component.empty())
                .addLore(Component.text("클릭하여 닫기", NamedTextColor.RED, TextDecoration.ITALIC));

        return GuiItem.clickable(builder.build(), player -> {
            SoundUtil.playSound(player, Sound.UI_BUTTON_CLICK);
            player.closeInventory();
        });
    }
}