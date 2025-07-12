package com.febrie.rpg.util;

import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.impl.JobSelectionGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.player.stat.Stat;
import com.febrie.rpg.talent.Talent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
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
            return GuiItem.empty();
        }

        int value = rpgPlayer.getStats().getStat(stat);
        int bonus = rpgPlayer.getStats().getBonus(stat);

        ItemBuilder builder = ItemBuilder.of(stat.getIcon())
                .displayName(langManager.getComponent(player, "stat." + stat.name().toLowerCase() + ".name"))
                .hideAllFlags();

        // 설명 추가
        List<Component> lore = new ArrayList<>();
        lore.addAll(langManager.getComponentList(player, "stat." + stat.name().toLowerCase() + ".description"));

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
        return GuiItem.empty(builder.build());
    }

    /**
     * 특성 아이템 생성
     */
    @NotNull
    public static GuiItem createTalentItem(@NotNull RPGPlayer rpgPlayer, @NotNull Talent talent,
                                           @NotNull LangManager langManager) {
        Player player = rpgPlayer.getPlayer();
        if (player == null) {
            return GuiItem.empty();
        }

        int currentLevel = rpgPlayer.getTalents().getTalentLevel(talent);
        int maxLevel = talent.getMaxLevel();
        boolean canLearn = rpgPlayer.getTalents().canLearnTalent(talent);

        Material material = currentLevel > 0 ? Material.ENCHANTED_BOOK : Material.BOOK;

        ItemBuilder builder = ItemBuilder.of(material)
                .displayName(Component.text()
                        .append(langManager.getComponent(player,
                                "talent." + talent.getId() + ".name"))
                        .append(Component.text(" "))
                        .append(Component.text("[" + currentLevel + "/" + maxLevel + "]",
                                currentLevel >= maxLevel ? NamedTextColor.GOLD : NamedTextColor.GRAY))
                        .build())
                .hideAllFlags();

        List<Component> lore = new ArrayList<>();
        lore.addAll(langManager.getComponentList(player,
                "talent." + talent.getId() + ".description"));

        // 현재 효과
        if (currentLevel > 0) {
            lore.add(Component.empty());
            lore.add(Component.text("현재 효과:", NamedTextColor.YELLOW));
            lore.addAll(talent.getEffectDescription(currentLevel));
        }

        // 다음 레벨 효과
        if (currentLevel < maxLevel) {
            lore.add(Component.empty());
            lore.add(Component.text("다음 레벨:", NamedTextColor.AQUA));
            lore.addAll(talent.getEffectDescription(currentLevel + 1));
        }

        // 요구사항
        lore.add(Component.empty());
        if (canLearn) {
            lore.add(Component.text("클릭하여 학습", NamedTextColor.GREEN, TextDecoration.ITALIC));
        } else {
            lore.add(Component.text("요구사항 미충족", NamedTextColor.RED, TextDecoration.ITALIC));
        }

        builder.lore(lore);

        return GuiItem.clickable(
                builder.build(),
                p -> {
                    if (canLearn && rpgPlayer.getTalents().learnTalent(talent)) {
                        SoundUtil.playSound(p, Sound.ENTITY_PLAYER_LEVELUP);
                        langManager.sendMessage(p, "messages.talent.learned",
                                "talent", langManager.getMessage(p,
                                        "talent." + talent.getId() + ".name"));
                    } else {
                        SoundUtil.playSound(p, Sound.ENTITY_VILLAGER_NO);
                    }
                }
        );
    }

    // ========== GuiService에서 이동한 기능들 ==========

    /**
     * 직업 선택 필요 체크
     */
    public static boolean requiresJobSelection(@NotNull Player player,
                                               @NotNull RPGPlayerManager playerManager,
                                               @NotNull GuiManager guiManager,
                                               @NotNull LangManager langManager) {
        RPGPlayer rpgPlayer = playerManager.getPlayer(player);
        if (rpgPlayer == null) {
            rpgPlayer = playerManager.getOrCreatePlayer(player);
        }

        if (!rpgPlayer.hasJob()) {
            // 직업 선택 GUI로 이동
            guiManager.openGui(player, new JobSelectionGui(guiManager, langManager, player, rpgPlayer));
            SoundUtil.playSound(player, Sound.ENTITY_VILLAGER_NO);
            langManager.sendMessage(player, "messages.no-job");
            return true;
        }
        return false;
    }

    /**
     * 진행도 바 생성
     */
    @NotNull
    public static Component createProgressBar(double progress, int length,
                                              @NotNull NamedTextColor filledColor,
                                              @NotNull NamedTextColor emptyColor) {
        int filledLength = (int) (progress * length);
        char filled = '■';
        char empty = '□';

        StringBuilder filledPart = new StringBuilder();
        StringBuilder emptyPart = new StringBuilder();

        // 채워진 부분
        for (int i = 0; i < filledLength; i++) {
            filledPart.append(filled);
        }

        // 빈 부분
        for (int i = filledLength; i < length; i++) {
            emptyPart.append(empty);
        }

        return Component.text()
                .append(Component.text(filledPart.toString(), filledColor))
                .append(Component.text(emptyPart.toString(), emptyColor))
                .build();
    }

    /**
     * 간단한 진행도 바 생성
     */
    @NotNull
    public static Component createSimpleProgressBar(double progress, int length) {
        return createProgressBar(progress, length,
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
                .addLoreLines(Component.empty())
                .addLore(Component.text("클릭하여 닫기", NamedTextColor.RED, TextDecoration.ITALIC));

        return GuiItem.clickable(builder.build(), player -> {
            SoundUtil.playSound(player, Sound.UI_BUTTON_CLICK);
            player.closeInventory();
        });
    }
}