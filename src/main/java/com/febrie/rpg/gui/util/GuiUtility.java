package com.febrie.rpg.gui.util;

import com.febrie.rpg.util.lang.GuiLangKey;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.impl.job.JobSelectionGui;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.stat.Stat;
import com.febrie.rpg.talent.Talent;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.SoundUtil;
import com.febrie.rpg.util.LangManager;
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
 * GUI 관???�틸리티 ?�래?? * ItemBuilder�??�용??GUI ?�이???�성 ?�우�? * GuiService 기능 ?�합
 *
 * @author Febrie, CoffeeTory
 */
public class GuiUtility {

    private GuiUtility() {
        throw new UnsupportedOperationException("?�틸리티 ?�래?�는 ?�스?�스?�할 ???�습?�다.");
    }

    /**
     * GUI ?�이???�정 - ?�심 메소??     * Map�?Inventory ?�시 ?�데?�트
     */
    public static void setItem(int slot, @NotNull GuiItem item,
                               @NotNull Map<Integer, GuiItem> items,
                               @NotNull Inventory inventory) {
        if (slot < 0 || slot >= inventory.getSize()) {
            return; // ?�못???�롯 번호 무시
        }

        items.put(slot, item);
        inventory.setItem(slot, item.getItemStack());
    }

    /**
     * ?�러 ?�롯??같�? ?�이???�정
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
     * ?�정 ?�에 ?�이???�정
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
     * ?�정 ?�에 ?�이???�정
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
     * ?�레?�어 머리 ?�이???�성
     */
    @NotNull
    public static ItemStack createPlayerHead(@NotNull Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        meta.setOwningPlayer(player);
        meta.displayName(LangManager.text(GuiLangKey.GUI_PROFILE_PLAYER_INFO_NAME, player, Component.text(player.getName())));

        head.setItemMeta(meta);
        return head;
    }

    /**
     * ?�탯 ?�보 ?�이???�성
     */
    @NotNull
    public static GuiItem createStatItem(@NotNull RPGPlayer rpgPlayer, @NotNull Stat stat) {
        Player player = rpgPlayer.getPlayer();
        if (player == null) {
            return GuiItem.display(new ItemStack(Material.BARRIER));
        }

        int value = rpgPlayer.getStats().getBaseStat(stat);
        int bonus = rpgPlayer.getStats().getBonusStat(stat);

        ItemBuilder builder = ItemBuilder.of(stat.getIcon())
                .displayName(Component.text(stat.getId()))
                .flags(ItemFlag.values());

        // ?�명 추�?
        List<Component> lore = new ArrayList<>();
        // Description from translation key
        lore.add(Component.text("Stat: " + stat.getId()));

        // ?�재 �??�시
        lore.add(Component.empty());
        lore.add(Component.text("?�재 �? ")
                .color(NamedTextColor.GRAY)
                .append(Component.text(String.valueOf(value))
                        .color(NamedTextColor.WHITE)
                        .decoration(TextDecoration.BOLD, true)));

        // 보너???�시 (?�는 경우)
        if (bonus > 0) {
            lore.add(Component.text("보너?? ")
                    .color(NamedTextColor.GRAY)
                    .append(Component.text("+" + bonus)
                            .color(NamedTextColor.GREEN)
                            .decoration(TextDecoration.BOLD, true)));
        }

        return GuiItem.display(builder.lore(lore).build());
    }

    /**
     * ?�성 ?�이???�성
     */
    @NotNull
    public static GuiItem createTalentItem(@NotNull Talent talent, @NotNull RPGPlayer rpgPlayer) {
        Player player = rpgPlayer.getPlayer();
        if (player == null) {
            return GuiItem.display(new ItemStack(Material.BARRIER));
        }

        int currentLevel = rpgPlayer.getTalents().getTalentLevel(talent);
        boolean isLearned = currentLevel > 0;
        boolean canLearn = talent.canActivate(rpgPlayer.getTalents());

        Material material = talent.getIcon();
        ItemBuilder builder = ItemBuilder.of(material)
                .displayName(Component.text(talent.getId()))
                .flags(ItemFlag.values());

        // ?�명 추�?
        List<Component> lore = new ArrayList<>();
        // Description from translation key
        lore.add(Component.text("Talent: " + talent.getId()));

        // ?�재 ?�벨 ?�시
        lore.add(Component.empty());
        lore.add(Component.text("?�벨: " + currentLevel + "/" + talent.getMaxLevel())
                .color(NamedTextColor.WHITE));

        // ?�태 ?�시
        if (currentLevel >= talent.getMaxLevel()) {
            lore.add(Component.text("Max Level")
                    .color(NamedTextColor.GREEN)
                    .decoration(TextDecoration.BOLD, true));
        } else if (canLearn) {
            lore.add(Component.text("Click to level up")
                    .color(NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, true));
        } else {
            lore.add(Component.text("Cannot level up")
                    .color(NamedTextColor.RED)
                    .decoration(TextDecoration.BOLD, true));
        }

        // ?�요 조건 ?�시 (?�행 ?�성)
        Map<Talent, Integer> prerequisites = talent.getPrerequisites();
        if (!prerequisites.isEmpty()) {
            lore.add(Component.empty());
            lore.add(Component.text("?�요 조건:")
                    .color(NamedTextColor.GRAY));

            for (Map.Entry<Talent, Integer> entry : prerequisites.entrySet()) {
                Talent prereqTalent = entry.getKey();
                int requiredLevel = entry.getValue();
                int playerLevel = rpgPlayer.getTalents().getTalentLevel(prereqTalent);
                boolean meets = playerLevel >= requiredLevel;

                Component prereqName = Component.text(prereqTalent.getId());
                lore.add(Component.text("??")
                        .append(prereqName)
                        .append(Component.text(" Lv." + requiredLevel))
                        .color(meets ? NamedTextColor.GREEN : NamedTextColor.RED));
            }
        }

        // ?�수 ?�과 ?�시
        List<String> effects = talent.getEffects();
        if (!effects.isEmpty()) {
            lore.add(Component.empty());
            lore.add(Component.text("?�과:")
                    .color(NamedTextColor.AQUA));

            for (String effect : effects) {
                lore.add(Component.text("??" + effect)
                        .color(NamedTextColor.GRAY));
            }
        }

        return GuiItem.display(builder.lore(lore).build());
    }

    /**
     * 직업 ?�택 ?�이???�성
     */
    @NotNull
    public static GuiItem createJobItem(@NotNull JobSelectionGui gui, @NotNull String jobKey,
                                        @NotNull Material material,
                                        @NotNull Player player, @NotNull RPGPlayerManager playerManager) {
        ItemBuilder builder = ItemBuilder.of(material)
                .displayName(Component.text(jobKey))
                .flags(ItemFlag.values());

        List<Component> lore = new ArrayList<>();
        // Description from translation key
        lore.add(Component.text("Job: " + jobKey));

        return GuiItem.clickable(
                builder.lore(lore).build(),
                clickPlayer -> {
                    // 직업 ?�택 로직
                    SoundUtil.playSound(clickPlayer, Sound.UI_BUTTON_CLICK);
                    clickPlayer.sendMessage(Component.text("Job selected: " + jobKey));
                }
        );
    }

    /**
     * ?�비게이??버튼 ?�성
     */
    @NotNull
    public static GuiItem createBackButton(@NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.ARROW)
                        .displayName(LangManager.text(GuiLangKey.GUI_BUTTONS_BACK_NAME, player))
                        .addLore(LangManager.text(GuiLangKey.GUI_BUTTONS_BACK_LORE, player))
                        .build(),
                clickPlayer -> SoundUtil.playSound(clickPlayer, Sound.UI_BUTTON_CLICK)
        );
    }

    @NotNull
    public static GuiItem createRefreshButton(@NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.EMERALD)
                        .displayName(LangManager.text(GuiLangKey.GUI_BUTTONS_REFRESH_NAME, player))
                        .addLore(LangManager.text(GuiLangKey.GUI_BUTTONS_REFRESH_LORE, player))
                        .build(),
                clickPlayer -> SoundUtil.playSound(clickPlayer, Sound.UI_BUTTON_CLICK)
        );
    }

    @NotNull
    public static GuiItem createCloseButton(@NotNull Player player) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.BARRIER)
                        .displayName(LangManager.text(GuiLangKey.GUI_BUTTONS_CLOSE_NAME, player))
                        .addLore(LangManager.list(GuiLangKey.GUI_BUTTONS_CLOSE_LORE, player))
                        .build(),
                clickPlayer -> {
                    SoundUtil.playSound(clickPlayer, Sound.UI_BUTTON_CLICK);
                    clickPlayer.closeInventory();
                }
        );
    }

    /**
     * �??�롯 ?�이???�성
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
     * ?�식???�이???�성
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
     * ?�롯 ?�효??검??     */
    public static boolean isValidSlot(int slot, @NotNull Inventory inventory) {
        return slot >= 0 && slot < inventory.getSize();
    }
}
